package coreTest.objects

import core.objects.{Blob, Branch, Commit}
import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

class CommitTest extends FlatSpec with BeforeAndAfterEach {

  val currentPath: String = System.getProperty("user.dir")
  val filename = "filetest.txt"
  val repoDir: String = Repository.getSgitName
  val textcontent = "testcontent"

  override def beforeEach(): Unit = {
    Repository.createRepository(System.getProperty("user.dir"))
  }

  it should "update the sha1 in the current Branch file" in {
    IO.createFile(IO.buildPath(List(currentPath, repoDir)), filename, textcontent)
    Blob.treatBlob(List(IO.buildPath(List(currentPath, repoDir, filename))))
    Commit.commit("test")
    Commit.updateCommitBranch(SgitIO.sha("testSha1"))
    Branch.getCurrentBranch match {
      case Left(_) => assert(false)
      case Right(result1) =>
        Repository.getPathToRefHeads match {
          case Left(_) => assert(false)
          case Right(result2) =>
            IO.readContentFile(IO.buildPath(List(result2, result1))) match {
              case Left(_) => assert(false)
              case Right(result) =>
                assert(IO.listToString(result).contains(SgitIO.sha("testSha1")))
            }
        }
    }
  }
}
