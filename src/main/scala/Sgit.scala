import utils.Parser._

object Sgit extends App {
  getConfig(args) match {
    case Some(config) => print(config)
    case None =>
  }
}
