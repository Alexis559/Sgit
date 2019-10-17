package core.commands

import core.objects.Checkout
import core.repository.Repository
import utils.parser.Printer


object CheckoutCmd {
  /**
   * Function to change of Branch.
   */
  def checkout(branchName: String): Unit = {
    Repository.getRepositoryPath() match {
      case Left(error) => Printer.displayln(error)
      case Right(_) => Checkout.checkout(branchName)
    }
  }
}
