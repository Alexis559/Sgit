package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.{InitCmd, TagCmd}
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class TagTest extends FlatSpec with BeforeAndAfterEach {

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

  it should "return an empty list" in {
    assert(repository.tags.isEmpty)
  }

  it should "create a Tag" in {
    val text = TagCmd.tag(repository, "test")
    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    assert(newRepo.tags.exists(_.tagName == "test") && text.contains("Tag test created") && text.contains(newRepo.currentBranch.commit))
  }

  it should "return the Tags" in {
    TagCmd.tag(repository, "test")
    TagCmd.tag(repository, "test2")
    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    assert(newRepo.tags.exists(x => x.tagName == "test") && newRepo.tags.exists(x => x.tagName == "test2"))
  }

  it should "return an error Tag already exists" in {
    TagCmd.tag(repository, "test")
    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val text = TagCmd.tag(newRepo, "test")

    assert(text == "Tag test already exists.")
  }

  it should "create a Tag with the good commit" in {
    TagCmd.tag(repository, "test")
    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val path = IO.buildPath(List(Repository.pathToRefsTags(newRepo), "test"))
    val content = IO.readContentFile(path).getOrElse(List())
    assert(content.head == newRepo.currentBranch.commit)
  }
}
