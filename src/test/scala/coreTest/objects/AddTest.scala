package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.{AddCmd, InitCmd}
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

class AddTest extends FlatSpec with BeforeAndAfterEach {
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

  it should "create a blob with the good content" in {
    IO.createFile(repoDir, filename, textcontent)

    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val sha = SgitIO.sha(textcontent)
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    val path = IO.buildPath(List(repoDir, ".sgit", "objects", dirName, fileName))
    if (IO.fileExist(path))
      IO.readContentFile(path) match {
        case Left(_) => assert(false)
        case Right(result) => assert(IO.listToString(result) == textcontent)
      }
    else
      assert(false)
  }

  it should "update the index file" in {
    IO.createFile(repoDir, filename, textcontent)
    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename))))

    val sha = SgitIO.sha(textcontent)
    IO.readContentFile(Repository.pathToIndex(repository)) match {
      case Left(_) => assert(false)
      case Right(result) =>
        assert(IO.listToString(result).contains(sha))
    }
  }

  it should "return an error when adding a file from .sgit" in {
    IO.createFile(Repository.getPathSgit(repository), filename, textcontent)
    AddCmd.add(repository, List(IO.buildPath(List(Repository.getPathSgit(repository), filename))))

    val newRepository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    newRepository.index match {
      case Left(value) => assert(false)
      case Right(value) =>
        assert(value.isEmpty)
    }
  }

  it should "return an error when adding a file that doesn't exist" in {
    val text = AddCmd.add(repository, List(IO.buildPath(List(Repository.getPathSgit(repository), filename))))

    val newRepository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    newRepository.index match {
      case Left(value) => assert(false)
      case Right(value) =>
        assert(value.isEmpty && text == "File(s) not valid.")
    }
  }
}
