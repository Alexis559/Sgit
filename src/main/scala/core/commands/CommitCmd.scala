package core.commands

import core.objects.{Commit, Tree}
import core.repository.Repository

object CommitCmd {
  /**
   * Function to create the commit.
   *
   * @param repository    Repository
   * @param messageCommit message to give to the commit
   * @return message in String format
   */
  def commit(repository: Repository, messageCommit: String): String = {
    repository.index match {
      case Left(error) => error
      case Right(index) =>
        if (index.nonEmpty) {
          val tree = Tree.buildTree(repository, index)
          Commit.commit(repository, messageCommit, tree)
        } else {
          "Nothing to commit."
        }
    }
  }
}
