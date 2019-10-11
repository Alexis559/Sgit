package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}

object Blob {

  /**
   * Function to get SHA-1 checksum for the files to create the folders and the files.
   *
   * @param file path to the file we want to add
   */
  def treatBlob(file: String): Unit = {
    // We read the content of the file
    val textContent = IO.readContentFile(file)
    if(textContent.isRight) {
      val fileContent = textContent.right.get
      // We hash the content
      val shaContent = SgitIO.sha(IO.listToString(fileContent))
      // We get the path to the objects folder in .sgit
      Object.createObject(shaContent, IO.listToString(fileContent)) match {
        case Left(error) => print(error)
        case Right(result) => {
          // We get the path to the index file
          Repository.getPathToIndex match {
            case Left(error) => print(error)
            // We create the blobs
            case Right(result) => IO.writeInFile(result, shaContent + " " + IO.getPathFile(file).right.get + "\n", true)
          }
        }
      }
    }else{
      print(textContent.left.get)
    }
  }
}
