package io.github.mvamsichaitanya.azkaban.jobgeneration.elements

import io.github.mvamsichaitanya.azkaban.jobgeneration.enums.JobTypes
import io.github.mvamsichaitanya.azkaban.jobgeneration.enums.JobTypes.Type

import scala.xml.Node

/**
  * type Custom job
  *
  * @param name       name of the job
  * @param node       XML Node  containing elements of job
  * @param dependency Dependency of the job
  */
case class CustomJob(override val name: String,
                     node: Node,
                     override val dependency: Seq[String]) extends Job {

  override def jobType: Type = JobTypes.Custom

}
