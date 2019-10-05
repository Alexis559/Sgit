package core.init

import core.repository.Repository
import utils.io.IO._

object Init {

  val listDir: Array[String] = Array(".sgit", "")

  def createRepository(path : String): Unit = {
    if(isEmpty(path)) {
      createDirectory(path, listDir(0))
      val repository = new Repository(path)
    }
    else
      print("Directory '" + path + "' is already a Sgit repository !")
  }
}
