package coreTest.objects

import core.objects.Branch
import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class BranchTest extends FlatSpec with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    Repository.createRepository(System.getProperty("user.dir"))
  }

  it should "return the current Branch name" in {
    assert(Branch.getCurrentBranch.getOrElse("") == "master")
  }

}
