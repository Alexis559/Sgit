package core.objects

import utils.io
import utils.io.{IO, SgitIO}

object Blob {

  /**
   * Function to get SHA-256 checksum for the files to create the folders and the files.
   * @param file path to the file
   * @param repDirectory path to the .sgit
   */
  def treatBlob(file: String, repDirectory: String): Unit = {
    val textContent = IO.readContentFile(file)
    if(textContent.isRight) {
      val fileContent = textContent.right.get
      val shaContent = SgitIO.sha(fileContent)

      SgitIO.getPathToObject match {
        case Left(error) => print(error)
        case Right(result) => createBlob(result, shaContent, fileContent)
      }

      SgitIO.getPathToIndex match {
        case Left(error) => print(error)
        case Right(result) => IO.writeInFile(result, shaContent + " " + IO.getPathFile(file).right.get + "\n")
      }
    }else{
      print(textContent.left.get)
    }
  }

  def createBlob(repoDir: String, sha: String, textContent: String): Unit = {
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(3)
    IO.createDirectory(repoDir, dirName)
    IO.createFile(IO.buildPath(List(repoDir, dirName)), fileName, textContent)
  }
}
