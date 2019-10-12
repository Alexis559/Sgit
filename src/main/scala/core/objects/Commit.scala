package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}

object Commit {

  // Name of the file where is stored the last commit message
  val nameFile = "COMMIT_EDITMSG"

  /**
   * Function to create the commit.
   *
   * @param messageCommit message to give to the commit
   */
  def commit(messageCommit: String): Unit = {
    // We build the commit tree with the content of the index file
    Tree.buildTree match {
      case Left(error) => print(error)
      case Right(result1) => {
        // We check if the file that contains the commit message exists if not we create it and we write the message in it
        val pathFile = IO.buildPath(List(Repository.getRepositoryPath().getOrElse(""), nameFile))
        if (!IO.fileExist(pathFile))
          IO.createFile(Repository.getRepositoryPath().getOrElse(""), nameFile, messageCommit)
        else
          IO.writeInFile(pathFile, messageCommit, false)

        var commitContent = ""
        val pathToRefHeads = Repository.getPathToRefHeads.getOrElse("")
        val currentBranch = Branch.getCurrentBranch.getOrElse("")
        val shaTree = IO.listToString(result1)

        // We get the sha1 of the last commit if there is one (nil if there's not) and we create a the commit file
        if (IO.fileExist(IO.buildPath(List(pathToRefHeads, currentBranch)))) {
          getLastCommit match {
            case Left(error) => println(error)
            case Right(result2) => {
              commitContent = generateCommitContent(IO.listToString(result1), result2, messageCommit)
            }
          }
        }
        else {
          IO.createFile(pathToRefHeads, currentBranch, "")
          commitContent = generateCommitContent(shaTree, "nil", messageCommit)
        }

        val shaCommit = SgitIO.sha(commitContent)
        Object.createObject(shaCommit, commitContent)

        // We update the ref to the new commit tree
        updateCommitBranch(shaCommit) match {
          case Left(error) => print(error)
          case Right(result) =>
        }
      }
    }
  }

  /**
   * Function to generate the commit file content.
   *
   * @param treeSha   the sha1 to the new tree commit
   * @param parentSha the sha1 to the old commit file
   * @param message   the commit message
   * @return the commit file content in String format
   */
  private def generateCommitContent(treeSha: String, parentSha: String, message: String): String = {
    treeSha + "\n" + "parent " + parentSha + "\n" + "message " + message + "\n"
  }

  /**
   * Function to get the sha1 of the last commit.
   *
   * @return Either left: error message, Either right: the sha1 of the last commit
   */
  private def getLastCommit: Either[String, String] = {
    Branch.getCurrentBranch match {
      case Left(error) => Left(error)
      case Right(result1) => {
        Repository.getPathToRefHeads match {
          case Left(error) => Left(error)
          case Right(result2) => {
            IO.readContentFile(IO.buildPath(List(result2, result1))) match {
              case Left(error) => Left(error)
              case Right(result3) => Right(IO.listToString(result3))
            }
          }
        }
      }
    }
  }

  /**
   * Function to update the ref to the last commit.
   *
   * @param newSha the sha to the new commit file
   * @return Either left: error message, Either right: null
   */
  def updateCommitBranch(newSha: String): Either[String, Any] = {
    Branch.getCurrentBranch match {
      case Left(error) => Left(error)
      case Right(result1) => {
        Repository.getPathToRefHeads match {
          case Left(error) => Left(error)
          case Right(result2) => {
            IO.writeInFile(IO.buildPath(List(result2, result1)), newSha, false)
            Right(null)
          }
        }
      }
    }
  }
}
