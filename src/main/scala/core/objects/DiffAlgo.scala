package core.objects

import core.repository.{Index, Repository}
import utils.io.IO

import scala.math.max

object DiffAlgo {

  /**
   * Function to compute the difference between all the files in the index and in the working directory.
   */
  def diffIndexWorking(): Unit = {
    Index.getIndex match {
      case Left(error) => println(error)
      case Right(index) =>
        Repository.getPathToParenSgit match {
          case Left(error) => println(error)
          case Right(path) =>

            val listDiff = index.flatten
              .map(x => Map(x._1 -> diffFiles(x._1, Object.getObjectFilePath(x._2).getOrElse("")).getOrElse(List())))

            printDiff(listDiff)

        }
    }
  }

  /**
   * Function to compute the difference between two files.
   *
   * @param fileNew the path to the new file
   * @param fileOld the path to the old file
   * @return Either left: error message, Either right: the list of differences between the two files
   */
  def diffFiles(fileNew: String, fileOld: String): Either[String, List[String]] = {
    IO.readContentFile(fileOld) match {
      case Left(error) => Left(error)
      case Right(contentOldFile) => {
        // If the file doesn't exist anymore we can return an empty list
        val contentNewFile = IO.readContentFile(fileNew).getOrElse(List())
        val matrix = createMatrix(contentNewFile, contentOldFile)
        Right(LCSAlgo(matrix, contentNewFile, contentOldFile))
      }
    }
  }


  /**
   * Function to create a "matrix" for the LCS algorithm.
   *
   * @param contentNewFile the content of the new file
   * @param contentOldFile the content of the old file
   * @return matrix of comparison between the two files
   */
  def createMatrix(contentNewFile: List[String], contentOldFile: List[String]): Map[(Int, Int), Int] = {
    val matrix = Map[(Int, Int), Int]()

    @scala.annotation.tailrec
    def fillMatrix(matrix: Map[(Int, Int), Int], row: Int, column: Int): Map[(Int, Int), Int] = {
      // If there's no more lines to check in the new file then we return the matrix
      if (row > contentNewFile.length) {
        matrix
      }
      // If we are at the end of the old file, we change of line in the new file
      else if (column > contentOldFile.length) {
        fillMatrix(matrix, row + 1, 0)
      }
      else {
        // To create a first row and column full of 0 used for the LCS algorithm
        if (row == 0 || column == 0)
          fillMatrix(matrix ++ Map((row, column) -> 0), row, column + 1)
        else {
          val maxInt: Int = max(matrix(row, column - 1), matrix(row - 1, column))
          if (contentNewFile(row - 1) == contentOldFile(column - 1)) {
            fillMatrix(matrix ++ Map((row, column) -> (maxInt + 1)), row, column + 1)
          } else {
            fillMatrix(matrix ++ Map((row, column) -> maxInt), row, column + 1)
          }
        }
      }
    }

    fillMatrix(matrix, 0, 0)
  }

  /**
   * Longest common subsequence algorithm.
   *
   * @see https://en.wikipedia.org/wiki/Longest_common_subsequence_problem
   * @param matrix         matrix of comparison between the two files
   * @param contentNewFile the content of the new file
   * @param contentOldFile the content of the old file
   * @return a list with all the changes
   */
  def LCSAlgo(matrix: Map[(Int, Int), Int], contentNewFile: List[String], contentOldFile: List[String]): List[String] = {
    val column: Int = contentOldFile.length
    val row: Int = contentNewFile.length

    @scala.annotation.tailrec
    def LCSAlgoRec(row: Int, column: Int, listDiff: List[String]): List[String] = {
      if (row == 0 && column == 0)
        listDiff
      else if (row == 0 || column == 0) {
        if (row == 0)
        // Only suppression
          LCSAlgoRec(row, column - 1, ("\n-\t" + contentOldFile(column - 1)) :: listDiff)
        else
        // Only adding
          LCSAlgoRec(row - 1, column, ("\n+\t" + contentNewFile(row - 1)) :: listDiff)
      }
      else {
        // If the line is the same then we go in diagonal
        if (contentNewFile(row - 1) == contentOldFile(column - 1))
          LCSAlgoRec(row - 1, column - 1, listDiff)
        else {
          if (matrix(row - 1, column) > matrix(row, column - 1))
          // If we change of line then it's an adding
            LCSAlgoRec(row - 1, column, ("\n+\t" + contentNewFile(row - 1)) :: listDiff)
          else
          // If we change of column then it's a suppression
            LCSAlgoRec(row, column - 1, ("\n-\t" + contentOldFile(column - 1)) :: listDiff)
        }
      }
    }

    LCSAlgoRec(row, column, List[String]())
  }

  /**
   * Function to print the differences.
   *
   * @param listDiff list of differences
   */
  def printDiff(listDiff: List[Map[String, List[String]]]): Unit = {
    val list = listDiff.filter(x => x.head._2.nonEmpty)
    if (list.isEmpty)
      println("No difference to display.")
    else {
      println(IO.listToString(list.map(x => {
        x.head._1 + "\n\n" + IO.listToString(x.head._2)
      })))
    }
  }
}
