package core.objects

import core.repository.Index
import utils.io.{IO, SgitIO}

object Blob {

  /**
   * Function to get SHA-1 checksum for the files to create the folders and the files.
   *
   * @param files path to the file we want to add
   */
  def treatBlob(files: List[String]): Unit = {
    var filesToAdd = List[Map[String, String]]()
    files.foreach(file => {
      // We read the content of the file
      IO.readContentFile(file) match {
        case Left(error) => println(error)
        case Right(result) =>
          // We hash the content
          val shaContent = SgitIO.sha(IO.listToString(result))
          // We get the path to the objects folder in .sgit
          Object.createObject(shaContent, IO.listToString(result)) match {
            case Left(error) => print(error)
            case Right(_) =>
              filesToAdd = Map(IO.cleanPathFile(file).getOrElse("") -> shaContent) :: filesToAdd
          }
      }
    })
    Index.updateIndex(filesToAdd)
  }
}
