package core.commands

import core.objects.Blob.treatBlob
import utils.io.SgitIO

object Add {
  /**
   * Function to create the blobs in the object folder.
   * @param filesAdd files to add in the current stage
   */
  def add(filesAdd: List[String]): Unit = {
    SgitIO.getRepositoryPath() match {
      case Left(error) => print(error)
      case Right(result) => filesAdd.foreach(x => treatBlob(x, result))
    }
  }
}
