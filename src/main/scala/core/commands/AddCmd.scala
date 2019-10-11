package core.commands

import core.objects.Blob
import core.repository.Repository

object AddCmd {
  /**
   * Function to create the blobs in the object folder.
   *
   * @param filesAdd files to add in the current stage
   */
  def add(filesAdd: List[String]): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => print(error)
      case Right(result) => filesAdd.foreach(x => Blob.treatBlob(x))
    }
  }
}
