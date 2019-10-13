package core.repository

import core.objects.Object
import utils.io.IO

object Index {

  def updateIndex(listFiles: List[Map[String, String]] = null): Unit = {
    getIndex match {
      case Left(error) => println(error)
      case Right(indexMap) => {
        var index = List[Map[String, String]]()
        val filesExisting = indexMap.filter(file => Repository.isFileInRepo(file.head._1))
        filesExisting.foreach(x => {
          index = Map(x.head._1 -> Object.returnNewSha(x.head._1).right.get) :: index
        })
        if (listFiles != null && !listFiles.isEmpty) {
          val values = index.map(x => x.head._1)
          listFiles.foreach(x => {
            if (!values.contains(x.head._1))
              index = Map(x.head._1 -> x.head._2) :: index
          })
        }
        writeIndex(index)
      }
    }
  }

  def getIndex: Either[String, List[Map[String, String]]] = {
    Repository.getPathToIndex match {
      case Left(error) => Left(error)
      case Right(result) => {
        IO.readContentFile(result) match {
          case Left(error) => Left(error)
          case Right(result) => {
            val index = indexToMap(result)
            Right(index)
          }
        }
      }
    }
  }

  def indexToMap(content: List[String]): List[Map[String, String]] = {
    val listMap = content.map(x => {
      val line = x.split(" ")
      Map(line(1) -> line(0))
    })
    listMap
  }

  def writeIndex(index: List[Map[String, String]]): Unit = {
    var textContent: String = ""
    index.foreach(x => {
      textContent = textContent + (x.head._2 + " " + x.head._1 + "\n")
    })
    Repository.getPathToIndex match {
      case Left(value) => print(value)
      case Right(value) => {
        IO.writeInFile(value, textContent, false)
      }
    }
  }
}
