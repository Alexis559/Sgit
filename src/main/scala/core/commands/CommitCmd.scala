package core.commands

import core.objects.Commit
import core.repository.{Index, Repository}
import utils.parser.Printer

object CommitCmd {
  /**
   * Function to create the commit.
   *
   * @param messageCommit message to give to the commit
   */
  def commit(messageCommit: String): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(_) =>
        Index.getTrackedFiles
        Commit.commit(messageCommit)
    }
  }
}
