package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.{AddCmd, InitCmd}
import core.objects.DiffAlgo
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class DiffAlgoTest extends FlatSpec with BeforeAndAfterEach {

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

  it should "return an addition" in {
    IO.createFile(repoDir, filename, textcontent)
    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo.index.getOrElse(List())
    IO.writeInFile(IO.buildPath(List(repoDir, filename)), "\nttt", true)

    val diff = DiffAlgo.diffIndexWorking(newRepo, index)
    assert(diff.contains("+\tttt"))

  }

  it should "return a deletion and an addition" in {
    IO.createFile(repoDir, filename, textcontent)
    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo.index.getOrElse(List())
    IO.writeInFile(IO.buildPath(List(repoDir, filename)), "ttt", false)

    val diff = DiffAlgo.diffIndexWorking(newRepo, index)
    assert(diff.contains("+\tttt") && diff.contains("-\ttestcontent"))

  }


  it should "return not diff to print" in {
    IO.createFile(repoDir, filename, textcontent)
    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo.index.getOrElse(List())

    val diff = DiffAlgo.diffIndexWorking(newRepo, index)
    assert(diff.contains("No difference to display."))
  }

}
