package utils.parser

// Config file used for Scopt parser
case class Config(
                   mode: String = "", // The command launched
                   filesAdd: List[String] = List(), // List of files added with the add command
                   regexAdd: String = "", // Regex used to select the files to add
                   patchLog: Boolean = false, // -p flag used for log command ?
                   statLog: Boolean = false, // -s flag used for log command ?
                   allBranch: Boolean = false, // -a flag used for branch command ?
                   verboseBranch: Boolean = false, // -v flag used for branch command ?
                   branchName: String = "", // Branch name
                   tagName: String = "", // Tag name
                   interactive: Boolean = false, // -i flag used for rebase command ?
                 )
