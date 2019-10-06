package core.objects

import utils.io.{IO, SgitIO}

object Commit {
  def treatCommit(): Unit = {
    val pathIndex = SgitIO.getPathToIndex
    if (pathIndex.isRight) {
      val indexContent = IO.readContentFile(pathIndex.right.get)
      if(indexContent.isRight){
        val lines = indexContent.right.get.map(x => x.split(" ").toList)
        val folders = lines.map(x => print(x(1).split("/").toList))
        print(folders)
        // TODO to continue
      }else{
        print(indexContent.left.get)
      }
    }else{
      print(pathIndex.left.get)
    }
  }
}
