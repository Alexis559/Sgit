package utils.io

import java.io.{File, FileWriter, PrintWriter}

import scala.io.Source

object IO {

  /**
   * Function to get the current path.
   *
   * @return the path in String format
   */
  def getCurrentPath: String = {
    System.getProperty("user.dir")
  }

  /**
   * Function to create a new directory.
   *
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
      print("The directory '" + pathDir + "' doesn't exists !\n")
    }
  }

  /**
   * Function to create a new file.
   *
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
      print("The directory '" + pathFile + "' doesn't exists !\n")
    }
  }

  /**
   * Function to build path.
   *
   * @param listPath list of directories to build the path
   * @return the path in String format
   */
  def buildPath(listPath: List[String]): String = {
    var path = ""
    listPath.foreach(x => path = path + x + File.separator)
    path.substring(0, path.length - 1)
  }

  /**
   * Function to know if a directory is empty.
   *
   * @param pathDir the path to the directory that we want to check
   * @return true if empty, false is not
   */
  def isEmpty(pathDir: String): Boolean = {
    val file = new File(pathDir)
    file.list().length == 0
  }

  /**
   * Function to read the content of a file.
   *
   * @param pathFile path to the file to read
   * @return Either left: error message, Either right: the content of the file in String format
   */
  def readContentFile(pathFile: String): Either[String, List[String]] = {
    if(new File(pathFile).exists()) {
      val bufferedSource = Source.fromFile(pathFile)
      val textContent = bufferedSource.getLines().toList
      bufferedSource.close
      Right(textContent)
    }else{
      Left("File " + pathFile + " doesn't exist !\n")
    }
  }

  /**
   * Function to know if a file exists
   *
   * @param pathFile the path to the file
   * @return true if the file exists else false
   */
  def fileExist(pathFile: String): Boolean = {
    new File(pathFile).exists()
  }

  /**
   * Function to write in a file.
   *
   * @param path path to the file where to write
   * @param content to write in the file
   */
  def writeInFile(path: String, content: String, append: Boolean): Unit = {
    val file = new File(path)
    if(file.exists() && file.isFile) {
      val writer = new FileWriter(file, append)
      writer.write(content)
      writer.close()
    }
  }

  /**
   * Function to get the path of a file.
   *
   * @param path path to the file
   * @return Either left: error message, Either right: the path in String format to the file
   */
  def getPathFile(path: String): Either[String, String] = {
    val file = new File(path)
    if(file.exists()) {
      var path = file.getPath.replace("." + File.separator, "")

      if (path.startsWith("."))
        path = path.replaceFirst(".", "")

      Right(path)
    } else {
      Left("File " + path + "doesn't exist !\n")
    }
  }

  /**
   * Function to concatenate a List of String
   *
   * @param list the List of String
   * @return the String
   */
  def listToString(list: List[String]): String = {
    val string: StringBuilder = new StringBuilder()
    list.foreach(x => string.append(x))
    string.toString()
  }

  /**
   * WTF SERIOUSLY!?
   *
   * @return
   */
  def getRegexFileSeparator: String = {
    if (File.separator == "\\")
      "\\\\"
    else
      "/"
  }
}
