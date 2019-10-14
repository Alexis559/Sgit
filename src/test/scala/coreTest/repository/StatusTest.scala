package coreTest.repository

import java.io.File

import core.commands.AddCmd
import core.objects.{Blob, Commit}
import core.repository.{Index, Repository, Status}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class StatusTest extends FlatSpec with BeforeAndAfterEach {
  val currentPath: String = System.getProperty("user.dir")
  val filename = "filetest"
  val filename2 = "filetest2"
  val filename3 = "filetest3"
  val textcontent = "testcontent"
  val repoDir: String = Repository.getSgitName

  override def beforeEach(): Unit = {
    Repository.createRepository(currentPath)
  }

  override def afterEach(): Unit = {
    IO.deleteRecursively(new File(IO.buildPath(List(System.getProperty("user.dir"), ".sgit"))))
  }

  it should "list new files before first commit" in {
    IO.createFile(currentPath, filename, textcontent)
    IO.createFile(currentPath, filename2, textcontent)
    IO.createFile(currentPath, filename3, textcontent)

    Blob.treatBlob(List(filename, filename2))

    val list = Status.listNewFilesFirstCommit.getOrElse(List())

    assert(list == List("new file: filetest", "new file: filetest2"))
  }

  it should "list new files empty before first commit" in {
    IO.createFile(currentPath, filename, textcontent)
    IO.createFile(currentPath, filename2, textcontent)
    IO.createFile(currentPath, filename3, textcontent)

    val list = Status.listNewFilesFirstCommit.getOrElse(List())

    assert(list == List())
  }

  it should "return the modified file not staged" in {
    IO.createFile(currentPath, filename, textcontent)
    IO.createFile(currentPath, filename2, textcontent)

    Blob.treatBlob(List(filename, filename2))

    IO.writeInFile(currentPath + File.separator + filename2, "grhyjy", false)
    val list = Status.changesNotStaged.getOrElse(List(Map()))

    assert(list.head.head._2 == "modified" && list.head.head._1 == filename2)
  }

  it should "return the deleted file not staged" in {
    IO.createFile(currentPath, filename, textcontent)
    IO.createFile(currentPath, filename2, textcontent)

    Blob.treatBlob(List(filename, filename2))

    val file = new File(currentPath + File.separator + filename2)
    file.delete()

    val list = Status.changesNotStaged.getOrElse(List(Map()))

    assert(list.head.head._2 == "deleted" && list.head.head._1 == filename2)
  }

  it should "return the deleted file not committed" in {
    IO.createFile(currentPath, filename, textcontent)
    IO.createFile(currentPath, filename2, textcontent)

    AddCmd.add(List(filename, filename2))

    Commit.commit("test")

    val file = new File(currentPath + File.separator + filename2)
    file.delete()

    AddCmd.add(List(filename))
    val index = Index.getIndex.getOrElse(List())
    println(index)

    val commit = Commit.getLastCommitIndex
    println(commit)

    val list = Status.changesNotCommitted.getOrElse(List(Map()))
    println(list)
    assert(list.head.head._2 == "deleted" && list.head.head._1 == filename2)
  }

  /*it should "return the untracked files" in {
    IO.createFile(currentPath, filename, textcontent)
    IO.createFile(currentPath, filename2, textcontent)
    IO.createFile(currentPath, filename3, textcontent)

    Blob.treatBlob(List(filename, filename2))
    val list = Status.getUntrackedFiles

    assert(list.contains(filename3))
  }*/
}
