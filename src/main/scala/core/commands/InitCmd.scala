package core.commands

import java.io.File

import core.repository.Repository

object InitCmd {
  def init(pathRepo: String): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Repository.createRepository(pathRepo)
      case Right(result) => {
        if (new File(result).getParent == pathRepo)
          println("You are already in a Sgit repository !")
        else
          Repository.createRepository(pathRepo)
      }
    }
  }
}
