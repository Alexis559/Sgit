package core.commands

import java.io.File
import core.repository.Repository
import utils.io.IO._

object Init {
  val listDir: Array[String] = Array("refs" + File.separator + "heads", "refs" + File.separator + "tags", "objects", "branches")

  /**
   * Function that initialize the Sgit repository.
   * @param path where we want to create the new repository
   */
  def createRepository(path : String): Unit = {
    if(isEmpty(path)) {
      createDirectory(path, ".sgit")
      val repository = new Repository(path)
      val directories = listDir.map(x => createDirectory(repository.sgitdir, x))
      createFile(repository.sgitdir, "description", "Unnamed repository, edit this file 'description' to name the repository.\n")
      createFile(repository.sgitdir, "HEAD", "ref: refs/heads/master\n")
    }
    else
      print("Directory '" + path + "' is not empty !")
  }
}
