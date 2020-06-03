package net.pfiers.shrinkwrap.util

import net.pfiers.shrinkwrap.exception.BadBaseDataException
import org.openstreetmap.josm.data.osm.*
import org.openstreetmap.josm.gui.Notification
import org.openstreetmap.josm.tools.I18n
import javax.swing.JOptionPane

fun connectedSelectedNodes(refNode: Node, selected: Collection<Node>, ways: Collection<Way>): Set<Node> {
    val connectedNodes = LinkedHashSet<Node>()
    for (way in ways) {
        var prevNode: Node? = null
        for ((i, node) in way.nodes.withIndex()) {
            if (node == refNode) {
                val nextNode = way.nodes.getOrNull(i + 1)
                if (prevNode != null && selected.contains(prevNode))
                    connectedNodes.add(prevNode)
                if (nextNode != null && selected.contains(nextNode))
                    connectedNodes.add(nextNode)
            }
            prevNode = node
        }
    }
    return connectedNodes
}

fun allNodesFrom(primitives: Collection<OsmPrimitive>): Set<Node> {
    val nodes = primitives.filterIsInstance<Node>()
    val wayNodes = primitives.filterIsInstance<Way>().flatMap(Way::getNodes)
    val relationNodes = primitives.filterIsInstance<Relation>().flatMap { r -> allNodesFrom(r.memberPrimitives) }
    return wayNodes.union(nodes).union(relationNodes)
}

fun warnNot(msg: String) {
    val not = Notification(I18n.tr(
            msg
    ))
    not.setIcon(JOptionPane.WARNING_MESSAGE)
    not.show()
}
