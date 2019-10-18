package core.objects

import java.io.File

import core.repository.Repository
import utils.io.{IO, SgitIO}
import utils.parser.Printer

object Tag {

  /**
   * Function to create a tag.
   *
   * @param tagName the tag name
   */
  def tag(tagName: String): Unit = {
    Commit.isFirstCommit match {
      case Left(error) => Printer.displayln(error)
      case Right(first) =>
        if (first)
          Printer.displayln("No commit to tag.")
        else {
          Commit.getLastCommit match {
            case Left(error) => Printer.displayln(error)
            case Right(shaCommit) =>
              Repository.getPathToRefTags match {
                case Left(error) => Printer.displayln(error)
                case Right(tagsPath) =>
                  IO.createFile(tagsPath, tagName, shaCommit)
              }
          }
        }
    }
  }

  def listTag(): Unit = {
    getTags match {
      case Left(error) => Printer.displayln(error)
      case Right(value) =>
        if (value.nonEmpty)
          Printer.displayln(IO.listToString(value.map(x => x + "\n")))
        else
          Printer.displayln("No tag.")
    }
  }

  /**
   * Function to get all the tags.
   */
  def getTags: Either[String, List[String]] = {
    Repository.getPathToRefTags match {
      case Left(error) => Left(error)
      case Right(tagsPath) =>
        val tags = SgitIO.listFiles(tagsPath)
        Right(tags.map(x => new File(x).getName))
    }
  }

}
