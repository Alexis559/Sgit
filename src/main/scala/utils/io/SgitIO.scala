package utils.io

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

import scala.annotation.tailrec

object SgitIO {

  /**
   * Function to get the SHA-1 hash of the content of a file.
   *
   * @param content to hash
   * @return the SHA-1 hash
   */
  def sha(content: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(content.getBytes("UTF-8"))))
  }

  /**
   * Function to convert a List of String into Map.
   *
   * @param list the List of String
   * @return the Map construct from the List
   */
  def listToMap(list: List[String]): Map[String, Any] = {
    val head = list.head // sha
    val newList = list.drop(1) // drop the sha
    val lastMap = Map(head -> Map.empty) // create a map with 'file name -> sha'

    listToMapRec(newList, lastMap)
  }

  /**
   * Recursive function to merge a list of Maps.
   *
   * @param listMap the list of Maps
   * @return a Map which is the result of the merge of the others
   */
  @tailrec
  def mergeMaps(listMap: List[Map[String, Any]]): Map[String, Any] = {
    if (listMap.size == 1) {
      listMap(0)
    } else {
      val map = mergeMap(listMap.head, listMap.tail.head)
      val list = listMap.drop(2)
      mergeMaps(list.appended(map))
    }
  }

  /**
   * Recursive function to convert a List of String into Map.
   *
   * @param list the List of String
   * @param map  the Map that we will return
   * @return the Map construct from the List
   */
  @tailrec
  private def listToMapRec(list: List[String], map: Map[String, Any]): Map[String, Any] = {
    if (list.isEmpty)
      map
    else {
      val newList: List[String] = list.drop(1)
      listToMapRec(newList, Map(list.head -> map))
    }
  }

  /**
   * Function to merge two maps.
   *
   * @param map1 the first map
   * @param map2 the second map
   * @return the merge between the two maps
   */
  private def mergeMap(map1: Map[String, Any], map2: Map[String, Any]): Map[String, Map[String, Any]] = {
    val keySet = map1.keySet ++ map2.keySet

    def nodeForKey(parent: Map[String, Any], key: String): Map[String, Any] = parent.getOrElse(key, Map.empty).asInstanceOf[Map[String, Any]]

    keySet.map(key => key -> mergeMap(nodeForKey(map1, key), nodeForKey(map2, key))).toMap
  }

  /**
   * Function to list all the files in a directory.
   *
   * @param pathFile the path of the Directory
   * @return List of String
   */
  def listFiles(pathFile: String = System.getProperty("user.dir")): List[String] = {
    val files = listFilesRec(new File(pathFile)).toList
    files.map(_.getPath)
  }

  /**
   * Function to list the files in a directory recursively.
   *
   * @param directory the directory where we want to get all the files.
   * @param recursive
   * @return a Sequence of File
   */
  def listFilesRec(directory: File, recursive: Boolean = true): Seq[File] = {
    val files = directory.listFiles
    val result = files.filter(_.isFile)
    result ++
      files
        .filter(_.isDirectory)
        .filter(_ => recursive)
        .flatMap(listFilesRec(_, recursive))
  }

}