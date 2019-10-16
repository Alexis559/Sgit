package core.commands

import core.objects.DiffAlgo
import core.repository.Repository

object DiffCmd {
  /**
   * Function to get the difference between the index and the working directory.
   */
  def diff(): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => print(error)
      case Right(_) => DiffAlgo.diffIndexWorking()
    }
  }
}
