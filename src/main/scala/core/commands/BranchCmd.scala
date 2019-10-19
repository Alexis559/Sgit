package core.commands

import core.objects.Branch
import core.repository.Repository
import utils.io.IO

object BranchCmd {
  /**
   * Function to create a Branch.
   *
   * @param repository the Repository in which we want to create the Branch
   * @param branchName the Branch name we want to create
   * @return message in String format
   */
  def branch(repository: Repository, branchName: String): String = {
    if (branchName.length == 0) {
      // If there's no branch name then we display the list of the branches
      branchList(repository, false)
    }
    else {
      // We check if the Branch is not already created
      if (!Branch.branchExists(repository, branchName))
        Branch.branch(repository, branchName)
      else
        s"Branch $branchName already exists."
    }
  }

  /**
   * List all branches with the last commit.
   *
   * @param repository the Repository of which we want to list the Branches
   * @return message in String format
   */
  def branchList(repository: Repository, verbose: Boolean): String = {
    if (verbose)
    // With the commit
      IO.listToString(repository.branches.map(x =>
        if (repository.currentBranch.branchName == x.branchName)
          "-> " + x.branchName + " " + x.commit + "\n"
        else
          x.branchName + " " + x.commit + "\n"))
    else
    // Without the commit
      IO.listToString(repository.branches.map(x =>
        if (repository.currentBranch.branchName == x.branchName)
          "-> " + x.branchName + "\n"
        else
          x.branchName + "\n"
      ))
  }
}
