package utils.parser

import java.io.File

import scopt.OptionParser

/**
 * Parser of the arguments with Scopt
 */
object Parser {
  def getParser(): OptionParser[Config] = {
    new scopt.OptionParser[Config]("sgit") {
      head("sgit", "1.0")

      cmd("init")
        .action((_, c) => c.copy(mode = "init"))
        .text("Create a Sgit repository in the current directory.")
      cmd("status")
        .action((_, c) => c.copy(mode = "status"))
        .text("Show the working tree status.")
      cmd("diff")
        .action((_, c) => c.copy(mode = "diff"))
        .text("Show changes between commits, commit and working tree, etc...")
      cmd("add")
        .action((_, c) => c.copy(mode = "add"))
        .text("Add file contents to the index.")
        .children(
          arg[File]("<file>...")
            .optional()
            .unbounded()
            .action((x, c) => c.copy(filesAdd = c.filesAdd :+ x))
            .text("List of files to add."),
          arg[String]("/regex/")
            .action((x, c) => c.copy(regexAdd = x))
            .text("Regular expression to add files.")
        )
      cmd("log")
        .action((_, c) => c.copy(mode = "log"))
        .text("Show commit logs.")
        .children(
          opt[Unit]('p', "patch")
            .optional()
            .action((_, c) => c.copy(patchLog = true))
            .text("Show changes overtime."),
          opt[Unit]('s', "stat")
            .optional()
            .action((_, c) => c.copy(statLog = true))
            .text("Show stats about changes overtime."),
        )
      cmd("branch")
        .action((_, c) => c.copy(mode = "branch"))
        .text("Create a new branch.")
        .children(
          arg[String]("<branch name>")
            .optional()
            .action((x, c) => c.copy(branchName = x))
            .text("The branch name."),
          opt[Unit]('a', "all")
            .optional()
            .action((_, c) => c.copy(allBranch = true))
            .text("Show stats about changes overtime."),
          opt[Unit]('v', "verbose")
            .optional()
            .action((_, c) => c.copy(verboseBranch = true))
            .text("?"),
        )
      cmd("checkout")
        .action((_, c) => c.copy(mode = "checkout"))
        .text("Switch branches or restore working tree files.")
        .children(
          arg[String](name = "<branch or tag or commit hash>")
            .action((x, c) => c.copy(branchName = x))
            .required()
            .text("Branch name or tag name or commit hash.")
        )
      cmd("tag")
        .action((_, c) => c.copy(mode = "tag"))
        .text("Create a tag.")
        .children(
          arg[String](name = "<tag name>")
            .action((x, c) => c.copy(tagName = x))
            .required()
            .text("Tag name.")
        )
      cmd("merge")
        .action((_, c) => c.copy(mode = "merge"))
        .text("Join two or more development histories together.")
        .children(
          arg[String](name = "<branch>")
            .action((x, c) => c.copy(branchName = x))
            .required()
            .text("Branch name.")
        )
      cmd("rebase")
        .action((_, c) => c.copy(mode = "rebase"))
        .text("Reapply commits on top of another base tip.")
        .children(
          arg[String](name = "<branch>")
            .action((x, c) => c.copy(branchName = x)),
          opt[Unit]('i', "interactive")
            .optional()
            .action((_, c) => c.copy(interactive = true))
            .text("Make a list of the commits which are about to be rebased.")
            .children(
              arg[String]("<commit hash or banch name>")
                .action((x, c) => c.copy(branchName = x))
                .required()
                .text("Branch name or commit hash.")
            )
        )
    }
  }

  def getConfig(arguments: Array[String]): Option[Config] = getParser().parse(arguments, Config())

}
