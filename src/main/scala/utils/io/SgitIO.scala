package utils.io

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

import utils.io.IO._

import scala.annotation.tailrec

object SgitIO {

  /**
   * Function to get the SHA-1 hash of the content of a file
   * @param content to hash
   * @return the SHA-1 hash
   */
  def sha(content: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(content.getBytes("UTF-8"))))
  }

  /**
   * Function to find the .sgit in the repository.
   * @param path current path
   * @return Either left: error message, Either right: the path in String format to the .sgit
   */
  @tailrec
  def getRepositoryPath(path: String = IO.getCurrentPath): Either[String, String] = {
    val file = new File(buildPath(List(path, ".sgit")))
    val index = new File(buildPath(List(file.getAbsolutePath, "INDEX")))
    val head = new File(buildPath(List(file.getAbsolutePath, "HEAD")))
    val objects = new File(buildPath(List(file.getAbsolutePath, "objects")))
    val refs = new File(buildPath(List(file.getAbsolutePath, "refs")))

    if(file.exists() && index.exists() && head.exists() && objects.exists() && refs.exists()) {
      Right(file.getAbsolutePath)
    }else if(file.getParent == "null"){
      Left("This is not a Sgit repository. You should use 'sgit init'.")
    }else {
      getRepositoryPath(new File(path).getParent)
    }
  }

  /**
   * Function to get the path to the object folder in .sgit
   * @return Either left: error message, Either right: the path in String format to the objects folder
   */
  def getPathToObject: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(buildPath(List(result, "objects")))
    }
  }

  /**
   * Function to get the path to the INDEX file in .sgit
   * @return Either left: error message, Either right: the path in String format to the INDEX file
   */
  def getPathToIndex: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(buildPath(List(result, "index")))
    }
  }

  /**
   * Function to get the path to the HEAD file in .sgit
   * @return Either left: error message, Either right: the path in String format to the HEAD file
   */
  def getPathToHead: Either[String, String] = {
    getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(result) => Right(buildPath(List(result, "HEAD")))
    }
  }
}
