package core.objects

import java.io.File

import core.repository.Repository
import utils.io.{IO, SgitIO}

case class Commit(sha: String, commitMessage: String, parent: String, content: List[String]) {

}

object Commit {

  // Name of the file where is stored the last commit message
  val nameFile = "COMMIT_EDITMSG"

  /**
   * Function to create a Commit.
   *
   * @param repository    Repository
   * @param messageCommit the Commit message
   * @param treeIndex     Index in a Map format
   * @return message in String format
   */
  def commit(repository: Repository, messageCommit: String, treeIndex: Map[String, Any]): String = {
    Tree.writeTree(repository, Map(Repository.getRepoName(repository) -> treeIndex)) match {
      case Left(error) => error
      case Right(lastTree) =>
        if (Repository.isFirstCommit(repository))
          IO.createFile(repository.pathRepo, nameFile, messageCommit)
        else
          IO.writeInFile(IO.buildPath(List(repository.pathRepo, nameFile)), messageCommit, append = false)

        val pathToRefHeads = Repository.pathToRefsHead(repository)
        val currentBranch = repository.currentBranch
        val shaTree = lastTree.head

        // We get the sha1 of the last commit if there is one (nil if there's not) and we create a the commit file
        if (!Repository.isFirstCommit(repository)) {
          val commitContent = generateCommitContent(IO.listToString(lastTree), currentBranch.commit, messageCommit)
          val shaCommit = SgitIO.sha(commitContent)
          Object.createObject(repository, shaCommit, commitContent)
          // We update the ref to the new commit tree
          updateCommitBranch(repository, shaCommit)
          "File(s) committed."
        }
        else {
          IO.createFile(pathToRefHeads, currentBranch.branchName, "")
          val commitContent = generateCommitContent(shaTree, "nil", messageCommit)
          val shaCommit = SgitIO.sha(commitContent)
          Object.createObject(repository, shaCommit, commitContent)
          // We update the ref to the new commit tree
          updateCommitBranch(repository, shaCommit)
          "File(s) committed."
        }
    }
  }

  /**
   * Function to generate the Commit file content.
   *
   * @param treeSha   the sha1 to the new tree Commit
   * @param parentSha the sha1 to the old Commit file
   * @param message   the commit message
   * @return the Commit file content in String format
   */
  private def generateCommitContent(treeSha: String, parentSha: String, message: String): String = {
    treeSha + "\n" + "parent " + parentSha + "\n" + "message " + message + "\n"
  }

  /**
   * Function to update the ref to the last commit.
   *
   * @param repository Repository
   * @param newSha     the sha to the new commit file
   */
  def updateCommitBranch(repository: Repository, newSha: String): Unit = {
    IO.writeInFile(IO.buildPath(List(Repository.pathToRefsHead(repository), repository.currentBranch.branchName)), newSha, append = false)
  }

  /**
   * Function to get the files and their hash of the last commit in a Map format
   *
   * @param repository Repository
   * @param shaCommit  the sha of the commit
   * @return Either left: error message, Either right: Map of the paths of the files with their sha1
   */
  def commitToMap(repository: Repository, shaCommit: String): Either[String, Map[String, Any]] = {
    val pathCommit = Object.getObjectFilePath(repository, shaCommit)
        IO.readContentFile(pathCommit) match {
          case Left(error) => Left(error)
          case Right(contentCommit) =>

            val listBlob = contentCommit.map(x => x.split(" ", 3))
              .filter(x => x(0) == "blob")
              .map(x => x(2) -> x(1))
              .toMap

            val listTree = contentCommit.map(x => x.split(" ", 3))
              .filter(x => x(0) == "tree")
              .map(x => x(2) -> commitToMap(repository, x(1)).getOrElse(Map()))
              .toMap

            Right(listBlob ++ listTree)
        }
  }

  /**
   * Function to get the files and their hash of the last commit in a List format
   *
   * @param commit Commit in a Map format
   * @return Either left: error message, Either right: List of the paths of the files with their sha1
   */
  def commitToList(repository: Repository, commit: Map[String, Any]): List[String] = {
    commitMapToListRec(commit).map(x => x.replaceFirst(Repository.getRepoName(repository) + IO.getRegexFileSeparator, ""))
  }

  /**
   * Function to convert the Commit in Map format to a List format.
   *
   * @param mapCommit the Commit in Map format
   * @return the Commit in a List format
   */
  private def commitMapToListRec(mapCommit: Map[String, Any]): List[String] = {
    var list = List[String]()
    mapCommit.foreach(x => {
      var path = List[String]()
      if (x._1.contains(".txt") || (x._2.isInstanceOf[String] && x._2.asInstanceOf[String].length == 40)) {
        list = x._1 + " " + x._2 :: list
      } else {
        path = path ::: commitMapToListRec(x._2.asInstanceOf[Map[String, Any]]).map(p => x._1 + File.separator + p)
        list = path ::: list
      }
    })
    list
  }

  /**
   * Function to get the content of a specific Commit.
   *
   * @param sha the sha of the Commit.
   * @return Either left: error message, Either right: the Commit content.
   */
  def getCommit(repository: Repository, sha: String): Either[String, List[String]] = {
    val path = Object.getObjectFilePath(repository, sha)
    IO.readContentFile(path) match {
      case Left(error) => Left(error)
      case Right(value) => Right(value)
    }
  }

  /**
   * Function to get the List of Commits.
   *
   * @param repository Repository
   * @return List of Commit
   */
  def getListCommit(repository: Repository): Either[String, List[Commit]] = {
    repository.commit match {
      case Left(error) => Left(error)
      case Right(value) =>
        if (value != null) {
          getListCommitRec(repository, value)
        }
        else
          Right(List())
    }
  }

  /**
   * Function to get the List of Commits.
   *
   * @param repository Repository
   * @param commit     First Commit
   *                   Either left: error message, Either right: List of Commit
   */
  def getListCommitRec(repository: Repository, commit: Commit): Either[String, List[Commit]] = {
    if (commit.parent == "nil") {
      Right(List(commit))
    } else {
      getCommit(repository, commit.parent) match {
        case Left(error) => Left(error)
        case Right(value) =>
          // We get the infos to create the Commit object
          val commitNew = value.map(x => x.split("\n")).dropWhile(!_ (0).contains("message")).flatten
          val parent = value.map(x => x.split(" ")).filter(_ (0) == "parent").flatten
          val content = value.map(x => x.split(" ")).filter(x => x(0) == "tree" || x(0) == "blob").flatten
          val commitParent = Commit(commit.parent, IO.listToString(commitNew).replace("message", ""), parent.tail.head.replace("parent", ""), content)
          Right(getListCommitRec(repository, commitParent).getOrElse(List()) ::: List(commit))
      }
    }
  }

}
