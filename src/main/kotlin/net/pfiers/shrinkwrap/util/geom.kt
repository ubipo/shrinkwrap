package net.pfiers.shrinkwrap.util

import org.openstreetmap.josm.data.coor.LatLon
import org.openstreetmap.josm.data.osm.Node
import kotlin.math.PI
import kotlin.math.atan2

/**
 * Adapted from https://www.geeksforgeeks.org/convex-hull-set-1-jarviss-algorithm-or-wrapping/
 *
 * @param p Reference point
 * @param q Point 1
 * @param r Point 2
 *
 * @return 0 iff colinear, 1 iff q clockwise, else 2
 */
fun orientation(p: LatLon, q: LatLon, r: LatLon): Int {
    val o = (q.y - p.y) * (r.x - q.x) -
            (q.x - p.x) * (r.y - q.y)
    return when {
        o == 0.0 -> 0 // Float comparison yadayada
        o > 0    -> 1
        else     -> 2
    }
}

/**
 * @param b center
 */
fun angle(a: LatLon, b: LatLon, c: LatLon): Double {
    return ((atan2(c.y - b.y, c.x - b.x) -
            atan2(a.y - b.y, a.x - b.x)) + 2 * PI) % (2 * PI)
}

/**
 * Adapted from https://en.wikipedia.org/wiki/Gift_wrapping_algorithm
 */
fun mostCounterclockwise(ref: Node, nodes: Collection<Node>): Node {
    var q = nodes.last()
    for (n in nodes) {
        // If n is more counterclockwise than
        // current q, then update q
        if (q == ref || orientation(ref.coor, q.coor, n.coor) == 2) {
            q = n
        }
    }
    return q
}