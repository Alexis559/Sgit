package coreTest.objects

import java.io.File
import java.nio.file.Files

import core.commands.InitCmd
import core.objects.Object
import core.repository.{ImpureRepository, Repository}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

class ObjectTest extends FlatSpec with BeforeAndAfterEach {

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

  it should "create an object with the good content" in {
    val sha = SgitIO.sha("ttttteeeeeeesssssstttttt")
    Object.createObject(repository, sha, textcontent)
    val dirName = sha.substring(0, 2)
    val fileName = sha.substring(2)
    val path = IO.buildPath(List(Repository.pathToObjects(repository), dirName, fileName))
    if (IO.fileExist(path))
      IO.readContentFile(path) match {
        case Left(_) => assert(false)
        case Right(result) => assert(IO.listToString(result) == textcontent)
      }
    else
      assert(false)
  }
}
