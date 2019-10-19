package core.commands

import core.objects.DiffAlgo
import core.repository.Repository

object DiffCmd {
  /**
   * Function to get the difference between the index and the working directory.
   *
   * @param repository Repository
   * @return message in String format
   */
  def diff(repository: Repository): String = {
    repository.index match {
      case Left(error) => error
      case Right(value) =>
        DiffAlgo.diffIndexWorking(repository, value)
    }
  }
}
