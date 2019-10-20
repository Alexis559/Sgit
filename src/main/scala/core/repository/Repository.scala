package core.repository

import java.io.File

import core.objects.{BlobIndex, Branch, Commit, Object, Tag}
import utils.io.{IO, SgitIO}

import scala.annotation.tailrec

case class Repository(pathRepo: String, currentBranch: Branch) {

  // Lazy val of the Index
  lazy val index: Either[String, List[BlobIndex]] = {
    IO.readContentFile(indexPath) match {
      case Left(error) => Left(error)
      case Right(result) =>
        val index = result.map(_.split(" ", 2)).map(x => BlobIndex(x(1), x(0)))
        Right(index)
    }
  }
  // Lazy val of the Branches
  lazy val branches: List[Branch] = {
    SgitIO.listFiles(branchPath).map(x => Branch(x.split(IO.getRegexFileSeparator).last, IO.readContentFile(x).getOrElse(List()).head))
  }
  // Lazy val of the Tags
  lazy val tags: List[Tag] = {
    SgitIO.listFiles(tagPath).map(x => Tag(x.split(IO.getRegexFileSeparator).last, IO.readContentFile(x).getOrElse(List()).head))
  }
  // Lazy val of the last Commit
  lazy val commit: Either[String, Commit] = {
    IO.readContentFile(IO.buildPath(List(pathRepo, "refs", "head", currentBranch.branchName))) match {
      case Left(error) => Left(error)
      case Right(sha) =>
        if (sha.head != "nil") {
          IO.readContentFile(Object.getObjectFilePath(Repository.apply(pathRepo, null), sha.head)) match {
            case Left(error) => Left(error)
            case Right(value) =>
              val commit = value.map(x => x.split("\n")).dropWhile(!_ (0).contains("message")).flatten
              val parent = value.map(x => x.split(" ")).filter(_ (0) == "parent").flatten
              Right(Commit(sha.head, IO.listToString(commit).replace("message", ""), IO.listToString(parent).replace("parent", ""), value))
          }
        } else {
          Right(null)
        }
    }
  }

  // Some paths
  val indexPath: String = IO.buildPath(List(pathRepo, "index"))
  val branchPath: String = IO.buildPath(List(pathRepo, "refs", "head"))
  val tagPath: String = IO.buildPath(List(pathRepo, "refs", "tags"))
  val objectsPath: String = IO.buildPath(List(pathRepo, "objects"))
}

object Repository {

  def getSgit: String = {
    ".sgit"
  }

  def getRepoName(repository: Repository): String = {
    Repository.getRepoPath(repository).split(IO.getRegexFileSeparator).last
  }

  def getCurrentBranch(repository: Repository): Branch = {
    repository.currentBranch
  }

  def pathToIndex(repository: Repository): String = {
    IO.buildPath(List(Repository.getPathSgit(repository), "index"))
  }

  def pathToObjects(repository: Repository): String = {
    IO.buildPath(List(Repository.getPathSgit(repository), "objects"))
  }

  // Path to .sgit
  def getPathSgit(repository: Repository): String = {
    repository.pathRepo
  }

  def pathToRefsHead(repository: Repository): String = {
    IO.buildPath(List(Repository.getPathSgit(repository), "refs", "head"))
  }

  def pathToRefsTags(repository: Repository): String = {
    IO.buildPath(List(Repository.getPathSgit(repository), "refs", "tags"))
  }

  def getPathInRepo(repository: Repository, pathFile: String): String = {
    IO.buildPath(List(Repository.getRepoPath(repository), pathFile))
  }

  // Path to parent of .sgit
  def getRepoPath(repository: Repository): String = {
    new File(repository.pathRepo).getParent
  }

  def pathToHead(repository: Repository): String = {
    IO.buildPath(List(Repository.getPathSgit(repository), "HEAD"))
  }

  def isFirstCommit(repository: Repository): Boolean = {
    repository.currentBranch.commit == "nil"
  }

  /**
   * Function to know if a file is in the repository.
   *
   * @param repository Repository
   * @param filePath   the file path in String format
   * @return true is it's in else false
   */
  def isFileInRepo(repository: Repository, filePath: String): Boolean = {
    (filePath.contains(Repository.getRepoPath(repository)) && new File(filePath).exists()) || new File(IO.buildPath(List(Repository.getRepoPath(repository), filePath))).exists()
  }

}

object ImpureRepository {

  def getCurrentPath: String = {
    System.getProperty("user.dir")
  }

  val listDir: Array[String] = Array(IO.buildPath(List("refs", "head")), IO.buildPath(List("refs", "tags")), "objects")

  /**
   * Function to get the Repository at the path given.
   *
   * @param path a path in String format
   * @return Either left: error message, Either right: the Repository
   */
  def chargeRepo(path: String): Either[String, Repository] = {
    getRepositoryPath(path) match {
      case Left(error) => Left(error)
      case Right(path) =>
        IO.readContentFile(IO.buildPath(List(path, "HEAD"))) match {
          case Left(error) => Left(error)
          case Right(value) =>
            val branch = value.head.split(IO.getRegexFileSeparator).last
            IO.readContentFile(IO.buildPath(List(path, "refs", "head", branch))) match {
              case Left(error) => Left(error)
              case Right(value) =>
                Right(Repository(path, Branch(branch, value.head)))
            }

        }


    }
  }

  /**
   * Function that initialize the .sgit repository.
   *
   * @param path where we want to create the new repository
   */
  def createRepository(path: String): Unit = {
    val repoPath = IO.buildPath(List(path, Repository.getSgit))
    IO.createDirectory(path, Repository.getSgit)
    listDir.foreach(x => IO.createDirectory(repoPath, x))
    IO.createFile(repoPath, "description", "Unnamed repository, edit this file 'description' to name the repository.\n")
    IO.createFile(repoPath, "HEAD", "ref: " + IO.buildPath(List("refs", "head", "master")) + "\n")
    IO.createFile(repoPath, "index", "")
    IO.createFile(IO.buildPath(List(path, ".sgit", "refs", "head")), "master", "nil")
  }

  /**
   * Function to find the .sgit in the Repository.
   *
   * @param path where we want to search for a Repository
   * @return Either left: error message, Either right: the path in String format to the .sgit
   */
  @tailrec
  def getRepositoryPath(path: String = IO.getCurrentPath): Either[String, String] = {
    val file = new File(IO.buildPath(List(path, Repository.getSgit)))
    val index = new File(IO.buildPath(List(file.getAbsolutePath, "index")))
    val head = new File(IO.buildPath(List(file.getAbsolutePath, "HEAD")))
    val objects = new File(IO.buildPath(List(file.getAbsolutePath, "objects")))
    val refs = new File(IO.buildPath(List(file.getAbsolutePath, "refs", "head")))
    val tag = new File(IO.buildPath(List(file.getAbsolutePath, "refs", "tags")))

    if (file.exists() && index.exists() && head.exists() && objects.exists() && refs.exists() && tag.exists()) {
      Right(file.getAbsolutePath)
    } else if (file.getParent == "null" || file.getParent == null) {
      Left("This is not a Sgit repository. You should use 'sgit init'.\n")
    } else {
      getRepositoryPath(new File(path).getParent)
    }
  }
}