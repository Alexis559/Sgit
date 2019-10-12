package coreTest.repository

import java.io.File
import java.nio.file.Files

import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class RepositoryTest extends FlatSpec with BeforeAndAfterEach {

  var tempDirPath: String = ""
  val currentPath: String = System.getProperty("user.dir")
  val repoDir: String = Repository.getSgitName

  override def beforeEach(): Unit = {
    tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
  }

  it should "create the .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create description file in .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir, "description"))
    val content = IO.listToString(IO.readContentFile(path).getOrElse(List[String]()))
    val file = new File(path)
    assert(file.exists() && file.isFile && content == "Unnamed repository, edit this file 'description' to name the repository.")
  }

  it should "create HEAD file in .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir, "HEAD"))
    val file = new File(path)
    val content = IO.listToString(IO.readContentFile(path).getOrElse(List[String]()))
    assert(file.exists() && file.isFile && content == "ref: " + IO.buildPath(List("refs", "head", "master")))
  }

  it should "create index file in .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir, "index"))
    val file = new File(path)
    val content = IO.listToString(IO.readContentFile(path).getOrElse(List[String]()))
    assert(file.exists() && file.isFile && content == "")
  }

  it should "create tag directory in a refs directory in .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir, "refs", "tag"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create head directory in a refs directory in .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir, "refs", "HEAD"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create branches directory in .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir, "branches"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create object directory in .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir, "objects"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "return the good path to the .sgit directory" in {
    println("Test made in: " + tempDirPath)
    Repository.createRepository(tempDirPath)
    val path = IO.buildPath(List(tempDirPath, repoDir))
    assert(path == Repository.getRepositoryPath(tempDirPath).getOrElse(""))
  }

  it should "return an error it's not a .sgit repository" in {
    println("Test made in: " + tempDirPath)
    Repository.getRepositoryPath(tempDirPath) match {
      case Left(error) => assert(true)
      case Right(result) => assert(false)
    }
  }
}