package core.objects

import utils.io.{IO, SgitIO}

object Object{

  def getObjectPath(shaObject : String): Either[String, String] = {
    SgitIO.getPathToObject match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, shaObject.substring(0,2))))
    }
  }

  def getObjectFilePath(shaObject: String): Either[String, String] = {
    getObjectPath(shaObject) match {
      case Left(error) => Left(error)
      case Right(result) => Right(IO.buildPath(List(result, shaObject.substring(3))))
    }
  }
}
