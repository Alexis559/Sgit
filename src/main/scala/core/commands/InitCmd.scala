package core.commands

import java.io.File

import core.repository.ImpureRepository

object InitCmd {

  /**
   * Function to create the repository.
   *
   * @param pathRepo path where to create the repository
   */
  def init(pathRepo: String): String = {
    ImpureRepository.getRepositoryPath() match {
      case Left(_) =>
        ImpureRepository.createRepository(pathRepo)
        s"Repository $pathRepo created."
      case Right(result) =>
        if (new File(result).getParent == pathRepo)
          "You are already in a Sgit repository !"
        else {
          ImpureRepository.createRepository(pathRepo)
          s"Repository $pathRepo created."
        }
    }
  }
}
