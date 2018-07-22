package io.github.mvamsichaitanya.azkaban.jobgeneration

import io.github.mvamsichaitanya.azkaban.jobgeneration.constants.Constants._
import io.github.mvamsichaitanya.azkaban.jobgeneration.elements.{Flow, Job}
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
  @Parameter(defaultValue = "${project.basedir}/src/main/resources/flows.xml")
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
      Flow(name, jobs, createGraph(jobs))
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
    * Main execution method
    * Steps:-
    * 1) load xml file
    * 2) get all flows present in xml file
    * 3)validates flow
    * 4)generated job files for all flows
    * 5)make zip file for all flows
    */
  override def execute(): Unit = {

    val outputPath = outputDirectory
    val zipName = zipFile
    val xmlFile = xml.XML.loadFile(jobsFile)
    val flows = getFlows(xmlFile)
    val flowsOutputDir = outputPath + s"/$zipName"
    createDirectory(flowsOutputDir)
    validateFlows(flows)
    flows.foreach(flow => {
      validateFlow(flow.graph)
      val flowOutputDir = flowsOutputDir + s"/${flow.name}"
      createDirectory(flowOutputDir)
      generateJobFiles(flow, flowOutputDir)
    })

    makeZip(flows, outputPath, zipName)

  }

}
