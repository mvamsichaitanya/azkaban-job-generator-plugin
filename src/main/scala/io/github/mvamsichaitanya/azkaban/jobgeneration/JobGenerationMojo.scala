package io.github.mvamsichaitanya.azkaban.jobgeneration

import io.github.mvamsichaitanya.azkaban.jobgeneration.constants.Constants._
import io.github.mvamsichaitanya.azkaban.jobgeneration.elements.{Flow, Job}
import io.github.mvamsichaitanya.azkaban.jobgeneration.utils.Graph
import io.github.mvamsichaitanya.azkaban.jobgeneration.utils.ValidationUtils._
import io.github.mvamsichaitanya.azkaban.jobgeneration.utils.Utils._
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.{Mojo, Parameter}

import scala.collection.immutable
import scala.xml.{Elem, Node}

/**
  * goal of the plugin "job_generation"
  */

@Mojo(name = "job_generation")
class JobGenerationMojo extends AbstractMojo {


  /**
    * Parameters for plugin
    */
  /**
    * path where zip file and job file to be generated
    * default value is project.build.directory
    */
  @Parameter(defaultValue = "${project.build.directory}")
  private val outputDirectory: String = null

  /**
    * path of flows.xml file
    * default value is project.basedir/src/main/resources/flows.xml
    */
  @Parameter(defaultValue = "${project.basedir}/src/main/resources")
  private val flowsPath: String = null

  @Parameter(defaultValue = "${project.basedir}/src/main/resources")
  private val propertiesPath: String = null

  /**
    * names of the flows file
    * default value is flows.xml
    */
  @Parameter(defaultValue = "flows.xml")
  private val jobsFile: String = null

  /**
    * Name of the zip file
    * default value is azkaban
    */
  @Parameter(defaultValue = "azkaban")
  private val zipFile: String = null


  /**
    * Return List of flows in the xml file
    *
    * @param xmlFile xml file
    * @return List of [[Flow]]
    */
  def getFlows(xmlFile: Elem): Seq[Flow] = {

    val flows = xmlFile \\ FlowsStr \\ FlowStr

    flows.map(flow => {
      val name = flow.attribute(Name) match {
        case None => throw new Exception("Flow name is not defined \n Attribute 'name' is mandatory for the flow")
        case _ => flow.attribute(Name).get.text
      }
      val jobs = getJobs(flow)
      Flow(name, jobs, createGraph(jobs), getFlowPropFiles(flow))
    })
  }

  /**
    * Returns List of jobs in Flow [[Node]]
    *
    * @param node Flow Node
    * @return List of [[Job]]
    */
  def getJobs(node: Node): immutable.Seq[Job] = (node \\ JobStr).map(Job.fromXml)

  /**
    *
    * @param flow : Flow node
    * @return Seq of property files
    */
  def getFlowPropFiles(flow: Node): Seq[String] = (flow \\ PropertyFiles \\ File).map(_.text)

  /**
    *
    * @param xmlFile : XML File
    * @return Seq of common property files
    */
  def getCommonPropertyFiles(xmlFile: Elem): Seq[String] =
    (xmlFile \\ FlowsStr \ PropertyFiles \\ File).map(_.text)

  /**
    * Creates graph for given list of jobs
    *
    * @param jobs : List of [[Job]]
    * @return Graph of jobs
    */
  def createGraph(jobs: immutable.Seq[Job]): Graph[String] = {

    val graph = new Graph[String](jobs.map(_.name).toList)

    val jobsWithName: Map[String, Job] = jobs.map(job => (job.name, job)).toMap
    jobsWithName.keys.foreach(name => graph.add(name, List.empty[String]))

    jobsWithName.foreach(jobWithName => {

      val job = jobWithName._2
      val name = jobWithName._1

      if (job.dependency.nonEmpty) {
        val dependencies = job.dependency
        dependencies.foreach(dependency => {
          graph.add(dependency, name)
        })
      }
    })
    graph
  }

  /**
    * Main execution method
    * Steps:-
    * 1)load xml file
    * 2)get all flows present in xml file
    * 3)validates flow
    * 4)Create directories for all flows
    * 5)generated job files for all flows in their respective directories
    * 6)copy flow property files to their respective flow directories
    * 7)copy common property files
    * 8)make zip file for all flows
    */
  override def execute(): Unit = {

    val inputFlowsPath = flowsPath
    val inputPropPath = propertiesPath
    val jobsFileName = jobsFile
    val outputPath = outputDirectory
    val zipName = zipFile
    val xmlFile = xml.XML.loadFile(s"$inputFlowsPath/$jobsFileName")
    val flows = getFlows(xmlFile)
    val flowsOutputDir = s"$outputPath/$zipName"
    val commonPropFiles = getCommonPropertyFiles(xmlFile)

    createDirectory(flowsOutputDir)
    addPropFiles(commonPropFiles, inputPropPath, flowsOutputDir)

    flows.foreach(flow => {
      validateFlow(flow.graph)
      val flowOutputDir = s"$flowsOutputDir/${flow.name}"
      createDirectory(flowOutputDir)
      generateJobFiles(flow, flowOutputDir)
      addPropFiles(flow.propFiles, inputPropPath, flowOutputDir)
    })

    makeZip(flowsOutputDir)
  }

}