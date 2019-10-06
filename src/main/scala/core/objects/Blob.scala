package core.objects

import utils.io.{IO, SgitIO}

object Blob {

  /**
   * Function to get SHA-1 checksum for the files to create the folders and the files.
   * @param file path to the file
   */
  def treatBlob(file: String): Unit = {
    val textContent = IO.readContentFile(file)
    if(textContent.isRight) {
      val fileContent = textContent.right.get
      val shaContent = SgitIO.sha(IO.listToString(fileContent))

      SgitIO.getPathToObject match {
        case Left(error) => print(error)
        case Right(result) => createBlob(result, shaContent, IO.listToString(fileContent))
      }

      SgitIO.getPathToIndex match {
        case Left(error) => print(error)
        case Right(result) => IO.writeInFile(result, shaContent + " " + IO.getPathFile(file).right.get + "\n")
      }
    }else{
      print(textContent.left.get)
    }
  }

  /**
   * TODO move this function to OBJECT
   * @param repoDir
   * @param sha
   * @param textContent
   */
  def createBlob(repoDir: String, sha: String, textContent: String): Unit = {
    println(sha)
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    IO.createDirectory(repoDir, dirName)
    IO.createFile(IO.buildPath(List(repoDir, dirName)), fileName, textContent)
  }
}
