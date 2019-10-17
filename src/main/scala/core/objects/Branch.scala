package core.objects

import core.repository.Repository
import utils.io.{IO, SgitIO}
import utils.parser.Printer

object Branch {

  /**
   * Function to create a Branch.
   *
   * @param branchName the name of the Branch
   */
  def branch(branchName: String): Unit = {
    val listBranches = getAllBranches.map(_.split(IO.getRegexFileSeparator).last)
    if (listBranches.contains(branchName))
      Printer.displayln("Branch already exists.")
    else {
      createBranch(branchName) match {
        case Left(error) => Printer.displayln(error)
        case Right(value) => Printer.displayln(s"Branch ${branchName} created.")
      }
    }
  }

  /**
   * Function to create a Branch.
   *
   * @param branchName the name of the Branch
   * @return Either left: error message, Either right: null
   */
  private def createBranch(branchName: String): Either[String, Any] = {
    getCurrentBranch match {
      case Left(error) => Left(error)
      case Right(value) =>
        Repository.getPathToRefHeads match {
          case Left(value) => Left(value)
          case Right(path) =>
            IO.readContentFile(IO.buildPath(List(path, value))) match {
              case Left(error) => Left(error)
              case Right(value) =>
                IO.createFile(path, branchName, IO.listToString(value))
                Right(null)
            }
        }
    }
  }

  /**
   * Function to get the current Branch name.
   *
   * @return Either left: error message, Either right: the Branch name in String format.
   */
  def getCurrentBranch: Either[String, String] = {
    Repository.getPathToHead match {
      case Left(error) => Left(error)
      case Right(result) =>
        IO.readContentFile(result) match {
          case Left(error) => Left(error)
          case Right(result) =>
            Right(IO.listToString(result).split(IO.getRegexFileSeparator).last)
        }
    }
  }

  def displayBranches(): Unit = {
    val branches = getAllBranches
      .filter(x => x.nonEmpty)
      .map(x => if (x == getCurrentBranch.getOrElse("")) "-> " + x + "\n" else "   " + x + "\n")
    if (branches.isEmpty)
      Printer.displayln("No branches.")
    else
      Printer.displayln("Branches: \n\n" + IO.listToString(branches))
  }

  def displayBranchesDetails(): Unit = {
    val branches = getAllBranches
      .filter(x => x.nonEmpty)
      .map(x => Map(x -> IO.readContentFile(IO.buildPath(List(Repository.getPathToRefHeads.getOrElse(""), x))).getOrElse(List()).head))
      .map(x => {
        val nameBranch = x.head._1.split(IO.getRegexFileSeparator).last
        if (nameBranch == getCurrentBranch.getOrElse("")) "-> " +
          nameBranch + " " + x.head._2 + "\n" else "   " +
          nameBranch + " " + x.head._2 + "\n"
      })
    if (branches.isEmpty)
      Printer.displayln("No branches.")
    else
      Printer.displayln("Branches: \n\n" + IO.listToString(branches))
  }

  /**
   * Function to get all the branches.
   *
   * @return list of branches
   */
  def getAllBranches: List[String] = {
    Repository.getPathToRefHeads match {
      case Left(value) => List()
      case Right(value) =>
        SgitIO.listFiles(value).map(_.split(IO.getRegexFileSeparator).last)
    }
  }

  def branchExists(branchName: String): Boolean = {
    getAllBranches.contains(branchName)
  }

  def getBranchCommit(branchName: String): Either[String, String] = {
    Repository.getPathToRefHeads match {
      case Left(error) => Left(error)
      case Right(value) =>
        val path = IO.buildPath(List(value, branchName))
        IO.readContentFile(path) match {
          case Left(error) => Left(error)
          case Right(value) => Right(value.head)
        }
    }
  }
}