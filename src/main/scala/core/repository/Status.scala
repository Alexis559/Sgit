package core.repository

import java.io.File

import core.objects.{Branch, Commit, Object}
import utils.io.{IO, SgitIO}
import utils.parser.Printer

object Status {

  /**
   * Function to print the status of the repository.
   */
  def status(): Unit = {
    Branch.getCurrentBranch match {
      case Left(error) => Printer.displayln(error)
      case Right(branch) =>
        getUntrackedFiles match {
          case Left(error) => Printer.displayln(error)
          case Right(untracked) =>
            changesNotStaged match {
              case Left(error) => Printer.displayln(error)
              case Right(notstaged) =>
                Commit.isFirstCommit match {
                  case Left(error) => Printer.displayln(error)
                  case Right(first) =>
                    Printer.displayln("On branch " + branch + "\n")
                    if (!first) {
                      // If it's not the first commit then we can get diff with the last commit
                      changesNotCommitted match {
                        case Left(error) => Printer.displayln(error)
                        case Right(notcommitted) =>
                          Printer.displayln("\nChanges to be committed: ")
                          printMapStatus(notcommitted)
                      }
                    }
                    // If it's the first commit and the index is not empty then we display it's content
                    else if (first && IO.readContentFile(Repository.getPathToIndex.getOrElse("")).getOrElse(List()).nonEmpty) {
                      listNewFilesFirstCommit match {
                        case Left(value) => Printer.displayln(value)
                        case Right(value) =>
                          Printer.displayln("\nChanges to be committed: ")
                          Printer.displayln("\n" + listToStringStatus(value))
                      }
                    } else {
                      Printer.displayln("\nNo commits yet\n")
                    }
                    Printer.displayln("\nChanges not staged for commit:\n  \t(use \"git add <file>...\" to update what will be committed)")
                    printMapStatus(notstaged)
                    Printer.displayln("\nUntracked files:\n  \t(use \"git add <file>...\" to include in what will be committed)\n")
                    Printer.displayln("\n" + listToStringStatus(untracked))
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
  def listNewFilesFirstCommit: Either[String, List[String]] = {
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
        // List of files that exists
        val filesExisting = indexMap.filter(file => Repository.isFileInRepo(file.head._1))
        // List of files that doesn't exist anymore
        val filesDeleted = indexMap.filterNot(file => Repository.isFileInRepo(file.head._1))

        var mapFilesDeleted = List[Map[String, String]]()
        var mapFilesModified = List[Map[String, String]]()

        mapFilesDeleted = filesDeleted.map(x => {
          Map(x.head._1 -> "deleted")
        })

        val pathRepo = Repository.getRepositoryPath().getOrElse("")

        mapFilesModified = filesExisting.filter(x => (Object.returnNewSha(IO.buildPath(List(pathRepo.replace(Repository.getSgitName, ""), x.head._1))).getOrElse("") != x.head._2))
          .map(x => Map(x.head._1 -> "modified"))

        Right(mapFilesDeleted ::: mapFilesModified)
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
        var filesDeleted = List[Map[String, String]]()
        var filesModified = List[Map[String, String]]()
        var filesAdded = List[Map[String, String]]()
        Commit.getLastCommitIndex match {
          case Left(error) => Left(error)
          case Right(indexCommit) => {
            val commitFlat = indexCommit.flatMap(x => x.split(" "))
            val indexKeys = indexMap.flatMap(x => x.keySet)
            val indexValues = indexMap.map(x => x.head._2)

            filesDeleted = indexCommit
              .map(_.split(" ")(0))
              .filterNot(indexKeys.contains(_))
              .map(x => Map(x -> "deleted"))

            filesModified = indexCommit
              .map(_.split(" "))
              .filter(x => indexKeys.contains(x(0)) && !indexValues.contains(x(1)))
              .map(y => Map(y(0) -> "modified"))

            filesAdded = indexMap
              .filterNot(x => commitFlat.contains(x.head._1))
              .map(y => Map(y.head._1 -> "added"))
          }
            Right(filesDeleted ::: filesModified ::: filesAdded)
        }
    }
  }

  /**
   * Function to print a Map.
   *
   * @param map the Map to print
   */
  def printMapStatus(map: List[Map[String, String]]): Unit = {
    Printer.displayln("\n")
    Printer.displayln(map.map(x => {
      ("\t" + x.head._2 + ": " + x.head._1 + "\n")
    }).mkString)
  }

  /**
   * Function to convert a List to a String
   *
   * @param list the List to convert in String format
   * @return the List in String format
   */
  def listToStringStatus(list: List[String]): String = {
    list.map(x => "\t" + x + "\n").mkString
  }
}