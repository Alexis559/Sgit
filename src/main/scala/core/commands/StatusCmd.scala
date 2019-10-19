package core.commands

import core.objects.Commit
import core.repository.{Repository, Status}
import utils.io.SgitIO

object StatusCmd {

  /**
   * Function to get the status of the repository.
   *
   * @param repository Repository
   * @return message in String format
   */
  def status(repository: Repository): String = {
    repository.index match {
      case Left(error) => error
      case Right(index) =>
        val listFiles = SgitIO.listFiles(Repository.getRepoPath(repository))
        val listFilesRepo = listFiles.filterNot(x => Repository.getPathInRepo(repository, x).contains(repository.pathRepo))
        val untrackedFiles = Status.getUntrackedFiles(repository, index.map(_.fileName), listFilesRepo)
        val changesNotStaged = Status.changesNotStaged(repository, index)
        repository.commit match {
          case Left(error) => error
          case Right(value) =>
            if (value != null) {
              Commit.commitToMap(repository, value.sha) match {
                case Left(error) => error
                case Right(commitMap) =>
                  Status.status(repository, untrackedFiles, changesNotStaged, index, commitMap)
              }
            } else {
              Status.status(repository, untrackedFiles, changesNotStaged, index, Map())
            }

        }
    }
  }
}

