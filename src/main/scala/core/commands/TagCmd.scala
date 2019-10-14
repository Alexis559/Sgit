package core.commands

import core.objects.Tag
import core.repository.Repository

object TagCmd {

  /**
   * Function to create a tag.
   *
   * @param tagName the tag name
   */
  def tag(tagName: String): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => print(error)
      case Right(_) =>
        if (tagName != "")
          Tag.tag(tagName)
        else
          Tag.listTag()
    }
  }
}
