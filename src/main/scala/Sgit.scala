import core.commands.{Add, Init}
import core.objects.{Commit}
import utils.io.IO
import utils.parser.Config
import utils.parser.Parser._

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => config match {
      case Config("init",_,_,_,_,_,_,_,_,_) => Init.createRepository(IO.getCurrentPath)
      case Config("add",_,_,_,_,_,_,_,_,_) => Add.add(config.filesAdd)
      case Config("test",_,_,_,_,_,_,_,_,_) => Commit.treatCommit() // TODO DELETE THIS AT THE END
      case _ => print(config)
    }
    case _ => print("No args given")
  }
}
