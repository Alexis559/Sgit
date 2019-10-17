package core.commands

import core.repository.{Repository, Status}
import utils.parser.Printer

object StatusCmd {

  /**
   * Function to get the status of the repository.
   */
  def status(): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(_) => Status.status()
    }
  }
}
