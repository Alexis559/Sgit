package core.objects

import core.repository.Repository
import utils.io.IO

import scala.math.max

object DiffAlgo {

  /**
   * Function to compute the difference between all the files in the index and in the working directory.
   *
   * @param repository Repository
   * @param index      Index in List of BlobIndex format
   * @return message in String format
   */
  def diffIndexWorking(repository: Repository, index: List[BlobIndex]): String = {
    val listDiff = index.map(x => Map(x.fileName -> diffFiles(IO.readContentFile(x.fileName).getOrElse(List()), IO.readContentFile(Object.getObjectFilePath(repository, x.sha)).getOrElse(List()))))
    printDiff(listDiff)
  }

  /**
   * Function to compute the difference between two files.
   *
   * @param contentNewFile content of new file
   * @param contentOldFile content of old file
   * @return the List of differences between the two files
   */
  def diffFiles(contentNewFile: List[String], contentOldFile: List[String]): List[String] = {
    val matrix = createMatrix(contentNewFile, contentOldFile)
    LCSAlgo(matrix, contentNewFile, contentOldFile)
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
   * @return message in String format
   */
  def printDiff(listDiff: List[Map[String, List[String]]]): String = {
    val list = listDiff.filter(x => x.head._2.nonEmpty)
    if (list.isEmpty)
      "No difference to display."
    else {
      IO.listToString(list.map(x => {
        x.head._1 + "\n" + IO.listToString(x.head._2) + "\n\n"
      }))
    }
  }
}
