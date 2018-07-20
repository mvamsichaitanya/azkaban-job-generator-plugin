package com.kohls.bigdata.dse.azkaban.jobgeneration.utils

import com.kohls.bigdata.dse.azkaban.jobgeneration.elements.Flow

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

  /**
    * Check whether duplicate jobs are present or not in flows.xml file
    *
    * @param flows Sequence of flows
    */
  def validateFlows(flows: Seq[Flow]): Unit = {
    val jobVisited = mutable.Map[String, Boolean]()
    flows.foreach(
      flow => {
        flow.jobs.foreach(job => {
          val jobName = job.name
          if (!jobVisited.getOrElse(jobName, false))
            jobVisited(jobName) = true
          else
            throw new Exception(s"$jobName job is found more than one time in flows.xml file")
        })
      }
    )
  }

}
