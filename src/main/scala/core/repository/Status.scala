package core.repository

import core.objects.{BlobIndex, Commit, Object}
import utils.io.IO

object Status {

  /**
   * Function to print the status of the repository.
   *
   * @param repository       Repository
   * @param untrackedFiles   List of untracked files
   * @param changesNotStaged List of changes not staged in a Map format
   * @param index            Index in List of BlobIndex format
   * @param commitMap        Index of last Commit in a Map format
   * @return the Status of the Repository
   */
  def status(repository: Repository, untrackedFiles: List[String], changesNotStaged: List[Map[String, String]], index: List[BlobIndex], commitMap: Map[String, Any]): String = {
    val commitList = Commit.commitToList(repository, commitMap)
    val changesNotCommitted = Status.changesNotCommitted(repository, index, commitList)
    val newFilesFirsCommit = Status.listNewFilesFirstCommit(index)

    var status = s"On branch ${repository.currentBranch.branchName}\n"

    if (!Repository.isFirstCommit(repository)) {
      // If it's not the first commit then we can get diff with the last commit
      status += "\nChanges to be committed: \n" + printMapStatus(changesNotCommitted)
    }
    // If it's the first commit and the index is not empty then we display it's content
    else if (Repository.isFirstCommit(repository) && newFilesFirsCommit.nonEmpty) {
      status += "\nChanges to be committed: " + "\n" + listToStringStatus(newFilesFirsCommit)
    }
    else {
      status += "\nNo commits yet\n"
    }
    status += "\nChanges not staged for commit:\n  \t(use \"git add <file>...\" to update what will be committed)\n" + printMapStatus(changesNotStaged) + "\nUntracked files:\n  \t(use \"git add <file>...\" to include in what will be committed)\n" + "\n" + listToStringStatus(untrackedFiles)
    status
  }


  /**
   * Function to get the text to get the new files for the first Commit.
   *
   * @param index Index in List of BlobIndex format
   * @return the Index in a List of String
   */
  def listNewFilesFirstCommit(index: List[BlobIndex]): List[String] = {
    val text = index.map(x => "new file: " + x.fileName)
    text
  }

  /**
   * Function to get the difference with the index and the last Commit.
   *
   * @param repository      Repository
   * @param index           Index in List of BlobIndex format
   * @param lastCommitIndex Index of last Commit in a List of String format
   * @return the List of differences in a Map format
   */
  def changesNotCommitted(repository: Repository, index: List[BlobIndex], lastCommitIndex: List[String]): List[Map[String, String]] = {
    val commitFlat = lastCommitIndex.map(x => x.splitAt(x.lastIndexOf(" ")))
    val commitFiles = commitFlat.map(x => x._1)
    val indexKeys = index.map(_.fileName)
    val indexValues = index.map(_.sha)

    val filesDeleted: List[Map[String, String]] = commitFiles
      .filterNot(x => indexKeys.contains(x))
      .map(x => Map(x -> "deleted"))

    val filesModified: List[Map[String, String]] = commitFlat
      .filter(x => indexKeys.contains(x._1) && !indexValues.contains(x._2.replace(" ", "")))
      .map(y => Map(y._1 -> "modified"))

    val filesAdded: List[Map[String, String]] = indexKeys
      .filterNot(x => commitFiles.contains(x))
      .map(y => Map(y -> "added"))

    filesDeleted ::: filesModified ::: filesAdded
  }

  /**
   * Function to print a Map.
   *
   * @param map a List of Map to print
   * @return the List of Map in a String format
   */
  def printMapStatus(map: List[Map[String, String]]): String = {
    val status = "\n " + map.map(x => {
      "\t" + x.head._2 + ": " + x.head._1 + "\n"
    }).mkString
    status
  }

  /**
   * Function to convert a List to a String.
   *
   * @param list the List to convert in String format
   * @return the List in String format
   */
  def listToStringStatus(list: List[String]): String = {
    list.map(x => "\t" + x + "\n").mkString
  }

  /**
   * Function to get the list of untracked files.
   *
   * @param repository    Repository
   * @param trackedFiles  list of tracked files in a List format
   * @param listFilesRepo list of all the files in the working directory
   * @return List of untracked files
   */
  def getUntrackedFiles(repository: Repository, trackedFiles: List[String], listFilesRepo: List[String]): List[String] = {
    val listFiles = listFilesRepo.map(IO.cleanPathFile(repository, _).getOrElse(""))
    // And we return only the ones who are not in the index
    listFiles.filterNot(trackedFiles.toSet)
  }

  /**
   * Function to get the list of changes in the repository.
   *
   * @param repository   Repository
   * @param trackedFiles list of tracked files in a List format
   * @return the List of changes in a Map format
   */
  def changesNotStaged(repository: Repository, trackedFiles: List[BlobIndex]): List[Map[String, String]] = {
    // List of files that exists
    val filesExisting = trackedFiles.filter(file => Repository.isFileInRepo(repository, file.fileName))
    // List of files that doesn't exist anymore
    val filesDeleted = trackedFiles.filterNot(file => Repository.isFileInRepo(repository, file.fileName))

    val mapFilesDeleted = filesDeleted.map(x => {
      Map(x.fileName -> "deleted")
    })

    val mapFilesModified = filesExisting.filter(x => (Object.returnNewSha(Repository.getPathInRepo(repository, x.fileName))).getOrElse("") != x.sha)
      .map(x => Map(x.fileName -> "modified"))

    mapFilesDeleted ::: mapFilesModified
  }
}