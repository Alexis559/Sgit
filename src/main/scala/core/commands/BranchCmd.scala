package core.commands

import core.objects.Branch
import core.repository.Repository
import utils.parser.Printer

object BranchCmd {
  /**
   * Function to create a Branch.
   *
   * @param branchName message to give to the Branch
   */
  def branch(branchName: String): Unit = {
    if (branchName.length == 0) {
      // If there's no branch name then we display the list of the branches
      Branch.displayBranches()
    }
    else {
      Repository.getRepositoryPath() match {
        case Left(error) => Printer.displayln(error)
        case Right(_) =>
          Branch.branch(branchName)
      }
    }
  }

  /**
   * List all branches with the last commit.
   */
  def branchList(): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(_) =>
        Branch.displayBranchesDetails()
    }
  }
}
