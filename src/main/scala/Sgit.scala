import core.commands.Init
import utils.io.IO
import utils.parser.Config
import utils.parser.Parser._
import core.objects.Blob

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => config match {
      case Config("init",_,_,_,_,_,_,_,_,_) => Init.createRepository(IO.getCurrentPath)
      case Config("add",_,_,_,_,_,_,_,_,_) => Blob.createBlob(config.filesAdd)
      case _ => print(config)
    }
    case _ => print("No args given")
  }
}
