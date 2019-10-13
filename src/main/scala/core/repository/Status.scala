package core.repository

import java.io.File

import core.objects.{Branch, Commit, Object}
import utils.io.{IO, SgitIO}

object Status {

  /**
   * Function to print the status of the repository.
   */
  def status(): Unit = {
    Branch.getCurrentBranch match {
      case Left(error) => print(error)
      case Right(branch) =>
        getUntrackedFiles match {
          case Left(error) => print(error)
          case Right(untracked) =>
            changesNotStaged match {
              case Left(error) => print(error)
              case Right(notstaged) =>
                Commit.isFirstCommit match {
                  case Left(error) => print(error)
                  case Right(first) =>
                    println("On branch " + branch + "\n")
                    if (!first) {
                      // If it's not the first commit then we can get diff with the last commit
                      changesNotCommitted match {
                        case Left(error) => print(error)
                        case Right(notcommitted) =>
                          println("\nChanges to be committed: ")
                          printMapStatus(notcommitted)
                      }
                    }
                    // If it's the first commit and the index is not empty then we display it's content
                    else if (first && IO.readContentFile(Repository.getPathToIndex.getOrElse("")).getOrElse(List()).nonEmpty) {
                      listTrackedFiles match {
                        case Left(value) => println(value)
                        case Right(value) =>
                          println("\nChanges to be committed: ")
                          println("\n" + listToStringStatus(value))
                      }
                    } else {
                      println("\nNo commits yet\n")
                    }
                    println("\nChanges not staged for commit:\n  \t(use \"git add <file>...\" to update what will be committed)")
                    printMapStatus(notstaged)
                    println("\nUntracked files:\n  \t(use \"git add <file>...\" to include in what will be committed)\n")
                    println("\n" + listToStringStatus(untracked))
                }
            }
        }
    }
  }

  /**
   * Function to get the text to print the untracked files.
   *
   * @return Either left: error message, Either right: the index in a List of String
   */
  def listTrackedFiles: Either[String, List[String]] = {
    Index.getTrackedFiles match {
      case Left(value) => Left(value)
      case Right(value) =>
        var text = List[String]()
        text = value.map(x => "new file: " + x)
        Right(text)
    }
  }

  /**
   * Function to get the list of untracked files.
   *
   * @return
   */
  def getUntrackedFiles: Either[String, List[String]] = {
    Repository.getRepositoryPath() match {
      case Left(error) => Left(error)
      case Right(repoPath) =>
        Index.getTrackedFiles match {
          case Left(error) => Left(error)
          case Right(indexFiles) =>
            // We get all the files in the repository
            var listFiles = SgitIO.listFiles(new File(repoPath).getParent)
            listFiles = listFiles.map(IO.cleanPathFile(_).getOrElse(""))
            // And we return only the ones who are not in the index
            Right(listFiles.filterNot(indexFiles.toSet))
        }
    }
  }

  /**
   * Function to get the list of changes in the repository.
   *
   * @return
   */
  def changesNotStaged: Either[String, List[Map[String, String]]] = {
    Index.getIndex match {
      case Left(error) => Left(error)
      case Right(indexMap) =>
        var index = List[Map[String, String]]()
        // List of files that exists
        val filesExisting = indexMap.filter(file => Repository.isFileInRepo(file.head._1))
        // List of files that doesn't exist anymore
        val filesDeleted = indexMap.filterNot(file => Repository.isFileInRepo(file.head._1))

        index = filesDeleted.map(x => {
          Map(x.head._1 -> "deleted")
        })

        val pathRepo = Repository.getRepositoryPath().getOrElse("")

        filesExisting.foreach(x => {
          // If the new sha is different of the old one then the file has been modified
          if (Object.returnNewSha(IO.buildPath(List(pathRepo.replace(Repository.getSgitName, ""), x.head._1))).getOrElse("") != x.head._2) {
            index = Map(x.head._1 -> "modified") :: index
          }
        })
        Right(index)
    }
  }

  /**
   * Function to get the difference with the index and the last commit.
   *
   * @return Either left: error message, Either right: the difference in a List of Map
   */
  def changesNotCommitted: Either[String, List[Map[String, String]]] = {
    Index.getIndex match {
      case Left(error) => Left(error)
      case Right(indexMap) =>
        var files = List[Map[String, String]]()
        Commit.getLastCommitIndex match {
          case Left(error) => Left(error)
          case Right(indexCommit) => {
            val commitFlat = indexCommit.flatMap(x => x.split(" "))
            val indexKeys = indexMap.flatMap(x => x.keySet)
            val indexValues = indexMap.map(x => x.head._2)
            indexCommit.foreach(x => {
              val line = x.split(" ")
              if (!indexKeys.contains(line(0)))
                files = Map(line(0) -> "deleted") :: files
              else if (indexKeys.contains(line(0)) && !indexValues.contains(line(1)))
                files = Map(line(0) -> "modified") :: files
            })
            indexMap.foreach(x => {
              if (!commitFlat.contains(x.head._1))
                files = Map(x.head._1 -> "added") :: files
            })
          }
            Right(files)
        }
    }
  }

  /**
   * Function to print a Map.
   *
   * @param map the Map to print
   */
  def printMapStatus(map: List[Map[String, String]]): Unit = {
    println("\n")
    map.foreach(x => {
      println("\t" + x.head._2 + ": " + x.head._1)
    })
  }

  /**
   * Function to convert a List to a String
   *
   * @param list the List to convert in String format
   * @return the List in String format
   */
  def listToStringStatus(list: List[String]): String = {
    val string: StringBuilder = new StringBuilder()
    list.foreach(x => string.append("\t" + x + "\n"))
    string.toString()
  }
}