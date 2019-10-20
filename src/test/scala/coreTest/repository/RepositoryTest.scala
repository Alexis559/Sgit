package coreTest.repository

import java.io.File
import java.nio.file.Files

import core.commands.InitCmd
import core.repository.ImpureRepository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class RepositoryTest extends FlatSpec with BeforeAndAfterEach {

  val currentPath: String = System.getProperty("user.dir")
  val filename = "filetest.txt"
  val textcontent = "testcontent"
  var repoDir: String = ""
  override def beforeEach(): Unit = {
    repoDir = Files.createTempDirectory("RepoTestSgit").toString
  }

  override def afterEach(): Unit = {
    IO.deleteRecursively(new File(repoDir))
  }

  it should "create the .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create description file in .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit", "description"))
    val content = IO.listToString(IO.readContentFile(path).getOrElse(List[String]()))
    val file = new File(path)
    assert(file.exists() && file.isFile && content == "Unnamed repository, edit this file 'description' to name the repository.")
  }

  it should "create HEAD file in .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit", "HEAD"))
    val file = new File(path)
    val content = IO.listToString(IO.readContentFile(path).getOrElse(List[String]()))
    assert(file.exists() && file.isFile && content == "ref: " + IO.buildPath(List("refs", "head", "master")))
  }

  it should "create index file in .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit", "index"))
    val file = new File(path)
    val content = IO.listToString(IO.readContentFile(path).getOrElse(List[String]()))
    assert(file.exists() && file.isFile && content == "")
  }

  it should "create tag directory in a refs directory in .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit", "refs", "tags"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create head directory in a refs directory in .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit", "refs", "head"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create object directory in .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit", "objects"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "return the good path to the .sgit directory" in {
    InitCmd.init(repoDir)
    val path = IO.buildPath(List(repoDir, ".sgit"))
    val repository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    assert(path == repository.pathRepo)
  }

  it should "return an error it's not a .sgit repository" in {
    ImpureRepository.chargeRepo(repoDir) match {
      case Left(_) => assert(true)
      case Right(_) => assert(false)
    }
  }
}