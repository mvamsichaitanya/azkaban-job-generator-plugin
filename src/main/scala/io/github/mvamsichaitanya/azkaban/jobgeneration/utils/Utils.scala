package io.github.mvamsichaitanya.azkaban.jobgeneration.utils

import java.io._
import better.files.File.root
import io.github.mvamsichaitanya.azkaban.jobgeneration.elements.Flow

object Utils {

  /**
    *
    * @param driPath path of the directory
    */
  def createDirectory(driPath: String): Unit = new File(driPath).mkdirs()

  /**
    *
    * @param bufferedWriter Buffered Writer
    * @param argument       Argument passed by user
    * @param parameter      parameter to be written to job file
    */
  def writeOptionalArguments(bufferedWriter: BufferedWriter,
                             argument: String,
                             parameter: String): Unit =
    if (argument.nonEmpty) bufferedWriter.write(parameter)


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
      writeOptionalArguments(bw, job.workingDir, s"working.dir=${job.workingDir}\n")
      writeOptionalArguments(bw, job.retries, s"retries=${job.retries}\n")
      writeOptionalArguments(bw, job.retryBackoff, s"retry.backoff=${job.retryBackoff}\n")
      writeOptionalArguments(bw, job.failureEmails, s"failure.emails=${job.failureEmails}\n")
      writeOptionalArguments(bw, job.successEmails, s"success.emails=${job.successEmails}\n")
      writeOptionalArguments(bw, job.notifyEmails, s"notify.emails=${job.notifyEmails}\n")
      bw.write(s"dependencies=${job.dependency.mkString(",")}")
      bw.close()
    })

  }

  /**
    * Adds specified seq of files to destination directory
    *
    * @param propFiles : Seq of file names
    * @param filesPath : Path of the files
    * @param outputDir : Output directory where files to be copied
    */
  def addPropFiles(propFiles: Seq[String],
                   filesPath: String,
                   outputDir: String): Unit =
    propFiles.foreach(file => {
      (root / s"$filesPath/$file").copyTo(root / s"$outputDir/$file")
    })

  /**
    *
    * @param directoryPathToZip : Directory path to zip
    */
  def makeZip(directoryPathToZip: String): Unit =
    (root / directoryPathToZip).zipTo(root / s"$directoryPathToZip.zip")

}
