package core.repository

import core.objects.Object
import utils.io.IO
import utils.parser.Printer

object Index {

  /**
   * Function to update the index file with the changes.
   *
   * @param listFiles files to add at the index
   */
  def updateIndex(listFiles: List[Map[String, String]] = null): Unit = {
    getIndex match {
      case Left(error) => Printer.displayln(error)
      case Right(indexMap) =>
        var index1 = List[Map[String, String]]()
        var index2 = List[Map[String, String]]()
        // We check if the files in the index are always present in the repository
        val filesExisting = indexMap.filter(file => Repository.isFileInRepo(file.head._1))
        index1 = filesExisting.map(x => {
          // If they always exist then we check for the content update (new hash)
          Map(x.head._1 -> Object.returnNewSha(Repository.getAbsolutePathInRepo(x.head._1).getOrElse("")).getOrElse(""))
        })
        // We had the new files to the index
        if (listFiles != null && listFiles.nonEmpty) {
          val values = index1.map(x => x.head._1)
          index2 = listFiles.filterNot(x => values.contains(x.head._1))
            .map(x => Map(x.head._1 -> x.head._2))
        }
        writeIndex(index1 ::: index2)
    }
  }

  /**
   * Function to get the index in a List of Map format.
   *
   * @return Either left: error message, Either right: the index in a List of Map format
   */
  def getIndex: Either[String, List[Map[String, String]]] = {
    Repository.getPathToIndex match {
      case Left(error) => Left(error)
      case Right(result) =>
        IO.readContentFile(result) match {
          case Left(error) => Left(error)
          case Right(result) =>
            val index = indexToMap(result)
            Right(index)
        }
    }
  }

  /**
   * Function to get the index in a List of Map format.
   *
   * @return the index in a List of Map format
   */
  def indexToMap(content: List[String]): List[Map[String, String]] = {
    val listMap = content.map(x => {
      val line = x.split(" ")
      Map(line(1) -> line(0))
    })
    listMap
  }

  /**
   * Function to write in the index file
   *
   * @param index the index in a List of Map format
   */
  def writeIndex(index: List[Map[String, String]]): Unit = {
    var textContent: String = ""
    textContent = IO.listToString(index.map(x => {
      textContent + (x.head._2 + " " + x.head._1 + "\n")
    }))
    Repository.getPathToIndex match {
      case Left(value) => Printer.displayln(value)
      case Right(value) =>
        IO.writeInFile(value, textContent, append = false)
    }
  }

  /**
   * Function to get the List of files that are tracked in the index.
   *
   * @return Either left: error message, Either right: the List of files paths in a String format
   */
  def getTrackedFiles: Either[String, List[String]] = {
    getIndex match {
      case Left(error) => Left(error)
      case Right(indexMap) =>
        Right(indexMap.map(x => x.head._1))
    }
  }
}
