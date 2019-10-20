package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}

object Object{

  /**
   * Function to get the file of an object from his hash.
   *
   * @param repository Repository
   * @param shaObject  the hash of the object we want to get
   * @return the path to the file of the object in String format
   */
  def getObjectFilePath(repository: Repository, shaObject: String): String = {
    val pathObject = getObjectPath(repository, shaObject)
    IO.buildPath(List(pathObject, shaObject.substring(2)))
  }

  /**
   * Function to get the folder of an object from his hash.
   *
   * @param repository Repository
   * @param shaObject  the hash of the object we want to get
   * @return the path to the folder of the object in String format
   */
  def getObjectPath(repository: Repository, shaObject: String): String = {
    IO.buildPath(List(Repository.pathToObjects(repository), shaObject.substring(0, 2)))
  }

  /**
   * Function to create the tree and blob files in the objects directory in .sgit.
   *
   * @param repository  Repository
   * @param sha         the hash of the file we want to create
   * @param textContent the content to write in the file.
   */
  def createObject(repository: Repository, sha: String, textContent: String): Unit = {
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    val pathObjects = Repository.pathToObjects(repository)
    IO.createDirectory(pathObjects, dirName)
    IO.createFile(IO.buildPath(List(pathObjects, dirName)), fileName, textContent)
  }

  /**
   * Function to get the sha of an existing file.
   *
   * @param pathFile the path to the file
   * @return Either left: error message, Either right: the sha1 in a String format
   */
  def returnNewSha(pathFile: String): Either[String, String] = {
    IO.readContentFile(pathFile) match {
      case Left(error) => Left(error)
      case Right(result) =>
        Right(SgitIO.sha(IO.listToString(result)))
    }
  }
}
