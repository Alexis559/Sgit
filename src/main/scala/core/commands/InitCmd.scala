package core.commands

import core.repository.Repository

object InitCmd {
  def init(pathRepo: String): Unit = {
    Repository.createRepository(pathRepo)
  }
}
