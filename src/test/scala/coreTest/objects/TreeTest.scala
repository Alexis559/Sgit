package coreTest.objects

import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class TreeTest extends FlatSpec with BeforeAndAfterEach {

  val currentPath: String = System.getProperty("user.dir")

  override def beforeEach(): Unit = {
    Repository.createRepository(currentPath)
  }

  // TODO

}
