package io.github.mvamsichaitanya.azkaban.jobgeneration.utils

import scala.collection.{mutable, immutable}

/**
  * Generic Directed graph of type `T`
  **/

case class Graph[T](nodes: immutable.Seq[T], name: String = "graph") {

  type parent = T
  type childNodes = immutable.Seq[T]

  /**
    * [[mutable.ListMap]] representing Graph
    */
  val graph: mutable.ListMap[parent, childNodes] = mutable.ListMap[parent, childNodes]()

  /**
    * Adds child node or nodes to the parent node
    *
    * @param parent    Parent node
    * @param childNode child node || Sequence of child nodes
    */
  def add(parent: T, childNode: T): Unit =
    graph.update(parent, childNode +: graph.getOrElse(parent, Nil))

  def add(parent: T, childNodes: childNodes): Unit =
    graph.update(parent, childNodes ++ graph.getOrElse(parent, Nil))

  /**
    * Validates whether All child nodes are present in [[nodes]]
    */
  def validateDependencies(): Boolean = ValidationUtils.validateDependencies[T](graph)

  /**
    * Checks whether cycle is present in graph or not
    *
    * @return true if cyclic
    */
  def isCyclic: Boolean = GraphAlgorithms.isCyclic[T](graph, name)

  /**
    * Checks whether given graph is forest or not
    *
    * @return true if not a forest
    */
  def isConnected: Boolean = GraphAlgorithms.isConnected[T](nodes, graph, name)

  /**
    * @return true if duplicate nodes are found
    */
  def containsDuplicates: Boolean =
    nodes.lengthCompare(nodes.distinct.length) != 0

  def leafNodes: Seq[T] = GraphAlgorithms.getLeafNodes[T](graph, nodes)

  override def toString: String = graph.mkString("\n")
}
