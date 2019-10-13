package core.commands

import java.io.File

import core.objects.Blob
import core.repository.Repository
import utils.io.SgitIO

object AddCmd {
  /**
   * Function to create the blobs in the object folder.
   *
   * @param filesAdd files to add in the current stage
   */
  def add(filesAdd: List[String]): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => print(error)
      case Right(_) =>
        if (filesAdd.head == ".")
        // We add all the files recursively in the current directory and subdirectory
          Blob.treatBlob(SgitIO.listFiles())
        else {
          // We get the Canonical path to avoid relative paths
          val filesPathCanonical = filesAdd.map(x => new File(x).getCanonicalPath)
          Blob.treatBlob(filesPathCanonical.filter(x => Repository.isFileInRepo(x) && new File(x).isFile))
        }
    }
  }
}
