package core.repository

import java.io.File

import utils.io.IO

/**
 * NOT SURE ABOUT THIS CLASS [DEPRECATED] WILL BE DELETED
 * @param pathRepo path where is located the .sgit
 */

class Repository(val pathRepo: String){

  val worktree: String = pathRepo
  val sgitdir: String = IO.buildPath(List(pathRepo, ".sgit"))
  val conf: String = ""

  def init(): Unit = {
    if (!new File(sgitdir).exists) {
      print("The directory '" + sgitdir + "' is not a Sgit directory !")
    }
  }

  init()

}
