package io.github.mvamsichaitanya.azkaban.jobgeneration.utils

import io.github.mvamsichaitanya.azkaban.jobgeneration.elements.Flow
import scala.collection.{immutable, mutable}

object ValidationUtils {


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

    if (graph.isCyclic)
      throw new Exception("Cycle found in graph")
    if (!graph.isConnected)
      throw new Exception("more than one DAG found")
  }

}
