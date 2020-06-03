package net.pfiers.shrinkwrap.util

import net.pfiers.shrinkwrap.angle
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
    val l = nodes.minBy(Node::lon)!! // Checked with isEmpty()

    hull.add(l)
    val possibleFirstNodes = connectedSelectedNodes(l, nodes, ways)
    if (possibleFirstNodes.isEmpty())
        throw UnconnectedStartNodeException()

    var p = mostCounterclockwise(l, possibleFirstNodes)
    var prev = l
    while (p != l) {
        hull.add(p)
        val connectedSelectedNodes = connectedSelectedNodes(p, nodes, ways)
        var possibleNextNodes = connectedSelectedNodes.minus(hull.minus(l))
        if (possibleNextNodes.isEmpty()) {
            // Backtrack (not including prev)
            possibleNextNodes = connectedSelectedNodes.minus(prev)
            if (possibleNextNodes.isEmpty()) {
                // Backtrack including prev
                possibleNextNodes = connectedSelectedNodes
            }
        }
        val next = possibleNextNodes.maxBy { node ->
            angle(prev.coor, p.coor, node.coor)
        }!! // Checked
        prev = p
        p = next
    }
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

    for (startNode in closestNodes) {
        val hull = ArrayList<Node>()
        val path = Path2D.Double()
        hull.add(startNode)
        path.moveTo(startNode.coor.x, startNode.coor.y)
        val possibleFirstNodes = connectedSelectedNodes(startNode, nodes, ways)
        var p = possibleFirstNodes.minBy { node ->
            angle(startPos, startNode.coor, node.coor)
        }!! // Checked

        var prev = startNode
        while (true) {
            hull.add(p)
            path.lineTo(p.coor.x, p.coor.y)
            val connectedSelectedNodes = connectedSelectedNodes(p, nodes, ways)
            // Prevents backtracking to the startNode on the first iteration
            val possibleNextNodes = when (prev) {
                startNode -> connectedSelectedNodes.minus(hull)
                else -> connectedSelectedNodes.minus(hull.minus(startNode))
            }.ifEmpty {
                // Backtrack (not including prev), this happens when returning on an inward spike
                connectedSelectedNodes.minus(prev)
            }.ifEmpty {
                // Backtrack including prev, this happens on the tip of an inward spike
                connectedSelectedNodes
            }.ifEmpty {
                // Should never occur (how did we get here without a connecting node??)
                throw Exception("Shouldn't occur")
            }
            val next = possibleNextNodes.minBy { node ->
                angle(prev.coor, p.coor, node.coor)
            }!! // Checked
            if (next == hull[1])
                break
            prev = p
            p = next
        }
        path.closePath()

        if (path.contains(startPos.x, startPos.y))
            return hull
    }

    throw NoInnerConcaveHullException()
}