package coreTest.objects

import java.io.File

import core.objects.Branch
import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import utils.io.IO

class BranchTest extends FlatSpec with BeforeAndAfterEach {

  val currentPath: String = System.getProperty("user.dir")

  override def beforeEach(): Unit = {
    Repository.createRepository(currentPath)
  }

  override def afterEach(): Unit = {
    IO.deleteRecursively(new File(IO.buildPath(List(System.getProperty("user.dir"), ".sgit"))))
  }

  it should "return the current Branch name" in {
    assert(Branch.getCurrentBranch.getOrElse("") == "master")
  }

}
