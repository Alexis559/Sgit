import core.Test
import core.commands._
import core.objects.Checkout
import utils.io.IO
import utils.parser.Parser._
import utils.parser.Printer

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => config.mode match {
      case "init" => InitCmd.init(IO.getCurrentPath)
      case "add" => AddCmd.add(config.filesAdd)
      case "commit" => CommitCmd.commit(config.commitName)
      case "status" => Test.time(StatusCmd.status())
      case "tag" => TagCmd.tag(config.tagName)
      case "diff" => DiffCmd.diff()
      case "branch" => Test.time(if (config.verboseBranch) BranchCmd.branchList() else BranchCmd.branch(config.branchName))
      case "checkout" => CheckoutCmd.checkout(config.branchName)
      case "test" => Printer.displayln(Checkout.recreateWorkingDirectory("test").toString) // TODO DELETE THIS AT THE END
      case _ => Printer.displayln(config.toString)
    }
    case _ => Printer.displayln("No args given")
  }
}
