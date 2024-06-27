package net.pfiers.shrinkwrap.util

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

fun allNodesFrom(
    primitives: Collection<OsmPrimitive>,
): Sequence<Node> = primitives.asSequence().flatMap { primitive ->
    when (primitive) {
        is Node -> sequenceOf(primitive)
        is Way -> primitive.nodes.asSequence()
        is Relation -> allNodesFrom(primitive.memberPrimitives)
        else -> { sequenceOf() }
    }
}.distinct()

fun warnNotification(msg: String) {
    val notification = Notification(I18n.tr(
            msg
    ))
    notification.setIcon(JOptionPane.WARNING_MESSAGE)
    notification.show()
}
