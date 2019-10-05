package core.objects

import java.io.File
import utils.io.IO

object Blob {

  /**
   * Function to create the blobs in the object folder.
   * @param filesAdd files to add in the current stage
   */
  def createBlob(filesAdd: List[String]): Unit = {
    val repDirectory = IO.getRepositoryPath()
    if(repDirectory == null)
      throw new Exception("This is not a Sgit repository. You should use 'sgit init'.")

    filesAdd.foreach(x => treatBlob(x, repDirectory))
  }

  /**
   * Function to get SHA-256 checksum for the files to create the folders and the files.
   * @param file path to the file
   * @param repDirectory path to the .sgit
   */
  def treatBlob(file: String, repDirectory: String): Unit = {
    val textContent = IO.readContentFile(file)
    val shaContent = IO.sha(textContent)
    val dirName = shaContent.substring(0, 2)
    val fileName = shaContent.substring(3)
    val pathObject = IO.getPathToObject
    val pathIndex = IO.getPathToIndex
    IO.createDirectory(pathObject, dirName)
    IO.createFile(IO.buildPath(List(pathObject, dirName)), fileName, textContent)
    IO.writeInFile(pathIndex, shaContent + " " + IO.getPathFile(file) + "\n")
  }
}
