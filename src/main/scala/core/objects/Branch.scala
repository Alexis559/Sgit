package core.objects

import core.repository.Repository
import utils.io.IO

object Branch {

  /**
   * Function to get the current Branch name.
   *
   * @return Either left: error message, Either right: the Branch name in String format.
   */
  def getCurrentBranch: Either[String, String] = {
    Repository.getPathToHead match {
      case Left(error) => Left(error)
      case Right(result) => {
        IO.readContentFile(result) match {
          case Left(error) => Left(error)
          case Right(result) => {
            Right(IO.listToString(result).split(IO.getRegexFileSeparator).last)
          }
        }
      }
    }
  }
}
