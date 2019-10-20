package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.{BranchCmd, InitCmd}
import core.objects.Branch
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class BranchTest extends FlatSpec with BeforeAndAfterEach {

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

  it should "return the current Branch name" in {
    assert(repository.currentBranch.branchName == "master")
  }

  it should "return nil" in {
    assert(repository.currentBranch.commit == "nil")
  }

  it should "return an error if a branch with the same name exists" in {
    assert(BranchCmd.branch(repository, "master") == "Branch master already exists.")
  }

  it should "create a new branch" in {
    val text = BranchCmd.branch(repository, "test")
    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val branches = newRepo.branches
    assert(Branch.branchExists(newRepo, "test") && text == "Branch test created." && branches.exists(x => x.branchName == "test"))
  }

  it should "create a new branch and have the good commit" in {
    BranchCmd.branch(repository, "test")
    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val branches = newRepo.branches.filter(x => x.branchName == "test")
    assert(branches.head.commit == repository.currentBranch.commit)
  }

  it should "list all the branches" in {
    BranchCmd.branch(repository, "test")

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    val branch = BranchCmd.branch(newRepo, "")
    assert(branch.contains("-> master") && branch.contains("test"))
  }

  it should "list the branches with commit" in {
    BranchCmd.branch(repository, "test")

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    val branch = BranchCmd.branchList(newRepo, true)
    assert(branch.contains("-> master " + newRepo.currentBranch.commit) && branch.contains("test " + newRepo.currentBranch.commit))
  }
}
