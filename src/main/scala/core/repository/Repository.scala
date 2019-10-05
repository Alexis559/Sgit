package core.repository

import java.io.File

/**
 * NOT SURE ABOUT THIS CLASS
 * @param pathRepo path where is located the .sgit
 */


class Repository(val pathRepo: String){

  val worktree: String = pathRepo
  val sgitdir: String = pathRepo + File.separator + ".sgit"
  val conf: String = ""

  def init(): Unit = {
    if (!new File(sgitdir).exists) {
      print("The directory '" + sgitdir + "' is not a Sgit directory !")
    }
  }

  init()

}
