package core.repository

import core.objects.{BlobIndex, Object}
import utils.io.IO

object Index {

  /**
   * Function to update the Index.
   *
   * @param repository Repository
   * @param index      Index in List of BlobIndex format
   * @return the Index updated in List of BlobIndex format
   */
  def updateIndex(repository: Repository, index: List[BlobIndex]): List[BlobIndex] = {
    var newIndex = List[BlobIndex]()
    // We check if the files in the index are always present in the repository
    val filesExisting = index.filter(file => Repository.isFileInRepo(repository, file.fileName))
    newIndex = filesExisting.map(x => {
      // If they always exist then we check for the content update (new hash)
      BlobIndex(x.fileName, Object.returnNewSha(Repository.getPathInRepo(repository, x.fileName)).getOrElse(""))
    })
    newIndex
  }

  /**
   * Function to add files to the Index.
   *
   * @param index Index in List of BlobIndex format
   * @param files files in List of BlobIndex format
   * @return Index with the files added
   */
  def addFilesToIndex(index: List[BlobIndex], files: List[BlobIndex]): List[BlobIndex] = {
    val indexFiles = index.map(_.fileName)
    val filesToAdd = files.filterNot(x => indexFiles.contains(x.fileName))

    index ::: filesToAdd
  }


  /**
   * Function to get the index in a List of Map format.
   *
   * @param repository Repository
   * @return Either left: error message, Either right: the Index in List of BlobIndex format
   */
  def getIndex(repository: Repository): Either[String, List[BlobIndex]] = {
    val pathIndex = Repository.pathToIndex(repository)
    IO.readContentFile(pathIndex) match {
      case Left(error) => Left(error)
      case Right(result) =>
        val index = indexBlobs(result)
        Right(index)
    }
  }

  /**
   * Function to get the index in a List of BlobIndex format.
   *
   * @param content the content of the Index in a List of String format
   * @return Index in List of BlobIndex format
   */
  def indexBlobs(content: List[String]): List[BlobIndex] = {
    val listMap = content.map(x => {
      val line = x.split(" ")
      BlobIndex(line(1), line(0))
    })
    listMap
  }

  /**
   * Function to write in the index file
   *
   * @param repository Repository
   * @param newIndex   Index in List of BlobIndex format
   * @return message in String format
   */
  def writeIndex(repository: Repository, newIndex: List[BlobIndex]): String = {
    val textContent = newIndex.map(x => {
      x.sha + " " + x.fileName + "\n"
    })
    val pathIndex = Repository.pathToIndex(repository)
    IO.writeInFile(pathIndex, IO.listToString(textContent), append = false)
    "File(s) added."
  }

  /**
   * Function to get the List of files that are untracked by the Index.
   *
   * @param repository Repository
   * @param index      Index in List of BlobIndex format
   * @return Either left: error message, Either right: the List of files paths in a String format
   */
  def getTrackedFiles(repository: Repository, index: List[BlobIndex]): Either[String, List[String]] = {
    Right(index.map(x => x.fileName))
  }
}
