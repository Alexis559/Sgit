package core.objects

import core.repository.Repository
import utils.io.IO

case class Tag(tagName: String, commit: String)

object Tag {

  /**
   * Function to create a Tag.
   *
   * @param tagsPath  path to the refs/tags
   * @param tagName   the Tag name
   * @param shaCommit the sha of the Commit where the Tag will refer to
   * @return message in String format
   */
  def tag(tagsPath: String, tagName: String, shaCommit: String): String = {
    IO.createFile(tagsPath, tagName, shaCommit)
    s"Tag $tagName created.\n$tagName -> $shaCommit"
  }

  /**
   * Function to know a Tag already exists.
   *
   * @param repository Repository
   * @param tagName    the Tag name
   * @return true if the Tag already exists else false
   */
  def tagExists(repository: Repository, tagName: String): Boolean = {
    repository.tags.exists(x => x.tagName == tagName)
  }
}
