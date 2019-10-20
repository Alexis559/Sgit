package core.commands

import core.objects.{Commit, Log}
import core.repository.Repository

object LogCmd {

  /**
   * Function to get the Log of the Repository for the current Branch.
   *
   * @param repository Repository
   * @return Log in String format
   */
  def log(repository: Repository): String = {
    repository.commit match {
      case Left(error) => error
      case Right(value) =>
        if (value == null)
          "No commits."
        else {
          Commit.getListCommit(repository) match {
            case Left(error) => error
            case Right(commits) =>
              Log.log(repository, commits)
          }
        }

    }
  }

}
