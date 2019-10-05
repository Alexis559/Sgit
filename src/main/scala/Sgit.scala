import core.init.Init
import utils.parser.Config
import utils.parser.Parser._

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => config match {
      case Config(init,List(),_,false,false,false,false,_,_,false) => Init.createRepository(System.getProperty("user.dir"))
      case _ => print("pffff")
    }
    case _ => print("No args given")
  }
}
