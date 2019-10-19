package core.objects

import core.repository.Repository
import utils.io.IO

case class Branch(branchName: String, commit: String)

object Branch {
  /**
   * Function to create a Branch.
   *
   * @param repository Repository
   * @param branchName the name of the Branch
   * @return message in String format
   */
  def branch(repository: Repository, branchName: String): String = {
    createBranch(repository, branchName) match {
      case Left(error) => error
      case Right(value) => value
    }
  }

  /**
   * Function to create a Branch.
   *
   * @param repository Repository
   * @param branchName the name of the Branch
   * @return Either left: error message, Either right: message
   */
  private def createBranch(repository: Repository, branchName: String): Either[String, String] = {
    repository.commit match {
      case Left(error) => Left(error)
      case Right(value) =>
        IO.createFile(Repository.pathToRefsHead(repository), branchName, value.sha)
        Right(s"Branch $branchName created.")
    }
  }

  /**
   * Function to get a Branch by it's name.
   *
   * @param repository Repository
   * @param branchName the name of the Branch
   * @return message in String format
   */
  def getBranchCommit(repository: Repository, branchName: String): Branch = {
    repository.branches.filter(x => x.branchName == branchName).head
  }

  /**
   * Function to check if a Branch exists.
   *
   * @param repository Repository
   * @param branchName the Branch name
   * @return true if exists else false
   */
  def branchExists(repository: Repository, branchName: String): Boolean = {
    repository.branches.exists(x => x.branchName == branchName)
  }
}