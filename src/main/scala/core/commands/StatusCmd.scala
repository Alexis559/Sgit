package core.commands

import core.repository.{Repository, Status}

object StatusCmd {

  /**
   * Function to get the status of the repository.
   */
  def status(): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => print(error)
      case Right(_) => Status.status()
    }
  }
}
