package core.objects

import utils.io.IO

import scala.math.max

object DiffAlgo {

  def diffFiles(fileNew: String, fileOld: String): Either[String, List[String]] = {
    IO.readContentFile(fileNew) match {
      case Left(error) => Left(error)
      case Right(contentNewFile) => {
        IO.readContentFile(fileOld) match {
          case Left(error) => Left(error)
          case Right(contentOldFile) => {
            val matrix = createMatrix(contentNewFile, contentOldFile)
            Right(LCSAlgo(matrix, contentNewFile, contentOldFile))
          }
        }
      }
    }
  }

  /**
   * Function to create a "matrix" after the LCS algorithm.
   *
   * @param contentNewFile
   * @param contentOldFile
   * @return
   */
  def createMatrix(contentNewFile: List[String], contentOldFile: List[String]): Map[(Int, Int), Int] = {
    val matrix = Map[(Int, Int), Int]()

    @scala.annotation.tailrec
    def fillMatrix(matrix: Map[(Int, Int), Int], row: Int, column: Int): Map[(Int, Int), Int] = {
      if (row > contentNewFile.length) {
        matrix
      }
      else if (column > contentOldFile.length) {
        fillMatrix(matrix, row + 1, 0)
      }
      else {
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
   * @param matrix         matrix of comparaison between the two files
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
          LCSAlgoRec(row, column - 1, ("-\t" + contentOldFile(column - 1)) :: listDiff)
        else
        // Only adding
          LCSAlgoRec(row - 1, column, ("+\t" + contentNewFile(row - 1)) :: listDiff)
      }
      else {
        // If the line is the same then we go in diagonal
        if (contentNewFile(row - 1) == contentOldFile(column - 1))
          LCSAlgoRec(row - 1, column - 1, listDiff)
        else {
          if (matrix(row - 1, column) > matrix(row, column - 1))
          // If we change of line then it's an adding
            LCSAlgoRec(row - 1, column, ("+\t" + contentNewFile(row - 1)) :: listDiff)
          else
          // If we change of column then it's a suppression
            LCSAlgoRec(row, column - 1, ("-\t" + contentOldFile(column - 1)) :: listDiff)
        }
      }
    }

    LCSAlgoRec(row, column, List[String]())
  }
}
