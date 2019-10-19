package coreTest.repository

import java.io.File
import java.nio.file.Files

import core.commands.{AddCmd, CommitCmd, InitCmd}
import core.objects.Commit
import core.repository.{ImpureRepository, Repository, Status}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.{IO, SgitIO}

class StatusTest extends FlatSpec with BeforeAndAfterEach {
  val currentPath: String = System.getProperty("user.dir")
  val filename = "filetest"
  val filename2 = "filetest2"
  val filename3 = "filetest3"
  val textcontent = "testcontent"
  var repoDir: String = ""
  var repository: Repository = null

  override def beforeEach(): Unit = {
    repoDir = Files.createTempDirectory("RepoTestSgit").toString
    InitCmd.init(repoDir)
    repository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
  }

  override def afterEach(): Unit = {
    //IO.deleteRecursively(new File(repoDir))
  }

  it should "list new files before first commit" in {

    IO.createFile(repoDir, filename, textcontent)
    IO.createFile(repoDir, filename2, textcontent)
    IO.createFile(repoDir, filename3, textcontent)

    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename)), IO.buildPath(List(repoDir, filename2))))

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo.index.getOrElse(List())

    val list = Status.listNewFilesFirstCommit(index)

    assert(list == List("new file: filetest", "new file: filetest2"))
  }

  it should "list new files empty before first commit" in {
    IO.createFile(repoDir, filename, textcontent)
    IO.createFile(repoDir, filename2, textcontent)
    IO.createFile(repoDir, filename3, textcontent)

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo.index.getOrElse(List())

    val list = Status.listNewFilesFirstCommit(index)

    assert(list == List())
  }

  it should "return the modified file not staged" in {
    IO.createFile(repoDir, filename, textcontent)
    IO.createFile(repoDir, filename2, textcontent)

    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename)), IO.buildPath(List(repoDir, filename2))))

    IO.writeInFile(repoDir + File.separator + filename2, "grhyjy", false)

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo.index.getOrElse(List())
    val list = Status.changesNotStaged(repository, index)

    assert(list.head.head._2 == "modified" && list.head.head._1 == filename2)
  }

  it should "return the deleted file not staged" in {

    IO.createFile(repoDir, filename, textcontent)
    IO.createFile(repoDir, filename2, textcontent)

    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename)), IO.buildPath(List(repoDir, filename2))))

    val file = new File(repoDir + File.separator + filename2)
    file.delete()

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo.index.getOrElse(List())

    val list = Status.changesNotStaged(repository, index)

    assert(list.head.head._2 == "deleted" && list.head.head._1 == filename2)
  }

  it should "return the deleted file not committed" in {
    IO.createFile(repoDir, filename, textcontent)
    IO.createFile(repoDir, filename2, textcontent)

    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename)), IO.buildPath(List(repoDir, filename2))))

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    CommitCmd.commit(newRepo, "test")

    val file = new File(repoDir + File.separator + filename2)
    file.delete()

    val newRepo2 = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    AddCmd.add(newRepo2, List(IO.buildPath(List(repoDir, filename))))

    val newRepo3 = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val index = newRepo3.index.getOrElse(List())
    val commitMap = Commit.commitToMap(newRepo3, newRepo3.currentBranch.commit).getOrElse(Map())
    val commitList = Commit.commitToList(commitMap)
    val list = Status.changesNotCommitted(newRepo3, index, commitList)

    assert(list.head.head._2 == "deleted" && list.head.head._1 == filename2)
  }

  it should "return the untracked files" in {
    IO.createFile(repoDir, filename, textcontent)
    IO.createFile(repoDir, filename2, textcontent)
    IO.createFile(repoDir, filename3, textcontent)

    AddCmd.add(repository, List(IO.buildPath(List(repoDir, filename)), IO.buildPath(List(repoDir, filename2))))

    val newRepo = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    val index = newRepo.index.getOrElse(List()).map(_.fileName)
    val files = SgitIO.listFiles(repoDir)
    val list = Status.getUntrackedFiles(newRepo, index, files)

    assert(list.contains(filename3))
  }
}
