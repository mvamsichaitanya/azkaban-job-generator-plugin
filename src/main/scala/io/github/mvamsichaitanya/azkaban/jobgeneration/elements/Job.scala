package io.github.mvamsichaitanya.azkaban.jobgeneration.elements

import io.github.mvamsichaitanya.azkaban.jobgeneration.constants.Constants._

import scala.xml.Node

/**
  *
  * @param name       Name of the job
  * @param command    Command to be executed
  * @param arguments  Arguments for the command
  * @param dependency Dependency of Sequence of jobs
  */
case class Job(name: String,
               command: String,
               arguments: String = EmptyString,
               dependency: Seq[String] = Nil,
               workingDir: String = EmptyString,
               retries: String = EmptyString,
               retryBackoff: String = EmptyString,
               failureEmails: String = EmptyString,
               successEmails: String = EmptyString,
               notifyEmails: String = EmptyString)

object Job {

  /**
    * Generate Job object from xml [[Node]]
    *
    * @param node xml job Node
    * @return [[Job]] object
    */
  def fromXml(node: Node): Job = {
    Job(
      name = node.attribute(Name) match {
        case None => throw new Exception("Job name is not defined \n Attribute 'name' is mandatory for job")
        case _ => node.attribute(Name).get.text
      },
      command = (node \\ Command).text,
      arguments = (node \\ Arguments).text,
      dependency = {
        val value = (node \\ Dependency).text.trim
        if (value.isEmpty) Nil else value.split(Comma).toSeq
      },
      workingDir = (node \\ workingDirStr).text,
      retries = (node \\ retriesStr).text,
      retryBackoff = (node \\ retryBackoffStr).text,
      failureEmails = (node \\ failureEmailsStr).text,
      successEmails = (node \\ successEmailsStr).text,
      notifyEmails = (node \\ notifyEmails).text
    )
  }

}