# Sgit [![Build Status](https://travis-ci.com/Alexis559/Sgit.svg?token=p1zyBPLTvXKpz2oSwz7z&branch=master)](https://travis-ci.com/Alexis559/Sgit)
[IG5] Scala project: A Scala-based git-like code source manager.<br/>
<br/>
Download the project, then go to the directory and open a terminal and execute the following command:
sbt assembly

An executable should be created in the source folder at “target/scala-2.13”.

You need to add “source folder/target/scala-2.13” (where source folder is the path to the project you downloaded) to the “path” variable of your operating system then you will be able to execute the program from anywhere on your computer as long as you do not change of place the executable.

Here is a list of the commands that are implemented in this program:

Create:
- sgit init

Local Changes:
- sgit status
- sgit diff
- sgit add <filename/filenames or . or regexp>
- git commit

Commit History:
- sgit log
  Show all commits started with newest


Branches and Tags:
- sgit branch <branch name>
  Create a new branch
- sgit branch -v
  List all existing branches
- sgit checkout <branch name>
- sgit tag <tag name>

