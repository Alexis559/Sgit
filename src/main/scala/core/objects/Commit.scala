package core.objects

import java.io.File

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
      case Right(result1) =>
        // We check if the file that contains the commit message exists if not we create it and we write the message in it
        val pathFile = IO.buildPath(List(Repository.getRepositoryPath().getOrElse(""), nameFile))
        if (!IO.fileExist(pathFile))
          IO.createFile(Repository.getRepositoryPath().getOrElse(""), nameFile, messageCommit)
        else
          IO.writeInFile(pathFile, messageCommit, append = false)

        var commitContent = ""
        val pathToRefHeads = Repository.getPathToRefHeads.getOrElse("")
        val currentBranch = Branch.getCurrentBranch.getOrElse("")
        val shaTree = IO.listToString(result1)

        // We get the sha1 of the last commit if there is one (nil if there's not) and we create a the commit file
        if (IO.fileExist(IO.buildPath(List(pathToRefHeads, currentBranch)))) {
          getLastCommit match {
            case Left(error) => println(error)
            case Right(result2) =>
              commitContent = generateCommitContent(IO.listToString(result1), result2, messageCommit)
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
          case Right(_) =>
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
  def getLastCommit: Either[String, String] = {
    Branch.getCurrentBranch match {
      case Left(error) => Left(error)
      case Right(result1) =>
        Repository.getPathToRefHeads match {
          case Left(error) => Left(error)
          case Right(result2) =>
            IO.readContentFile(IO.buildPath(List(result2, result1))) match {
              case Left(error) => Left(error)
              case Right(result3) => Right(IO.listToString(result3))
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
      case Right(result1) =>
        Repository.getPathToRefHeads match {
          case Left(error) => Left(error)
          case Right(result2) =>
            IO.writeInFile(IO.buildPath(List(result2, result1)), newSha, append = false)
            Right(null)
        }
    }
  }

  /**
   * Function to get the files and their hash of the last commit
   *
   * @return Either left: error message, Either right: List of the paths of the files with their sha1
   */
  def getLastCommitIndex: Either[String, List[String]] = {
    Commit.getLastCommit match {
      case Left(error) => Left(error)
      case Right(value) =>
        Object.getObjectFilePath(value) match {
          case Left(error) => Left(error)
          case Right(value) =>
            IO.readContentFile(value) match {
              case Left(error) => Left(error)
              case Right(value) =>
                val sha = value.head.split(" ")
                commitToList(sha(1)) match {
                  case Left(error) => Left(error)
                  case Right(value) => Right(value)
                }
            }
        }
    }
  }

  /**
   * Function to get the files and their hash of the last commit in a Map format
   *
   * @param shaCommit the sha of the commit
   * @return Either left: error message, Either right: Map of the paths of the files with their sha1
   */
  def commitToMap(shaCommit: String): Either[String, Map[String, Any]] = {
    Object.getObjectFilePath(shaCommit) match {
      case Left(error) => Left(error)
      case Right(pathCommit) =>
        IO.readContentFile(pathCommit) match {
          case Left(error) => Left(error)
          case Right(contentCommit) =>
            var listBlob = Map[String, Any]()
            var listTree = Map[String, Any]()

            listBlob = contentCommit.map(x => x.split(" "))
              .filter(x => x(0) == "blob")
              .map(x => x(2) -> x(1))
              .toMap

            listTree = contentCommit.map(x => x.split(" "))
              .filter(x => x(0) == "tree")
              .map(x => x(2) -> commitToMap(x(1)).getOrElse(Map()))
              .toMap


            /*contentCommit.foreach(x => {
              val line = x.split(" ")
              if (line(0) == "blob") {
                listMap = listMap + (line(2) -> line(1))
              } else if (line(0) == "tree") {
                listMap = listMap + (line(2) -> commitToMap(line(1)).getOrElse(Map()))
              }
            })*/
            Right(listBlob ++ listTree)
        }
    }
  }

  /**
   * Function to get the files and their hash of the last commit in a List format
   *
   * @param shaCommit the sha of the commit
   * @return Either left: error message, Either right: List of the paths of the files with their sha1
   */
  def commitToList(shaCommit: String): Either[String, List[String]] = {
    Commit.commitToMap(shaCommit) match {
      case Left(value) => Left(value)
      case Right(value) =>
        commitMapToListRec(value)
    }
  }

  /**
   * Function to know if a commit already exists
   *
   * @return Either left: error message, Either right: true if there's no commit done or false if there"s already a commit
   */
  def isFirstCommit: Either[String, Boolean] = {
    Branch.getCurrentBranch match {
      case Left(error) => Left(error)
      case Right(result1) =>
        Repository.getPathToRefHeads match {
          case Left(error) => Left(error)
          case Right(result2) =>
            Right(!IO.fileExist(IO.buildPath(List(result2, result1))))
        }
    }
  }

  /**
   * Function to convert the Commit in Map format to a List format.
   *
   * @param mapCommit the Commit in Map format
   * @return Either left: error message, Either right: the Commit in a List format
   */
  private def commitMapToListRec(mapCommit: Map[String, Any]): Either[String, List[String]] = {
    var list = List[String]()
    mapCommit.foreach(x => {
      var path = List[String]()
      if (x._1.contains(".txt") || x._2.asInstanceOf[String].length == 40) {
        list = x._1 + " " + x._2 :: list
      } else {
        path = path ::: commitMapToListRec(x._2.asInstanceOf[Map[String, Any]]).getOrElse(List()).map(p => x._1 + File.separator + p)
        list = path ::: list
      }
    })
    Right(list)
  }
}
