package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}

// Blob used only for the add
case class BlobFile(fileName: String, content: String) {
  val sha: String = SgitIO.sha(content)
}

case class BlobIndex(fileName: String, sha: String)

object Blob {
  /**
   * Function to get SHA-1 checksum for the files to create the folders and the files.
   *
   * @param repository Repository
   * @param files      path to the file we want to add
   * @return List of BlobIndex
   */
  def treatBlob(repository: Repository, files: List[BlobFile]): List[BlobIndex] = {
    val filesToAdd: List[BlobIndex] = files
      .map(x => {
        Object.createObject(repository, x.sha, x.content)
        BlobIndex(IO.cleanPathFile(repository, x.fileName).getOrElse(""), x.sha)
      })
    filesToAdd
  }

  /**
   * Function to transform a file to a BlobFile
   *
   * @param file the file we want to transform
   * @return Either left: error message, Either right: the BlobFile
   */
  def transformToBlobFile(file: String): Either[String, BlobFile] = {
    IO.readContentFile(file) match {
      case Left(error) => Left(error)
      case Right(value) => Right(BlobFile(file, IO.listToString(value)))
    }
  }

}
