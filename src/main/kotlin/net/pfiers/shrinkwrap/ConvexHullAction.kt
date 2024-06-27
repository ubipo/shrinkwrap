package net.pfiers.shrinkwrap

import net.pfiers.shrinkwrap.exception.BadBaseDataException
import net.pfiers.shrinkwrap.util.allNodesFrom
import net.pfiers.shrinkwrap.util.convexHull
import net.pfiers.shrinkwrap.util.doesntThrow
import net.pfiers.shrinkwrap.util.takeOnlyIfAtLeast
import net.pfiers.shrinkwrap.util.warnNotification
import org.openstreetmap.josm.actions.JosmAction
import org.openstreetmap.josm.command.AddCommand
import org.openstreetmap.josm.command.SelectCommand
import org.openstreetmap.josm.command.SequenceCommand
import org.openstreetmap.josm.data.UndoRedoHandler
import org.openstreetmap.josm.data.osm.DataSet
import org.openstreetmap.josm.data.osm.Node
import org.openstreetmap.josm.data.osm.OsmPrimitive
import org.openstreetmap.josm.data.osm.Way
import org.openstreetmap.josm.tools.I18n
import java.awt.event.ActionEvent

class ConvexHullAction : JosmAction(
    ACTION_NAME, ICON_NAME,
    I18n.tr(
            "Create a convex hull way around the selection. " +
                    "Works on all elements."
    ),
    org.openstreetmap.josm.tools.Shortcut.registerShortcut(
            "tools:convexhull",
            I18n.tr("Tool: {0}", ACTION_NAME),
            java.awt.event.KeyEvent.VK_H, org.openstreetmap.josm.tools.Shortcut.ALT_SHIFT
    ),
    true
) {
    override fun updateEnabledState() {
        updateEnabledStateOnCurrentSelection()
    }

    override fun updateEnabledState(selection: Collection<OsmPrimitive>?) {
        isEnabled = doesntThrow(BadBaseDataException::class) {
            getBaseData(layerManager.editDataSet)
        }
    }

    override fun actionPerformed(e: ActionEvent?) {
        // Get data
        val ds = layerManager.editDataSet
        val selectedNodes = try {
            getBaseData(ds)
        } catch (ex: BadBaseDataException) {
            warnNotification(ex.message)
            return
        }

        // Run alg
        val hull = convexHull(selectedNodes)
        val hullWay = Way()
        hullWay.nodes = hull.toList()

        // Set result
        val commands = listOf(
            AddCommand(ds, hullWay),
            SelectCommand(ds, listOf(hullWay))
        )
        UndoRedoHandler.getInstance().add(SequenceCommand(ACTION_NAME, commands))
    }

    companion object {
        val ACTION_NAME: String = I18n.tr("Convex Hull")
        const val ICON_NAME = "convexhull"

        private fun getBaseData(ds: DataSet?): Collection<Node> {
            if (ds == null || ds.isLocked)
                throw BadBaseDataException("\"$ACTION_NAME\" requires an active, editable layer")

            val usableNodeSequence = allNodesFrom(ds.selected).filter(Node::isUsable).takeOnlyIfAtLeast(3)
                ?: throw BadBaseDataException("\"$ACTION_NAME\" requires at least three (indirectly) selected nodes")

            return usableNodeSequence.toSet()
        }
    }
}
