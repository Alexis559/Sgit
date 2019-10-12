package coreTest.objects

import core.objects.Blob
import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

class BlobTest extends FlatSpec with BeforeAndAfterEach {
  override def beforeEach(): Unit = {
    Repository.createRepository(System.getProperty("user.dir"))
  }

  it should "create a blob with the good content" in {
    IO.createFile(IO.buildPath(List(System.getProperty("user.dir"), ".sgit")), "filetest.txt", "testcontent")
    Blob.treatBlob(IO.buildPath(List(System.getProperty("user.dir"), ".sgit", "filetest.txt")))
    val sha = SgitIO.sha("testcontent")
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    val path = IO.buildPath(List(System.getProperty("user.dir"), ".sgit", "objects", dirName, fileName))
    if (IO.fileExist(path))
      IO.readContentFile(path) match {
        case Left(error) => assert(false)
        case Right(result) => assert(IO.listToString(result) == "testcontent")
      }
    else
      assert(false)
  }

  it should "update the index file" in {
    IO.createFile(IO.buildPath(List(System.getProperty("user.dir"), ".sgit")), "filetest.txt", "testcontent")
    Blob.treatBlob(IO.buildPath(List(System.getProperty("user.dir"), ".sgit", "filetest.txt")))
    val sha = SgitIO.sha("testcontent")
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    IO.readContentFile(Repository.getPathToIndex.getOrElse("")) match {
      case Left(error) => assert(false)
      case Right(result) => {
        assert(IO.listToString(result).contains(sha))
      }
    }
  }
}
