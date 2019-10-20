package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.{BranchCmd, CheckoutCmd, InitCmd}
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class CheckoutTest extends FlatSpec with BeforeAndAfterEach {

  val currentPath: String = System.getProperty("user.dir")
  val filename = "filetest.txt"
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

  it should "return an error branch doesn't exist" in {
    val text = CheckoutCmd.checkout(repository, "test")
    assert(text == "Branch test doesn't exist.")
  }

  it should "return an error already on branch" in {
    val text = CheckoutCmd.checkout(repository, "master")
    assert(text == "Already on branch master.")
  }

  it should "update the head file" in {
    BranchCmd.branch(repository, "test")
    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    CheckoutCmd.checkout(newRepo, "test")
    val newRepo2 = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    assert(newRepo2.currentBranch.branchName == "test")
  }


}
