package core.objects

import java.io.File

import core.repository.{Index, Repository, Status}
import utils.io.IO
import utils.parser.Printer

object Checkout {
  def checkout(branchName: String): Unit = {
    if (!Branch.branchExists(branchName))
      Printer.displayln(s"Branch $branchName doesn't exist.")
    else {
      verifChanges match {
        case Left(value) => Printer.displayln(value)
        case Right(value) =>
          if (value) {
            updateHead(branchName) match {
              case Left(error) => Printer.displayln(error)
              case Right(value) =>
                deleteWorkingDirectory() match {
                  case Left(error) => Printer.displayln(error)
                  case Right(value) =>
                    if (value)
                      recreateWorkingDirectory(branchName)
                }

            }
          }
      }
    }
  }

  def verifChanges: Either[String, Boolean] = {
    Status.changesNotStaged match {
      case Left(error) => Left(error)
      case Right(value) =>
        if (value.isEmpty) {
          Status.changesNotCommitted match {
            case Left(value) => Left(value)
            case Right(value) =>
              if (value.isEmpty)
                Right(true)
              else {
                Printer.displayln("You need to commit the changes.")
                Right(false)
              }
          }
        } else {
          Printer.displayln("You need to stage the changes.")
          Right(false)
        }
    }
  }

  def updateHead(branchName: String): Either[String, Boolean] = {
    Repository.getPathToHead match {
      case Left(error) => Left(error)
      case Right(value) =>
        IO.writeInFile(value, "ref: " + IO.buildPath(List("refs", "head", "master", branchName)), false)
        Right(true)
    }
  }

  def deleteWorkingDirectory(): Either[String, Boolean] = {
    Index.getIndex match {
      case Left(error) => Left(error)
      case Right(index) =>
        index.map(x => IO.deleteFile(x.head._1))
        Repository.getPathToIndex match {
          case Left(error) => Left(error)
          case Right(value) =>
            IO.writeInFile(value, "", false)
        }
        Right(true)
    }
  }

  def recreateWorkingDirectory(branchName: String): Either[String, Boolean] = {
    Branch.getBranchCommit(branchName) match {
      case Left(error) => Left(error)
      case Right(value) =>
        Commit.commitToList(value) match {
          case Left(error) => Left(error)
          case Right(commit) =>
            commit.map(_.split(" "))
              .map(x => x(0).substring(Repository.getRepoName.getOrElse("").length + 1) + " " + x(1))
              .map(_.split(" "))
              .foreach(x => {
                val pathRepo = Repository.getPathToParenSgit.getOrElse("")
                if (x(0).contains(File.separator)) {
                  IO.createDirectory(Repository.getPathToParenSgit.getOrElse(""), x(0).substring(0, x(0).lastIndexOf(File.separator)))
                }
                IO.createFile(pathRepo, x(0).split(IO.getRegexFileSeparator).last, IO.listToString(IO.readContentFile(Object.getObjectFilePath(x(1)).getOrElse("")).getOrElse(List())))
                IO.writeInFile(Repository.getPathToIndex.getOrElse(""), x(1) + " " + x(0), true)
              })
            Right(false)
        }
    }
  }
}
