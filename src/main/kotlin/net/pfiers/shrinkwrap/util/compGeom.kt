package net.pfiers.shrinkwrap.util

import net.pfiers.shrinkwrap.exception.BalloonDurationLimitExceededException
import net.pfiers.shrinkwrap.exception.IterationLimitExceededException
import net.pfiers.shrinkwrap.exception.NoInnerConcaveHullException
import net.pfiers.shrinkwrap.exception.UnconnectedStartNodeException
import org.openstreetmap.josm.data.coor.ILatLon
import org.openstreetmap.josm.data.coor.LatLon
import org.openstreetmap.josm.data.osm.Node
import org.openstreetmap.josm.data.osm.Way
import java.awt.geom.Path2D
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

const val BALLOON_MAX_ITERATIONS = 4000
val BALLOON_MAX_TIME = 3.seconds

/**
 * Jarvis march alg, adapted from https://en.wikipedia.org/wiki/Gift_wrapping_algorithm
 */
fun convexHull(nodes: Collection<Node>): Collection<Node> {
    if (nodes.isEmpty())
        throw IllegalArgumentException("<nodes> must not be empty")

    val hull = ArrayList<Node>()

    // Leftmost node
    val l = nodes.minByOrNull(Node::lon)!! // Checked with isEmpty()

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
fun shrinkwrap(nodes: Collection<Node>, ways: Collection<Way>): Collection<Node> {
    if (nodes.isEmpty())
        throw IllegalArgumentException("<nodes> must not be empty")

    if (ways.isEmpty())
        throw IllegalArgumentException("<ways> must not be empty")

    val hull = ArrayList<Node>()

    // Leftmost node
    val startNode = nodes.minByOrNull(Node::lon)!! // Checked with isEmpty()

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
        val next = possibleNextNodes.maxByOrNull { node ->
            angle(prev.coor, p.coor, node.coor)
        }!! // Checked
        prev = p
        p = next
    } while (!(prev == startNode && p == secondNode))

    return hull
}

fun balloon(startPos: LatLon, nodes: Collection<Node>, ways: Collection<Way>): Collection<Node> {
    if (nodes.isEmpty())
        throw IllegalArgumentException("<nodes> must not be empty")

    if (ways.isEmpty())
        throw IllegalArgumentException("<ways> must not be empty")

    val closestNodes = nodes.sortedBy { node ->
        (startPos as ILatLon).greatCircleDistance(node.coor)
    }

    val balloonStartTime = TimeSource.Monotonic.markNow()
    for (firstNode in closestNodes) {
        val hull = ArrayList<Node>()
        val path = Path2D.Double()
        hull.add(firstNode)
        path.moveTo(firstNode.coor.x, firstNode.coor.y)
        val possibleFirstNodes = connectedSelectedNodes(firstNode, nodes, ways)
        val secondNode = possibleFirstNodes.minByOrNull { node ->
            angle(startPos, firstNode.coor, node.coor)
        } ?: continue // Start node is not connected, try the next closest node

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
            }
            val next = possibleNextNodes.minByOrNull { node ->
                angle(prev.coor, p.coor, node.coor)
            } ?: throw Exception("Shouldn't occur") // Should never occur (how did we get here
            // without a connecting node??) TODO: Why is this state representable?
            prev = p
            p = next

            if (i++ > BALLOON_MAX_ITERATIONS)
                throw IterationLimitExceededException()

            if (balloonStartTime.elapsedNow() > BALLOON_MAX_TIME)
                throw BalloonDurationLimitExceededException()
        } while (!(prev == firstNode && next == secondNode))
        path.closePath()

        if (path.contains(startPos.x, startPos.y))
            return hull
    }

    throw NoInnerConcaveHullException()
}
