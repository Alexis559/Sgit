package core.commands

import java.io.File

import core.objects.Blob
import core.repository.Repository
import utils.io.{IO, SgitIO}
import utils.parser.Printer

object AddCmd {
  /**
   * Function to create the blobs in the object folder.
   *
   * @param filesAdd files to add in the current stage
   */
  def add(filesAdd: List[String]): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(result) =>
        if (filesAdd.isEmpty) {
          Printer.displayln("Nothing specified, nothing added.\nMaybe you wanted to say 'sgit add .'?")
        }
        else if (filesAdd.head == ".") {
          if (!IO.getCurrentPath.contains(result))
          // We add all the files recursively in the current directory and subdirectory
            Blob.treatBlob(SgitIO.listFiles())
          else
            Printer.displayln("You can't do this action here.")
        } else {
          // We get the Canonical path to avoid relative paths
          val filesPathCanonical = filesAdd.map(x => new File(x).getCanonicalPath)
          Blob.treatBlob(filesPathCanonical.filter(x => Repository.isFileInRepo(x) && new File(x).isFile))
        }
    }
  }
}
