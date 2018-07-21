package com.github.vamsi.azkaban.jobgeneration.utils

import java.io._
import java.util.zip.{ZipEntry, ZipOutputStream}
import com.github.vamsi.azkaban.jobgeneration.elements.{Flow, Job}
import scala.collection.immutable

object Utils {

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
    *
    * @param driPath path of the directory
    */
  def createDirectory(driPath: String): Unit = {
    val path = new File(driPath)
    val creation = path.mkdir()
    if (!creation)
      throw new Exception(s"failed to create directory $path")
  }

  /**
    * Generates job files for given Sequence of jobs
    *
    * @param flow       flow of type  [[Flow]]
    * @param outputPath : output path where job files to be generated
    */
  def generateJobFiles(flow: Flow, outputPath: String): Unit = {

    val jobs = flow.jobs
    val flowName = flow.name
    val file = new File(outputPath + s"/$flowName.job")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("type=noop\n")
    val endJobs = flow.graph.leafNodes.mkString(",")
    bw.write(s"dependencies=$endJobs")
    bw.close()

    jobs.foreach(job => {
      val file = new File(outputPath + s"/${job.name}.job")
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write("type=command\n")
      bw.write(s"command=${job.command} ${job.arguments}\n")
      bw.write(s"working.dir=${job.workingDir}\n")
      bw.write(s"retries=${job.retries}\n")
      bw.write(s"retry.backoff=${job.retryBackoff}\n")
      bw.write(s"failure.emails=${job.failureEmails}\n")
      bw.write(s"success.emails=${job.successEmails}\n")
      bw.write(s"notify.emails=${job.notifyEmails}\n")
      bw.write(s"dependencies=${job.dependency.mkString(",")}")
      bw.close()
    })

  }

  /**
    * Make zip for Sequence of flows mentioned in flows.xml
    *
    * @param flows      Sequence of [[Flow]]
    * @param outputPath output path where zip file to be generated
    */
  def makeZip(flows: Seq[Flow], outputPath: String, zipName: String): Unit = {
    val zip = new ZipOutputStream(new FileOutputStream(outputPath + s"/$zipName.zip"))
    val flowsOutputDir = outputPath + s"/$zipName"
    flows.foreach { flow => {

      val flowOutputDir = flowsOutputDir + s"/${flow.name}"
      val flowName = flow.name
      val path = flowOutputDir + s"/$flowName.job"
      zip.putNextEntry(new ZipEntry(s"/$flowName.job"))
      val in = new BufferedInputStream(new FileInputStream(path))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()

      flow.jobs.foreach(job => {
        val path = flowOutputDir + s"/${job.name}.job"
        zip.putNextEntry(new ZipEntry(s"/${job.name}.job"))
        val in = new BufferedInputStream(new FileInputStream(path))
        var b = in.read()
        while (b > -1) {
          zip.write(b)
          b = in.read()
        }
        in.close()
        zip.closeEntry()
      })
    }
    }
    zip.close()
  }


}
