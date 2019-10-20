package core.commands

import java.io.File

import core.objects.{Blob, BlobFile, BlobIndex}
import core.repository.{ImpureRepository, Index, Repository}
import utils.io.SgitIO

object AddCmd {

  /**
   * Function to create the blobs in the object folder.
   *
   * @param repository the Repository in which we want to add the files
   * @param filesAdd   files to add in the current stage
   * @return message in String format
   */
  def add(repository: Repository, filesAdd: List[String]): String = {
    repository.index match {
      case Left(error) => error
      case Right(index) =>
        // We update the index with the changes (files modified, deleted)
        val newIndex = Index.updateIndex(repository, index)
        if (filesAdd.isEmpty) {
          "Nothing specified, nothing added.\nMaybe you wanted to say 'sgit add .'?"
        }
        else if (filesAdd.head == ".") {
          // We add all the files recursively in the current directory and subdirectory (but not the ones in .sgit)
          val listFiles = SgitIO.listFiles(ImpureRepository.getCurrentPath)
          val files = listFiles.filterNot(x => Repository.getPathInRepo(repository, x).contains(repository.pathRepo))
          if (files.nonEmpty) {
            // We create the Blobs
            val blobs: List[BlobFile] = files.map(Blob.transformToBlobFile(_).getOrElse(null))
            val blobFiles: List[BlobIndex] = Blob.treatBlob(repository, blobs)
            val indexUpdated = Index.addFilesToIndex(newIndex, blobFiles)
            Index.writeIndex(repository, indexUpdated)
          }
          else
            "No file(s) to add."
        } else {
          // We get the Canonical path to avoid relative paths
          if (filesAdd.nonEmpty) {
            val pathCano = filesAdd.map(new File(_).getCanonicalPath)
            val blobs: List[BlobFile] = pathCano.filter(x => Repository.isFileInRepo(repository, x) && new File(x).isFile && !x.contains(repository.pathRepo)).map(Blob.transformToBlobFile(_).getOrElse(null))
            if (blobs.nonEmpty) {
              val blobFiles: List[BlobIndex] = Blob.treatBlob(repository, blobs)
              val indexUpdated = Index.addFilesToIndex(newIndex, blobFiles)
              Index.writeIndex(repository, indexUpdated)
            } else
              "File(s) not valid."
          } else
            "No file(s) to add."
        }
    }
  }
}
