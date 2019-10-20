package core.commands

import core.objects.{Branch, Checkout, Commit}
import core.repository.{Repository, Status}

object CheckoutCmd {

  /**
   * Function to change of Branch.
   *
   * @param repository Repository
   * @param branchName the Branch name where we want to checkout
   * @return message in String format
   */
  def checkout(repository: Repository, branchName: String): String = {
    // We check if we are not already on the Branch or if the Branch exists
    if (repository.currentBranch.branchName == branchName) {
      s"Already on branch $branchName."
    } else if (!Branch.branchExists(repository, branchName)) {
      s"Branch $branchName doesn't exist."
    }
    else {
      repository.index match {
        case Left(error) => error
        case Right(index) =>
          val changesNotStaged = Status.changesNotStaged(repository, index)
          repository.commit match {
            case Left(error) => error
            case Right(value) =>
              // Check for the last Commit
              if (value != null) {
                Commit.commitToMap(repository, value.sha) match {
                  case Left(error) => error
                  case Right(commitMap) =>
                    val indexLastCommit = Commit.commitToList(commitMap)
                    val changesNotCommitted = Status.changesNotCommitted(repository, index, indexLastCommit)
                    val shaCommitBranch = Branch.getBranchCommit(repository, branchName).commit
                    Commit.commitToMap(repository, shaCommitBranch) match {
                      case Left(error) => error
                      case Right(commitBranchMap) =>
                        Checkout.checkout(repository, branchName, changesNotStaged, changesNotCommitted, index, commitBranchMap)
                    }
                }
              } else {
                // There was no Commit before
                val changesNotCommitted = Status.changesNotCommitted(repository, index, List())
                Checkout.checkout(repository, branchName, changesNotStaged, changesNotCommitted, index, Map())
              }
          }
      }
    }
  }
}