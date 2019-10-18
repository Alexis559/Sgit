package core.commands

import core.objects.Log
import core.repository.Repository
import utils.parser.Printer

object LogCmd {
  /**
   * Function to get the logs.
   */
  def log(): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(_) => Log.log()
    }
  }
}
