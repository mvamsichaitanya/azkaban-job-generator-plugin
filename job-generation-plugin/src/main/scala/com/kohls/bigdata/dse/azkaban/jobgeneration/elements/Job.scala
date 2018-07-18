package com.kohls.bigdata.dse.azkaban.jobgeneration.elements

import com.kohls.bigdata.dse.azkaban.jobgeneration.constants.Constants._

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
               dependency: Seq[String] = Nil)

object Job {

  /**
    * Generate Job object from xml [[Node]]
    *
    * @param node xml job Node
    * @return [[Job]] object
    */
  def fromXml(node: Node): Job = {
    Job(
      name = node.attribute(Name).get.text,
      command = (node \\ Command).text,
      arguments = (node \\ Arguments).text,
      dependency = {
        val value = (node \\ Dependency).text.trim
        if (value.isEmpty) Nil else value.split(Comma).toSeq
      }
    )
  }

}