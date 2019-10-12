package coreTest.objects

import core.objects.Branch
import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class BranchTest extends FlatSpec with BeforeAndAfterEach {

  val currentPath: String = System.getProperty("user.dir")

  override def beforeEach(): Unit = {
    Repository.createRepository(currentPath)
  }

  it should "return the current Branch name" in {
    assert(Branch.getCurrentBranch.getOrElse("") == "master")
  }

}
