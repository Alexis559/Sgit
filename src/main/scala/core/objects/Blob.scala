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
    IO.readContentFile(file) match {
      case Left(error) => println(error)
      case Right(result) => {
        // We hash the content
        val shaContent = SgitIO.sha(IO.listToString(result))
        // We get the path to the objects folder in .sgit
        Object.createObject(shaContent, IO.listToString(result)) match {
          case Left(error) => print(error)
          case Right(result) => {
            // We get the path to the index file
            Repository.getPathToIndex match {
              case Left(error) => print(error)
              // We create the blobs
              case Right(result) => IO.writeInFile(result, shaContent + " " + IO.cleanPathFile(file).getOrElse("") + "\n", true)
            }
          }
        }
      }
    }
  }
}
