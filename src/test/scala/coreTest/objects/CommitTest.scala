package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.{AddCmd, CommitCmd, InitCmd}
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

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

  it should "create the commit file" in {
    IO.createFile(repoDir, filename, textcontent)
    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val newRepository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    CommitCmd.commit(newRepository, "test")

    val newRepository2 = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val commit = newRepository2.currentBranch.commit
    val dir = IO.buildPath(List(Repository.pathToObjects(newRepository2), commit.substring(0, 2)))
    val file = IO.buildPath(List(dir, commit.substring(2)))
    assert(new File(dir).exists() && new File(file).exists())

    val content = IO.readContentFile(file).getOrElse(List())


    assert(content.head.contains(filename) && content.head.contains(SgitIO.sha(textcontent)) && content(2).contains("parent nil"))
  }

  it should "create the commit file (2)" in {

    IO.createFile(repoDir, filename, textcontent)
    IO.createFile(repoDir, filename2, textcontent)

    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val newRepository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    CommitCmd.commit(newRepository, "test")

    AddCmd.add(newRepository, List(IO.buildPath(List(repoDir, filename2))))

    val newRepository2 = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    CommitCmd.commit(newRepository2, "test")

    val newRepository3 = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val commit = newRepository3.currentBranch.commit
    val dir = IO.buildPath(List(Repository.pathToObjects(newRepository3), commit.substring(0, 2)))
    val file = IO.buildPath(List(dir, commit.substring(2)))
    assert(new File(dir).exists() && new File(file).exists())

    val content = IO.readContentFile(file).getOrElse(List())


    assert(content.head.contains(filename2) && content.head.contains(SgitIO.sha(textcontent)) && content(3).contains("parent " + newRepository2.currentBranch.commit))
  }
}
