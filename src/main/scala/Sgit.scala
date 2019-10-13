import core.commands.{AddCmd, InitCmd}
import core.objects.Commit
import utils.io.{IO, SgitIO}
import utils.parser.Parser._

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => config.mode match {
      case "init" => InitCmd.init(IO.getCurrentPath)
      case "add" => AddCmd.add(config.filesAdd)
      case "commit" => Commit.commit(config.commitName)
      case "test" => print(SgitIO.listFiles(System.getProperty("user.dir"))) // TODO DELETE THIS AT THE END
      case _ => print(config)
    }
    case _ => print("No args given")
  }
}
