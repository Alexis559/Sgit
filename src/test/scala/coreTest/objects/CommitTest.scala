package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.{AddCmd, CommitCmd, InitCmd}
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class CommitTest extends FlatSpec with BeforeAndAfterEach {

  val currentPath: String = System.getProperty("user.dir")
  val filename = "filetest.txt"
  val filename2 = "filetest2.txt"
  val textcontent = "testcontent"
  var repoDir: String = ""
  var repository: Repository = _

  override def beforeEach(): Unit = {
    repoDir = Files.createTempDirectory("RepoTestSgit").toString
    InitCmd.init(repoDir)
    repository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
  }

  override def afterEach(): Unit = {
    IO.deleteRecursively(new File(repoDir))
  }

  it should "return nothing to commit" in {
    val message = CommitCmd.commit(repository, "test")
    assert(message == "Nothing to commit.")
  }

  it should "update the commit in the branch file" in {
    IO.createFile(repoDir, filename, textcontent)
    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val newRepository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    CommitCmd.commit(newRepository, "test")
    val newRepository2 = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    assert(newRepository2.currentBranch.commit != "nil")
  }
}
