package utils

import java.io.File

case class Config(
                   mode: String = "",
                   filesAdd: Seq[File] = Seq(),
                   regexAdd: String = "",
                   patchLog: Boolean = false,
                   statLog: Boolean = false,
                   allBranch: Boolean = false,
                   verboseBranch: Boolean = false,
                   branchName: String = "",
                   tagName: String = "",
                   interactive: Boolean = false,
                 )