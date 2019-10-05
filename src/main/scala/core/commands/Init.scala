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
    if(IO.isEmpty(path)) {
      IO.createDirectory(path, ".sgit")
      val repository = new Repository(path)
      val directories = listDir.map(x => IO.createDirectory(repository.sgitdir, x))
      IO.createFile(repository.sgitdir, "description", "Unnamed repository, edit this file 'description' to name the repository.\n")
      IO.createFile(repository.sgitdir, "HEAD", "ref: refs/heads/master\n")
      IO.createFile(repository.sgitdir, "INDEX", "")
    }
    else
      print("Directory '" + path + "' is not empty !")
  }
}
