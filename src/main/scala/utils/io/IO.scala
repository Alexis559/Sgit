package utils.io

import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}

import core.repository.Repository

object IO {

  /**
   * Function to create a new directory.
   * @param pathDir path where we want to create the directory
   * @param dirName the name of the directory
   */
  def createDirectory(pathDir: String, dirName: String): Unit = {
    val file = new File(pathDir)
    if(file.exists() && file.isDirectory) {
      val path = pathDir + File.separator + dirName
      val file = new File(path)
      file.mkdirs()
    }else{
      print("The directory '" + pathDir + "' doesn't exists !")
    }
  }

  /**
   * Function to create a new file.
   * @param pathFile path where we want to create the file
   * @param fileName the name of the file
   * @param contentToWrite the content to write in the file
   */
  def createFile(pathFile: String, fileName: String, contentToWrite: String): Unit = {
    val file = new File(pathFile)
    if(file.exists() && file.isDirectory) {
      val path = pathFile + File.separator + fileName
      val pw = new PrintWriter(new File(path))
      pw.write(contentToWrite)
      pw.close()
    }else{
      print("The directory '" + pathFile + "' doesn't exists !")
    }
  }

  /**
   * Function to know if a directory is empty.
   * @param pathDir the path to the directory that we want to check
   * @return true if empty, false is not
   */
  def isEmpty(pathDir: String): Boolean = {
    val file = new File(pathDir)
    file.list().length == 0
  }
}
