package utils.io

import java.io.{File, FileWriter, PrintWriter}
import java.math.BigInteger
import java.security.MessageDigest

import scala.annotation.tailrec
import scala.io.Source

object IO {

  /**
   * Function to get the current path.
   * @return the path in String format
   */
  def getCurrentPath: String = {
    System.getProperty("user.dir")
  }

  /**
   * Function to create a new directory.
   * @param pathDir path where we want to create the directory
   * @param dirName the name of the directory
   */
  def createDirectory(pathDir: String, dirName: String): Unit = {
    val file = new File(pathDir)
    if(file.exists() && file.isDirectory) {
      val path = buildPath(List(pathDir, dirName))
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
      val path = buildPath(List(pathFile, fileName))
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

  /**
   * Function to convert the content in sha-256
   * @param content to convert in sha-256
   * @return the sha-256 equivalent of the content
   */
  def sha(content: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(content.getBytes("UTF-8"))))
  }

  /**
   * Function to find the .sgit in the repository.
   * @param path current path
   * @return the path where is located the .sgit, else return null if not found
   */
  @tailrec
  def getRepositoryPath(path: String = IO.getCurrentPath): String = {
    val file = new File(buildPath(List(path, ".sgit")))
    if(file.exists()) {
      file.getAbsolutePath
    }else if(file.getParent == "null"){
      null
    }else {
      getRepositoryPath(new File(path).getParent)
    }
  }

  /**
   * Function to read the content of a file.
   * @param pathFile path to the file to read
   * @return the content of the file in String format
   */
  def readContentFile(pathFile: String): String = {
    if(new File(pathFile).exists()) {
      val bufferedSource = Source.fromFile(pathFile)
      val textContent = bufferedSource.getLines().mkString
      bufferedSource.close
      textContent
    }else{
      throw new Exception("File doesn't exist !")
    }
  }

  /**
   * Function to write in a file.
   * @param path path to the file where to write
   * @param content to write in the file
   */
  def writeInFile(path: String, content: String): Unit = {
    val file = new File(path)
    if(file.exists() && file.isFile) {
      val writer = new FileWriter(file, true)
      writer.write(content)
      writer.close()
    }
  }

  /**
   * Function to get the absolute path of a file.
   * @param path path to the file
   * @return the path in a String format
   */
  def getPathFile(path: String): String = {
    val file = new File(path)
    if(file.exists()) {
      file.getAbsolutePath
    }else{
      throw new Exception("File doesn't exist !")
    }
  }

  /**
   * Function to build path.
   * @param listPath list of directories to build the path
   * @return the path in String format
   */
  def buildPath(listPath: List[String]): String = {
    var path = ""
    listPath.foreach(x => path = path + x + File.separator)
    println(path)
    path.substring(0, path.length-1)
  }

  /**
   * Function to get the path to the object folder in .sgit
   * @return the path in String format
   */
  def getPathToObject: String = {
    buildPath(List(getRepositoryPath(), "objects"))
  }

  /**
   * Function to get the path to the INDEX file in .sgit
   * @return the path in String format
   */
  def getPathToIndex: String = {
    buildPath(List(getRepositoryPath(), "INDEX"))
  }
}
