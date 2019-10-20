package core.commands

import core.objects.Tag
import core.repository.Repository
import utils.io.IO

object TagCmd {

  /**
   * Function to create a tag.
   *
   * @param repository Repository
   * @param tagName    the tag name
   * @return message in String format
   */
  def tag(repository: Repository, tagName: String): String = {
    if (tagName != "") {
      // Check if a tag already exists
      if (Tag.tagExists(repository, tagName))
        s"Tag $tagName already exists."
      else {
        val commit = repository.currentBranch.commit
        Tag.tag(Repository.pathToRefsTags(repository), tagName, commit)
      }
    }
    else {
      // List all the tags
      if (repository.tags.nonEmpty)
        IO.listToString(repository.tags.map(_.tagName + "\n"))
      else
        "No tag."
    }
  }
}