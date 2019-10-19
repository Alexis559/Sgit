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
  var repository: Repository = null

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
}
