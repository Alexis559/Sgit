import core.commands._
import utils.io.{IO, Printer}
import utils.parser.Parser._

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => config.mode match {
      case "init" => Printer.displayln(InitCmd.init(IO.getCurrentPath))
      case _ => Printer.displayln(Dispatcher.dispatch(config))
    }
    case _ => Printer.displayln("No args given.")
  }
}
