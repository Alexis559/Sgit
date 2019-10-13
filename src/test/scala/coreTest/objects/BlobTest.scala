package coreTest.objects

import core.objects.Blob
import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

class BlobTest extends FlatSpec with BeforeAndAfterEach {
  val currentPath: String = System.getProperty("user.dir")
  val filename = "filetest.txt"
  val textcontent = "testcontent"
  val repoDir: String = Repository.getSgitName

  override def beforeEach(): Unit = {
    Repository.createRepository(currentPath)
  }

  it should "create a blob with the good content" in {
    IO.createFile(IO.buildPath(List(currentPath, repoDir)), filename, textcontent)
    Blob.treatBlob(List(IO.buildPath(List(currentPath, repoDir, filename))))
    val sha = SgitIO.sha(textcontent)
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    val path = IO.buildPath(List(currentPath, repoDir, "objects", dirName, fileName))
    if (IO.fileExist(path))
      IO.readContentFile(path) match {
        case Left(_) => assert(false)
        case Right(result) => assert(IO.listToString(result) == textcontent)
      }
    else
      assert(false)
  }

  it should "update the index file" in {
    IO.createFile(IO.buildPath(List(currentPath, repoDir)), filename, textcontent)
    Blob.treatBlob(List(IO.buildPath(List(currentPath, repoDir, filename))))
    val sha = SgitIO.sha(textcontent)
    IO.readContentFile(Repository.getPathToIndex.getOrElse("")) match {
      case Left(_) => assert(false)
      case Right(result) =>
        assert(IO.listToString(result).contains(sha))
    }
  }
}
