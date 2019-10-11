package core.commands

import core.repository.Repository
import utils.io.IO

object InitCmd {
  val listDir: Array[String] = Array(IO.buildPath(List("refs", "head")), IO.buildPath(List("refs", "tag")), "objects", "branches")

  /**
   * Function that initialize the .sgit repository.
   *
   * @param path where we want to create the new repository
   */
  def createRepository(path : String): Unit = {
    val repoPath = IO.buildPath(List(path, Repository.getSgitName))
    IO.createDirectory(path, Repository.getSgitName)
    listDir.foreach(x => IO.createDirectory(repoPath, x))
    IO.createFile(repoPath, "description", "Unnamed repository, edit this file 'description' to name the repository.\n")
    IO.createFile(repoPath, "HEAD", "ref: " + IO.buildPath(List("refs", "head", "master")) + "\n")
    IO.createFile(repoPath, "index", "")
  }
}
