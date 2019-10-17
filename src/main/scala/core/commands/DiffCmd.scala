package core.commands

import core.objects.DiffAlgo
import core.repository.Repository
import utils.parser.Printer

object DiffCmd {
  /**
   * Function to get the difference between the index and the working directory.
   */
  def diff(): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(_) => DiffAlgo.diffIndexWorking()
    }
  }
}
