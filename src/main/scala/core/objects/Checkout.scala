package core.objects

import java.io.File

import core.repository.Repository
import utils.io.IO

object Checkout {

  /**
   * Function to change of Branch.
   *
   * @param repository          Repository
   * @param branchName          the Branch name
   * @param changesNotStaged    the List of changes not staged
   * @param changesNotCommitted the List of changes not committed
   * @param index               the Index in List of BlobIndex format
   * @param commitMap           Commit Index in a Map format
   * @return message in String format
   */
  def checkout(repository: Repository, branchName: String, changesNotStaged: List[Map[String, String]], changesNotCommitted: List[Map[String, String]], index: List[BlobIndex], commitMap: Map[String, Any]): String = {
    val verif = verifChanges(changesNotStaged, changesNotCommitted)
    if (verif._1) {
      updateHead(repository, branchName)
      deleteWorkingDirectory(repository, index)
      recreateWorkingDirectory(repository, branchName, commitMap)
      s"Moved to $branchName."
      }
    else
      verif._2
  }

  /**
   * Function to check if the Stage is updated.
   *
   * @param changesNotStaged    List of changes not staged
   * @param changesNotCommitted List of changes not committed
   * @return Tuple(true if Stage is updated else alse, message in String format)
   */
  def verifChanges(changesNotStaged: List[Map[String, String]], changesNotCommitted: List[Map[String, String]]): (Boolean, String) = {
    if (changesNotStaged.isEmpty) {
      if (changesNotCommitted.isEmpty)
        (true, "")
      else
        (false, "You need to commit the changes.")

    } else {
      (false, "You need to stage the changes.")
    }
  }

  /**
   * Function to update the HEAD file.
   *
   * @param repository Repository
   * @param branchName the Branch name
   */
  def updateHead(repository: Repository, branchName: String): Unit = {
    IO.writeInFile(Repository.pathToHead(repository), "ref: " + IO.buildPath(List("refs", "head", branchName)), false)
  }

  /**
   * Function to delete the files from the working directory.
   *
   * @param repository Repository
   * @param index      the Index in List of BlobIndex format
   */
  def deleteWorkingDirectory(repository: Repository, index: List[BlobIndex]): Unit = {
    index.foreach(x => IO.deleteFile(x.fileName))
    IO.writeInFile(Repository.pathToIndex(repository), "", false)
  }

  /**
   * Function to re-create the files from the index of a Commit.
   *
   * @param repository Repository
   * @param branchName the Branch name
   * @param commitMap  Commit Index in a Map format
   */
  def recreateWorkingDirectory(repository: Repository, branchName: String, commitMap: Map[String, Any]): Unit = {
    val commit = Commit.commitToList(repository, commitMap)
    commit.map(x => x.splitAt(x.lastIndexOf(" ")))
      .foreach(x => {
        val pathRepo = Repository.getRepoPath(repository)
        if (x._1.contains(File.separator)) {
          val filePath = x._1.substring(0, x._1.lastIndexOf(File.separator))
          IO.createDirectory(pathRepo, filePath)
          IO.createFile(IO.buildPath(List(pathRepo, filePath)), x._1.split(IO.getRegexFileSeparator).last, IO.listToString(IO.readContentFile(Object.getObjectFilePath(repository, x._2.replace(" ", ""))).getOrElse(List())))
        } else {
          IO.createFile(pathRepo, x._1.split(IO.getRegexFileSeparator).last, IO.listToString(IO.readContentFile(Object.getObjectFilePath(repository, x._2.replace(" ", ""))).getOrElse(List())))
        }
        IO.writeInFile(Repository.pathToIndex(repository), x._2.replace(" ", "") + " " + x._1 + "\n", true)
      })
  }
}
