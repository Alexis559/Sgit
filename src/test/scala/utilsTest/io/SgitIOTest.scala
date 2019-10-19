package utilsTest.io

import java.io.File
import java.nio.file.Files

import core.repository.ImpureRepository
import org.scalatest.FlatSpec
import utils.io.{IO, SgitIO}

class SgitIOTest extends FlatSpec {

  it should "return the correct hash" in {
    assert(SgitIO.sha("testSha1") == "62f9fcdfc06f4499eb9573d0a4575de4cdbc586a")
  }

  it should "return false with a false hash" in {
    assert(SgitIO.sha("testSha1") != "123456dfc06f4499eb9573d0a4575de4cdbc586a")
  }

  it should "create a map from the list (1)" in {
    val list = List("test")
    val map = SgitIO.listToMap(list)

    assert(map.head._1 == "test" && map.head._2 == Map.empty)
  }

  it should "create a map from the list (2)" in {
    val list = List("test", "value")
    val map = SgitIO.listToMap(list)
    assert(map.head._1 == "value" && map.head._2.asInstanceOf[Map[String, Any]].head._1 == "test" && map.head._2.asInstanceOf[Map[String, Any]].head._2 == Map.empty)
  }

  it should "merge maps" in {
    val map1 = Map("test" -> Map.empty)
    val map2 = Map("test2" -> Map.empty)
    val map3 = SgitIO.mergeMaps(List(map1, map2))
    val size = map3.size
    val keys = map3.keySet

    assert(size == 2 && keys.contains("test") && keys.contains("test2"))

  }

  it should "return the list of files" in {
    val repoDir = Files.createTempDirectory("RepoTestSgit").toString

    ImpureRepository.createRepository(repoDir)
    val repository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)

    val seq = List("HEAD", "description", "index", "master")
    val files = SgitIO.listFilesRec(new File(IO.buildPath(List(repoDir, ".sgit")))).map(_.getName)

    assert(seq.sorted == files.sorted)
  }

  it should "return an empty list of files" in {
    val repoDir = Files.createTempDirectory("RepoTestSgit").toString

    ImpureRepository.createRepository(repoDir)
    val repository = ImpureRepository.chargeRepo(repoDir).getOrElse(null)
    val files = SgitIO.listFilesRec(new File(IO.buildPath(List(repoDir, ".sgit", "refs", "tags")))).map(_.getName)

    assert(files.isEmpty)
    IO.deleteRecursively(new File(repoDir))
  }

}