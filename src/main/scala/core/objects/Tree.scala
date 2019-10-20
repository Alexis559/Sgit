package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}

object Tree{

  /**
   * Function to create a Tree from the index file.
   *
   * @param repository Repository
   * @param index      Index in List of BlobIndex format
   * @return Map representation of the index file
   */
  def buildTree(repository: Repository, index: List[BlobIndex]): Map[String, Any] = {
    // We split each path to get a list of the directories
    val folders = index.map(x => x.fileName.split(IO.getRegexFileSeparator).toList :+ x.sha)
    // We convert each list of path to a Map
    val listMap = folders.map(x => SgitIO.listToMap(x.reverse))
    // We merge each Map to get a unique Map representing the working directory of the project
    val mergedListMap = SgitIO.mergeMaps(listMap)
    // We create the files according to the Map
    mergedListMap
  }

  /**
   * Function to create the tree files for the commit.
   *
   * @param repository Repository
   * @param mapTree    Map representation of the index file
   * @return Either left: error message, Either right: the sha of the last tree the commit will point on
   */
  def writeTree(repository: Repository, mapTree: Map[String, Any]): Either[String, List[String]] = {
    var listTree = List[String]()
    mapTree.foreach(x => {
      // If a branch has many children we will stock them in this list
      var listChildren = List[String]()
      // If we are at the end of a branch the Map then it's a blob
      if (x._1.contains(".txt") || x._2.asInstanceOf[Map[String, Any]].head._1.length == 40) {
        listTree = ("blob " + x._2.asInstanceOf[Map[String, Any]].head._1 + " " + x._1 + "\n") :: listTree
      } else {
        listChildren = IO.listToString(writeTree(repository, x._2.asInstanceOf[Map[String, Any]]).getOrElse(List(""))) :: listChildren
        // We create the hash with the content of the file that we will create
        val shaChildrenContent = SgitIO.sha(IO.listToString(listChildren))
        // We create the tree line
        val trtr = "tree " + shaChildrenContent + " " + x._1 + "\n"
        // We create the tree files
        Object.createObject(repository, shaChildrenContent, IO.listToString(listChildren))
        listTree = trtr :: listTree
      }
    })
    Right(listTree)
  }
}
