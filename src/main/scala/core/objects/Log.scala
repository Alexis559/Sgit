package core.objects

import core.repository.Repository

object Log {

  /**
   * Function to get the Log of the Repository.
   *
   * @param repository Repository
   * @param commits    List of Commit
   * @return Log in String format
   */
  def log(repository: Repository, commits: List[Commit]): String = {
    var log = ""
    commits.reverse.map(x => log += s"\ncommit ${x.sha} (HEAD -> ${repository.currentBranch.branchName})\n\n\t${x.commitMessage}\n").toString()
    log
  }
}
