package core.commands

import core.objects.Commit
import core.repository.{Index, Repository}

object CommitCmd {
  /**
   * Function to create the commit.
   *
   * @param messageCommit message to give to the commit
   */
  def commit(messageCommit: String): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => print(error)
      case Right(_) =>
        Index.getTrackedFiles
        Commit.commit(messageCommit)
    }
  }
}
