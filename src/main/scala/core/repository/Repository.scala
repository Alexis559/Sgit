package core.repository

import java.io.File

class Repository(val pathRepo: String){

  val worktree: String = pathRepo
  val sgitdir: String = pathRepo + File.separator + ".sgit"

  def init(): Unit = {
    if (!(new File(sgitdir).exists())) {
      print("The directory '" + sgitdir + "' is not a Sgit directory !")
    }
  }

  init()

}
