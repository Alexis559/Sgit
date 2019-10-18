package core.objects

import core.objects.Commit.{getCommit, getLastCommit}
import utils.io.IO
import utils.parser.Printer

object Log {

  /**
   * Logs
   */
  def log(): Unit = {
    getLastCommit match {
      case Left(error) => Printer.displayln(error)
      case Right(sha) =>
        commitParentRec(sha)

        @scala.annotation.tailrec
        def commitParentRec(sha: String): Unit = {
          getCommit(sha) match {
            case Left(error) => Printer.displayln(error)
            case Right(value) =>
              val commit = value.map(x => x.split("\n")).dropWhile(!_ (0).contains("message")).flatten
              val parent = value.map(x => x.split(" ")).filter(_ (0) == "parent").flatten
              Printer.displayln(s"commit ${sha} (HEAD -> ${Branch.getCurrentBranch.getOrElse("")}, tag: ${IO.listToString(Tag.getTags.getOrElse(List()))}) \n\n ${IO.listToString(commit).replace("message", "")} \n\n")
              if (parent.tail.head != "nil")
                commitParentRec(parent.tail.head)
          }
        }
    }
  }
}
