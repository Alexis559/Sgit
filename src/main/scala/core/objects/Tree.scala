package core.objects

import utils.io.{IO, SgitIO}

object Tree{
  def treatTree(workFiles: List[String]): Unit = {
    if(workFiles.nonEmpty) {
      val contentFile: StringBuilder = new StringBuilder
      workFiles.foreach(x => contentFile.append(x + "\n"))
      val shaContent = SgitIO.sha(contentFile.toString())

      SgitIO.getPathToObject match {
        case Left(error) => print(error)
        case Right(result) => createTree(result, shaContent, contentFile.toString())
      }
    }
  }

  def createTree(repoDir: String, sha: String, textContent: String): Unit = {
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    IO.createDirectory(repoDir, dirName)
    IO.createFile(IO.buildPath(List(repoDir, dirName)), fileName, textContent)
  }
}
