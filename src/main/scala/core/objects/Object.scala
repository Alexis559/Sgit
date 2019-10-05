package core.objects

import java.io.File

import utils.io.IO

class Object(val sha: String){

  val shaObject: String = sha
  val objectPath: String = getObjectPath(shaObject)
  val objectFilePath: String = getObjectFilePath(shaObject)

  def getObjectPath(sha : String): String = {
    IO.buildPath(List(IO.getPathToObject, sha.substring(0,2)))
  }

  def getObjectFilePath(shaObject: String): String = {
    IO.buildPath(List(getObjectPath(shaObject), shaObject.substring(3)))
  }
}
