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

  /**
   * Function to list all the tags.
   */
  def listTag(): Unit = {
    Repository.getPathToRefTags match {
      case Left(error) => Printer.displayln(error)
      case Right(tagsPath) =>
        val tags = SgitIO.listFiles(tagsPath)
        Printer.displayln(IO.listToString(tags.map(x => new File(x).getName + "\n")))
    }
  }

}
