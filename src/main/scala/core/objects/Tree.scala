package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}

object Tree{

  /**
   * Function to create a Tree from the index file.
   *
   * @return Either left: error message, Either right: the sha of the last tree the commit will point on
   */
  def buildTree: Either[String, List[String]] = {
    // Get the path to the index file
    Repository.getPathToIndex match {
      case Left(error) => Left(error)
      case Right(result) =>
        // Read the content of the index file
        IO.readContentFile(result) match {
          case Left(error2) => Left(error2)
          case Right(result2) =>
            // We split the content (sha and path) line by line
            val lines = result2.map(x => x.split(" "))
            // We split each path to get a list of the directories
            val folders = lines.map(x => x(1).split(IO.getRegexFileSeparator).toList :+ x(0))
            // We convert each list of path to a Map
            val listMap = folders.map(x => SgitIO.listToMap(x.reverse))
            // We merge each Map to get a unique Map representing the working directory of the project
            val mergedListMap = SgitIO.mergeMaps(listMap)
            // We create the files according to the Map
            writeTree(Map(Repository.getRepoName.getOrElse("") -> mergedListMap)) match {
              case Left(error) => Left(error)
              case Right(result) => Right(result)
            }
        }
    }
  }

  /**
   * Function to create the tree files for the commit.
   *
   * @param mapTree Map representation of the index file
   * @return Either left: error message, Either right: the sha of the last tree the commit will point on
   */
  def writeTree(mapTree: Map[String, Any]): Either[String, List[String]] = {
    var listTree = List[String]()
    mapTree.foreach(x => {
      // If a branch has many children we will stock them in this list
      var listChildren = List[String]()
      // If we are at the end of a branch the Map then it's a blob
      if (x._1.contains(".txt") || x._2.asInstanceOf[Map[String, Any]].head._1.length == 40) {
        listTree = ("blob " + x._2.asInstanceOf[Map[String, Any]].head._1 + " " + x._1 + "\n") :: listTree
      } else {
        listChildren = IO.listToString(writeTree(x._2.asInstanceOf[Map[String, Any]]).getOrElse(List(""))) :: listChildren
        // We create the hash with the content of the file that we will create
        val shaChildrenContent = SgitIO.sha(IO.listToString(listChildren))
        // We create the tree line
        val trtr = "tree " + shaChildrenContent + " " + x._1 + "\n"
        // We create the tree files
        Object.createObject(shaChildrenContent, IO.listToString(listChildren)) match {
          case Left(error) => return Left(error)
          case Right(_) =>
            // We had the tree to the list
            listTree = trtr :: listTree
        }
      }
    })
    Right(listTree)
  }
}
