package io.github.mvamsichaitanya.azkaban.jobgeneration.elements

import io.github.mvamsichaitanya.azkaban.jobgeneration.constants.Constants._
import io.github.mvamsichaitanya.azkaban.jobgeneration.enums.JobTypes.Type
import scala.xml.Node

trait Job {

  def jobType: Type

  val name: String

  val dependency: Seq[String]

}

object Job {

  /**
    * Generate Job object from xml [[Node]]
    *
    * @param node xml job Node
    * @return [[CommandJob]] object
    */
  def fromXml(node: Node): Job = {

    if ((node \\ CustomJobStr).text == True) {
      CustomJob(
        name = node.attribute(Name) match {
          case None => throw new Exception("Job name is not defined \n Attribute 'name' is mandatory for job")
          case _ => node.attribute(Name).get.text
        },
        node = node,
        dependency = {
          val value = (node \\ Dependency).text.trim
          if (value.isEmpty) Nil else value.split(Comma).toSeq
        }
      )
    }
    else {
      CommandJob(
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
        workingDir = (node \\ WorkingDirStr).text,
        retries = (node \\ RetriesStr).text,
        retryBackoff = (node \\ RetryBackoffStr).text,
        failureEmails = (node \\ FailureEmailsStr).text,
        successEmails = (node \\ SuccessEmailsStr).text,
        notifyEmails = (node \\ NotifyEmails).text
      )
    }
  }

}