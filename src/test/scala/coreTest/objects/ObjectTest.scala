package coreTest.objects

import core.objects.Object
import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

class ObjectTest extends FlatSpec with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    Repository.createRepository(System.getProperty("user.dir"))
  }

  it should "create an object with the good content" in {
    val sha = SgitIO.sha("ttttteeeeeeesssssstttttt")
    Object.createObject(sha, "textcontent")
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    val path = IO.buildPath(List(System.getProperty("user.dir"), ".sgit", "objects", dirName, fileName))
    if (IO.fileExist(path))
      IO.readContentFile(path) match {
        case Left(error) => assert(false)
        case Right(result) => assert(IO.listToString(result) == "textcontent")
      }
    else
      assert(false)
  }
}
