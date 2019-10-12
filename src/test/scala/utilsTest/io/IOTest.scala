package utilsTest.io

import java.io.File
import java.nio.file.Files

import org.scalatest.FlatSpec
import utils.io.IO

class IOTest extends FlatSpec {

  it should "create a directory at the right place" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createDirectory(tempDirPath, "dirCreTest")
    val path = IO.buildPath(List(tempDirPath, "dirCreTest"))
    val file = new File(path)
    assert(file.exists() && file.isDirectory)
  }

  it should "create a file at the right place with the right content" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createFile(tempDirPath, "fileCreTest", "test content")
    val path = IO.buildPath(List(tempDirPath, "fileCreTest"))
    val file = new File(path)
    val content = IO.listToString(IO.readContentFile(path).getOrElse(List[String]()))
    assert(file.exists() && file.isFile && content == "test content")
  }

  it should "create the right path" in {
    val path = "test" + File.separator + "1" + File.separator + "3"
    assert(IO.buildPath(List("test", "1", "3")) == path)
  }

  it should "return true with empty directory" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    assert(IO.isEmpty(tempDirPath))
  }

  it should "return false with no empty directory" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createFile(tempDirPath, "test", "")
    assert(!IO.isEmpty(tempDirPath))
  }

  it should "return an empty List for an empty file" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createFile(tempDirPath, "test", "")
    assert(IO.readContentFile(IO.buildPath(List(tempDirPath, "test"))).getOrElse(List("test")).isEmpty)
  }

  it should "return a List with the content of a file" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createFile(tempDirPath, "test", "test1\ntest2\n")
    val content = IO.readContentFile(IO.buildPath(List(tempDirPath, "test"))).getOrElse(List[String]())
    assert(content.size == 2 && content.head == "test1" && content.tail.head == "test2")
  }

  it should "return an error if file doesn't exist" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.readContentFile(IO.buildPath(List(tempDirPath, "test"))) match {
      case Left(error) => assert(true)
      case Right(result) => assert(false)
    }
  }

  it should "return true if file exists" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createFile(tempDirPath, "test", "")
    assert(IO.fileExist(IO.buildPath(List(tempDirPath, "test"))))
  }

  it should "return false if file dooesn't exist" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    assert(!IO.fileExist(IO.buildPath(List(tempDirPath, "test"))))
  }

  it should "write in file without append" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createFile(tempDirPath, "test", "")
    IO.writeInFile(IO.buildPath(List(tempDirPath, "test")), "test", false)
    val content = IO.readContentFile(IO.buildPath(List(tempDirPath, "test"))).getOrElse(List[String]())
    assert(content.size == 1 && content.head == "test")
  }

  it should "write in file with append" in {
    val tempDirPath = Files.createTempDirectory("RepoTestSgit").toString
    IO.createFile(tempDirPath, "test", "")
    IO.writeInFile(IO.buildPath(List(tempDirPath, "test")), "test1\n", false)
    IO.writeInFile(IO.buildPath(List(tempDirPath, "test")), "test2", true)
    val content = IO.readContentFile(IO.buildPath(List(tempDirPath, "test"))).getOrElse(List[String]())
    assert(content.size == 2 && content.head == "test1" && content.tail.head == "test2")
  }

  it should "return a String from a List of String" in {
    val list = List("test", "test1", "test2")
    assert(IO.listToString(list) == "testtest1test2")
  }
}
