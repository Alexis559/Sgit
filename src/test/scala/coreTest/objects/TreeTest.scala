package coreTest.objects

import core.repository.Repository
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class TreeTest extends FlatSpec with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    Repository.createRepository(System.getProperty("user.dir"))
  }

  // TODO

}
