package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}

object Object{

  /**
   * Function to get the file of an object from his hash.
   *
   * @param shaObject the hash of the object we want to get
   * @return the path to the file of the object in String format
   */
  def getObjectFilePath(shaObject: String): Either[String, String] = {
    getObjectPath(shaObject) match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, shaObject.substring(2))))
    }
  }

  /**
   * Function to get the folder of an object from his hash.
   *
   * @param shaObject the hash of the object we want to get
   * @return the path to the folder of the object in String format
   */
  def getObjectPath(shaObject: String): Either[String, String] = {
    Repository.getPathToObject match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, shaObject.substring(0, 2))))
    }
  }

  /**
   * Function to create the tree and blob files in the objects directory in .sgit.
   *
   * @param sha         the hash of the file we want to create
   * @param textContent the content to write in the file.
   */
  def createObject(sha: String, textContent: String): Either[String, Any] = {
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    Repository.getPathToObject match {
      case Right(result) => {
        IO.createDirectory(result, dirName)
        IO.createFile(IO.buildPath(List(result, dirName)), fileName, textContent)
        Right(null)
      }
      case Left(error) => Left(error)
    }
  }

  def returnNewSha(pathFile: String): Either[String, String] = {
    IO.readContentFile(pathFile) match {
      case Left(error) => Left(error)
      case Right(result) => {
        Right(SgitIO.sha(IO.listToString(result)))
      }
    }
  }
}
