package com.kohls.bigdata.dse.azkaban.jobgeneration

import com.kohls.bigdata.dse.azkaban.jobgeneration.constants.Constants._
import com.kohls.bigdata.dse.azkaban.jobgeneration.elements.{Flow, Job}
import com.kohls.bigdata.dse.azkaban.jobgeneration.utils.ValidationUtils._
import com.kohls.bigdata.dse.azkaban.jobgeneration.utils.Utils._
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
    * Return List of flows in the xml file
    *
    * @param xmlFile xml file
    * @return List of [[Flow]]
    */
  def getFlows(xmlFile: Elem): Seq[Flow] = {

    val flows = xmlFile \\ FlowsStr \\ FlowStr

    flows.map(flow => {
      val name = flow.attribute(Name).get.text
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

    val output = outputDirectory
    val xmlFile = xml.XML.loadFile(jobsFile)
    val flows = getFlows(xmlFile)

    validateFlows(flows)
    flows.foreach(flow => {
      validateFlow(flow.graph)
      generateJobFiles(flow, output)
    })

    makeZip(flows, output)

  }

}
