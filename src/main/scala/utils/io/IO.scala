package utils.io

import java.io.File
import java.nio.file.{Path, Paths}

import core.repository.Repository

object IO {

  def createDirectory(pathDir: String, dirName: String): Unit = {
    val file = new File(pathDir)
    if(file.exists() && file.isDirectory) {
      val file = new File(pathDir + File.separator + dirName)
      file.mkdir()
    }else{
      print("The directory '" + pathDir + "' doesn't exists !")
    }
  }

  def isEmpty(pathDir: String): Boolean = {
    val file = new File(pathDir)
    file.list().length == 0
  }

  def repoPath(repository: Repository, path: String): Path = {
    Paths.get(repository.sgitdir, path)
  }

}
