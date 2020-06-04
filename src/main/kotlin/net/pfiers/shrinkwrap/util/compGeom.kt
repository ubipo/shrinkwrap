package net.pfiers.shrinkwrap.util

import net.pfiers.shrinkwrap.angle
import net.pfiers.shrinkwrap.exception.IterationLimitExceededException
import net.pfiers.shrinkwrap.exception.NoInnerConcaveHullException
import net.pfiers.shrinkwrap.exception.UnconnectedStartNodeException
import net.pfiers.shrinkwrap.mostCounterclockwise
import org.openstreetmap.josm.data.coor.LatLon
import org.openstreetmap.josm.data.osm.Node
import org.openstreetmap.josm.data.osm.Way
import java.awt.geom.Path2D

/**
 * Jarvis march alg, adapted from https://en.wikipedia.org/wiki/Gift_wrapping_algorithm
 */
fun convexHull(nodes: LinkedHashSet<Node>): List<Node> {
    if (nodes.isEmpty())
        throw IllegalArgumentException("<nodes> must not be empty")

    val hull = ArrayList<Node>()

    // Leftmost node
    val l = nodes.minBy(Node::lon)!! // Checked with isEmpty()

    var p = l
    do {
        hull.add(p)
        p = mostCounterclockwise(p, nodes)
    } while (p != l)
    hull.add(p)

    return hull
}

/**
 * Concave hull
 *
 * @see convexHull
 */
fun shrinkwrap(nodes: LinkedHashSet<Node>, ways: LinkedHashSet<Way>): List<Node> {
    if (nodes.isEmpty())
        throw IllegalArgumentException("<nodes> must not be empty")

    if (ways.isEmpty())
        throw IllegalArgumentException("<ways> must not be empty")

    val hull = ArrayList<Node>()

    // Leftmost node
    val startNode = nodes.minBy(Node::lon)!! // Checked with isEmpty()

    hull.add(startNode)
    val possibleFirstNodes = connectedSelectedNodes(startNode, nodes, ways)
    if (possibleFirstNodes.isEmpty())
        throw UnconnectedStartNodeException()
    val secondNode = mostCounterclockwise(startNode, possibleFirstNodes)

    var p = secondNode
    var prev = startNode
    do {
        hull.add(p)
        val connectedSelectedNodes = connectedSelectedNodes(p, nodes, ways)
        val possibleNextNodes = connectedSelectedNodes.minus(prev).ifEmpty {
            connectedSelectedNodes
        }
        val next = possibleNextNodes.maxBy { node ->
            angle(prev.coor, p.coor, node.coor)
        }!! // Checked
        prev = p
        p = next
    } while (!(prev == startNode && p == secondNode))
    hull.add(p)

    return hull
}

fun balloon(startPos: LatLon, nodes: LinkedHashSet<Node>, ways: LinkedHashSet<Way>): List<Node> {
    if (nodes.isEmpty())
        throw IllegalArgumentException("<nodes> must not be empty")

    if (ways.isEmpty())
        throw IllegalArgumentException("<ways> must not be empty")

    val closestNodes = nodes.sortedBy { node ->
        startPos.greatCircleDistance(node.coor)
    }

    for (firstNode in closestNodes) {
        val hull = ArrayList<Node>()
        val path = Path2D.Double()
        hull.add(firstNode)
        path.moveTo(firstNode.coor.x, firstNode.coor.y)
        val possibleFirstNodes = connectedSelectedNodes(firstNode, nodes, ways)
        if (possibleFirstNodes.isEmpty()) {
            // Start node is not connected, try the next closest node
            continue
        }
        val secondNode = possibleFirstNodes.minBy { node ->
            angle(startPos, firstNode.coor, node.coor)
        }!! // Checked

        var p = secondNode
        var prev = firstNode
        var i = 0
        do {
            hull.add(p)
            path.lineTo(p.coor.x, p.coor.y)
            val connectedSelectedNodes = connectedSelectedNodes(p, nodes, ways)
            // Prevent backtracking
            val possibleNextNodes = connectedSelectedNodes.minus(prev).ifEmpty {
                // Backtrack, this happens on the tip of an inward spike
                connectedSelectedNodes
            }.ifEmpty {
                // Should never occur (how did we get here without a connecting node??)
                throw Exception("Shouldn't occur")
            }
            val next = possibleNextNodes.minBy { node ->
                angle(prev.coor, p.coor, node.coor)
            }!! // Checked
            prev = p
            p = next

            if (i++ > 5000)
                throw IterationLimitExceededException()
        } while (!(prev == firstNode && next == secondNode))
        path.closePath()

        if (path.contains(startPos.x, startPos.y))
            return hull
    }

    throw NoInnerConcaveHullException()
}