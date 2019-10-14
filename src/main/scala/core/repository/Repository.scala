package core.repository

import java.io.File

import utils.io.IO

import scala.annotation.tailrec

object Repository {

  val listDir: Array[String] = Array(IO.buildPath(List("refs", "head")), IO.buildPath(List("refs", "tags")), "objects", "branches")

  /**
   * Function that initialize the .sgit repository.
   *
   * @param path where we want to create the new repository
   */
  def createRepository(path: String): Unit = {
    val repoPath = IO.buildPath(List(path, getSgitName))
    IO.createDirectory(path, getSgitName)
    listDir.foreach(x => IO.createDirectory(repoPath, x))
    IO.createFile(repoPath, "description", "Unnamed repository, edit this file 'description' to name the repository.\n")
    IO.createFile(repoPath, "HEAD", "ref: " + IO.buildPath(List("refs", "head", "master")) + "\n")
    IO.createFile(repoPath, "index", "")
  }

  /**
   * Function to get the name of the directory where are stored the system files.
   *
   * @return the name in String format
   */
  def getSgitName: String = {
    ".sgit"
  }

  /**
   * Function to get the name of the working directory.
   *
   * @return Either left: error message, Either right: the name in String format
   */
  def getRepoName: Either[String, String] = {
    getPathToParenSgit match {
      case Left(error) => Left(error)
      case Right(result) => Right(result.split(IO.getRegexFileSeparator).last)
    }
  }

  /**
   * Function to know if a file is in the repository.
   *
   * @param filePath the file path in String format
   * @return true is it's in else false
   */
  def isFileInRepo(filePath: String): Boolean = {
    getPathToParenSgit match {
      case Left(_) =>
        println("File " + filePath + " is not in a Sgit repository !")
        false
      case Right(result) =>
        if (filePath.contains(result) && new File(filePath).exists()) {
          true
        } else {
          new File(IO.buildPath(List(result, filePath))).exists()
        }
    }
  }

  /**
   * Function to find the .sgit in the repository.
   *
   * @param path current path
   * @return Either left: error message, Either right: the path in String format to the .sgit
   */
  @tailrec
  def getRepositoryPath(path: String = IO.getCurrentPath): Either[String, String] = {
    val file = new File(IO.buildPath(List(path, getSgitName)))
    val index = new File(IO.buildPath(List(file.getAbsolutePath, "index")))
    val head = new File(IO.buildPath(List(file.getAbsolutePath, "HEAD")))
    val objects = new File(IO.buildPath(List(file.getAbsolutePath, "objects")))
    val refs = new File(IO.buildPath(List(file.getAbsolutePath, "refs")))

    if (file.exists() && index.exists() && head.exists() && objects.exists() && refs.exists()) {
      Right(file.getAbsolutePath)
    } else if (file.getParent == "null" || file.getParent == null) {
      Left("This is not a Sgit repository. You should use 'sgit init'.\n")
    } else {
      getRepositoryPath(new File(path).getParent)
    }
  }

  /**
   * Function to get the path to the objects folder in .sgit
   *
   * @return Either left: error message, Either right: the path in String format to the objects folder
   */
  def getPathToObject: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, "objects")))
    }
  }

  /**
   * Function to get the path to the INDEX file in .sgit
   *
   * @return Either left: error message, Either right: the path in String format to the INDEX file
   */
  def getPathToIndex: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, "index")))
    }
  }

  /**
   * Function to get the path to the HEAD file in .sgit
   *
   * @return Either left: error message, Either right: the path in String format to the HEAD file
   */
  def getPathToHead: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, "HEAD")))
    }
  }

  /**
   * Function to get the path to the refs/head file in .sgit
   *
   * @return Either left: error message, Either right: the path in String format to the refs/head file
   */
  def getPathToRefHeads: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, "refs", "head")))
    }
  }

  /**
   * Function to get the path to the tags folder in .sgit
   *
   * @return Either left: error message, Either right: the path in String format to the tags folder
   */
  def getPathToRefTags: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, "refs", "tags")))
    }
  }

  /**
   * Function to get the absolute path of a file in the repository.
   *
   * @param pathFile relative path of the file in the repository
   * @return Either left: error message, Either right: the absolute path in a String format
   */
  def getAbsolutePathInRepo(pathFile: String): Either[String, String] = {
    getPathToParenSgit match {
      case Left(error) => Left(error)
      case Right(value) => Right(IO.buildPath(List(value, pathFile)))
    }
  }

  /**
   * Function to get the path to the parent of the Sgit repository.
   *
   * @return Either left: error message, Either right: the path to the parent in a String format
   */
  def getPathToParenSgit: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(path) => Right(new File(path).getParent)
    }
  }

}
