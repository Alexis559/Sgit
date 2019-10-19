package core.commands

import core.repository.ImpureRepository
import utils.io.IO
import utils.parser.Config

object Dispatcher {

  def dispatch(config: Config): String = {
    // We try to get the Repository at the current path
    ImpureRepository.chargeRepo(IO.getCurrentPath) match {
      case Left(error) => error
      case Right(repository) =>
        config.mode match {
          case "add" => AddCmd.add(repository, config.filesAdd)
          case "commit" => CommitCmd.commit(repository, config.commitName)
          case "status" => StatusCmd.status(repository)
          case "tag" => TagCmd.tag(repository, config.tagName)
          case "diff" => DiffCmd.diff(repository)
          case "branch" => if (config.verboseBranch) BranchCmd.branchList(repository, true) else BranchCmd.branch(repository, config.branchName)
          case "checkout" => CheckoutCmd.checkout(repository, config.branchName)
          //case "test" => Printer.displayln(Checkout.recreateWorkingDirectory("test").toString)
        }
    }
  }
}
