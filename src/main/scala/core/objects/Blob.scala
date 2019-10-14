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

    filesToAdd = files.filter(file => IO.readContentFile(file).isRight)
      .map(x => {
        val content = IO.listToString(IO.readContentFile(x).getOrElse(List()))
        val shaContent = SgitIO.sha(content)
        if (Object.createObject(shaContent, content).isRight)
          Map(IO.cleanPathFile(x).getOrElse("") -> shaContent)
        else
          Map(IO.cleanPathFile(x).getOrElse("") -> "")
      })

    Index.updateIndex(filesToAdd)
  }
}
