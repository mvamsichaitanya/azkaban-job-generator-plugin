package io.github.mvamsichaitanya.azkaban.jobgeneration.elements

import io.github.mvamsichaitanya.azkaban.jobgeneration.constants.Constants._
import io.github.mvamsichaitanya.azkaban.jobgeneration.enums.JobTypes
import io.github.mvamsichaitanya.azkaban.jobgeneration.enums.JobTypes.Type

/**
  * type Command Job
  *
  * @param name       Name of the job
  * @param command    Command to be executed
  * @param arguments  Arguments for the command
  * @param dependency Dependency of Sequence of jobs
  */
case class CommandJob(override val name: String,
                      command: String,
                      arguments: String = EmptyString,
                      override val dependency: Seq[String] = Nil,
                      workingDir: String = EmptyString,
                      retries: String = EmptyString,
                      retryBackoff: String = EmptyString,
                      failureEmails: String = EmptyString,
                      successEmails: String = EmptyString,
                      notifyEmails: String = EmptyString) extends Job {
  override def jobType: Type = JobTypes.Command
}