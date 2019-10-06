package core.commands

import java.io.File

import core.repository.Repository
import utils.io.IO

object Init {
  val listDir: Array[String] = Array(IO.buildPath(List("refs", "head")), IO.buildPath(List("refs", "tag")), "objects", "branches")

  /**
   * Function that initialize the Sgit repository.
   * @param path where we want to create the new repository
   */
  def createRepository(path : String): Unit = {
    val repoPath = IO.buildPath(List(path, ".sgit"))
    IO.createDirectory(path,".sgit")
    listDir.foreach(x => IO.createDirectory(repoPath, x))
    IO.createFile(repoPath, "description", "Unnamed repository, edit this file 'description' to name the repository.\n")
    IO.createFile(repoPath, "HEAD", "ref: refs/heads/master\n")
    IO.createFile(repoPath, "index", "")
  }
}
