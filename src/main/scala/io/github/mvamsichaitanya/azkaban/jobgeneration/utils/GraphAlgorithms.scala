package io.github.mvamsichaitanya.azkaban.jobgeneration.utils

import scala.collection.mutable
import scala.collection.immutable

object GraphAlgorithms {
  /**
    * Checks whether graph is cyclic or not
    * Cycle check is performed on all nodes whose inDegree is zero
    *
    * @param graph [[mutable.ListMap]] representing graph
    * @tparam T Type of graph
    * @return false if graph is acyclic
    */
  def isCyclic[T](graph: mutable.ListMap[T, immutable.Seq[T]],
                  name: String): Boolean = {

    /**
      * Recursive method to perform DFS(Depth first search) and detect cycle
      *
      * @param visited [[mutable.Map]] represent whether given node is visited or not
      * @param elem    : Node on which operation is performed
      * @param parent  : Parent of elem
      * @return false if cycle is not detected
      */
    def detectCycleRecursively(visited: mutable.Map[T, Boolean],
                               elem: T,
                               parent: Option[T]): Boolean = {
      if (visited.getOrElse(elem, false))
        throw new Exception(s"cycle detected between $elem and ${parent.get} in flow $name")

      visited(elem) = true
      val childNodes = graph(elem)
      childNodes.foreach(node => if (detectCycleRecursively(visited, node, Option(elem))) return true)

      false
    }

    val zeroInDegreeNodes = getZeroInDegreeNodes[T](graph: mutable.ListMap[T, immutable.Seq[T]])
    zeroInDegreeNodes.
      foreach(node => detectCycleRecursively(mutable.Map[T, Boolean](), node, None))

    false
  }

  /**
    *
    * @param graph [[mutable.ListMap]] representing graph
    * @tparam T Type of graph
    * @return Seq of nodes whose inDegree is zero
    */
  def getZeroInDegreeNodes[T](graph: mutable.ListMap[T, immutable.Seq[T]]): Seq[T] = {
    val inDegrees = mutable.Map[T, Int]()
    graph.foreach(node => {

      val childNodes = node._2
      childNodes.foreach(childNode => {
        inDegrees.put(childNode, inDegrees.getOrElse(childNode, 0) + 1)
      })
    })
    val inDegreeNodes = inDegrees.keys.toSeq

    graph.keys.toSeq.filterNot(inDegreeNodes.contains)
  }

  /**
    * Checks whether given graph is forest or not By following steps
    * 1) converting Directed graph to unDirected graph
    * 2) performing BFT(Breadth First Traversal) from one node and mark all nodes visited
    * 3) check whether all nodes are visited or not
    *
    * @param nodes      Seq of nodes present in graph
    * @param graph      [[mutable.ListMap]] representing graph
    * @param isDirected flag indicating type of graph
    * @tparam T Type of graph
    * @return true if graph is not a forest
    */
  def isConnected[T](nodes: immutable.Seq[T],
                     graph: mutable.ListMap[T, immutable.Seq[T]],
                     name: String,
                     isDirected: Boolean = true): Boolean = {

    val unDirectedGraph = graph.clone()
    val visited = mutable.Map[T, Boolean]()

    if (isDirected)
      graph.foreach(tuple => {
        val parent = tuple._1
        val childNodes = tuple._2
        childNodes.
          foreach(node => unDirectedGraph.update(node, parent +: unDirectedGraph(node)))
      })

    /**
      * Breadth First Traversal
      */
    def performBFT(): Unit = {

      val queue = mutable.Queue[T]()
      val startingNode = graph.head._1
      queue.enqueue(startingNode)
      visited(startingNode) = true
      while (queue.nonEmpty) {
        val node = queue.dequeue()
        val childNodes = unDirectedGraph(node)

        childNodes.foreach(cn => {
          if (!visited.getOrElse(cn, false)) {
            queue.enqueue(cn)
            visited(cn) = true
          }
        })
      }
    }

    performBFT()

    val visitedNodes = visited.keys.toSeq
    val unVisitedNodes = nodes.filterNot(visitedNodes.contains)
    if (unVisitedNodes.lengthCompare(0) > 0)
      throw new Exception(s"connectivity not found in flow $name between ${unVisitedNodes.mkString(",")} " +
        s"and ${visitedNodes.mkString(",")}")
    true
  }

  /**
    *
    * @param graph Graph
    * @param nodes All nodes of graph
    * @tparam T Type of graph
    * @return Seq of leaf nodes
    */
  def getLeafNodes[T](graph: mutable.ListMap[T, immutable.Seq[T]],
                      nodes: immutable.Seq[T]): Seq[T] = {

    val nodesWithOutDegrees = graph.filter(tuple => tuple._2.nonEmpty).keys.toSeq
    val zeroOutDegreeNodes = nodes.filterNot(nodesWithOutDegrees.contains)
    zeroOutDegreeNodes
  }
}
