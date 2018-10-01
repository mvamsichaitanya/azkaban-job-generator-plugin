package io.github.mvamsichaitanya.azkaban.jobgeneration.utils

import scala.collection.{immutable, mutable}
import scala.xml.Node
import io.github.mvamsichaitanya.azkaban.jobgeneration.constants.Constants.CommandJobParameters

object ValidationUtils {

  /**
    * throw exception if parameters other than `CommandJobParameters` are present
    *
    * @param node Command job node
    */
  def validateCommandJob(node: Node): Unit = {
    val wrongParams = node.child.filter(_.text.trim.nonEmpty).map(_.label).filterNot(CommandJobParameters.contains)
    if (wrongParams.nonEmpty)
      throw new Exception(s" params ${wrongParams.mkString(",")} are not supported \n " +
        s"Command Job Parameters should be from ${CommandJobParameters.mkString(",")}" +
        s"\n keep additional parameters in flow specific property file")
  }

  /**
    *
    * @param graph [[mutable.ListMap]] representing graph
    * @tparam T Type of graph
    * @return true if all dependencies are present with in nodes of graph
    */
  def validateDependencies[T](graph: mutable.ListMap[T, immutable.Seq[T]]): Boolean = {
    val allDependencies = graph.flatMap(_._2).filter(_ != None)
    val allJobs = graph.keys.toSeq
    val unIdentifiedDependencies = allDependencies.filterNot(allJobs.contains)

    if (unIdentifiedDependencies.nonEmpty)
      throw new Exception(s"${unIdentifiedDependencies.toString()} jobs are not found")

    true
  }

  /**
    * Check whether given graph is a DAG or not
    *
    * @param graph [[mutable.ListMap]] representing graph
    */
  def validateFlow(graph: Graph[String]): Unit = {

    graph.validateDependencies()

    if (graph.containsDuplicates)
      throw new Exception(s"duplicate jobs are present in ${graph.name}")

    if (graph.isCyclic)
      throw new Exception(s"Cycle found in flow ${graph.name}")
//    if (!graph.isConnected)
//      throw new Exception(s"more than one DAG found in flow ${graph.name}")
  }
}
