import core.commands._
import utils.io.IO
import utils.parser.Parser._

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => config.mode match {
      case "init" => InitCmd.init(IO.getCurrentPath)
      case "add" => AddCmd.add(config.filesAdd)
      case "commit" => CommitCmd.commit(config.commitName)
      case "status" => StatusCmd.status()
      case "tag" => TagCmd.tag(config.tagName)
      case "test" => //print(Commit.commitMapToList(Commit.commitToMap(Commit.getLastCommit.getOrElse("")).getOrElse(Map()))) // TODO DELETE THIS AT THE END
      case _ => print(config)
    }
    case _ => print("No args given")
  }
}
