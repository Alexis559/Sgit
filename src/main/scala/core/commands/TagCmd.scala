package core.commands

import core.objects.Tag
import core.repository.Repository
import utils.parser.Printer

object TagCmd {

  /**
   * Function to create a tag.
   *
   * @param tagName the tag name
   */
  def tag(tagName: String): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(_) =>
        if (tagName != "")
          Tag.tag(tagName)
        else
          Tag.listTag()
    }
  }
}
