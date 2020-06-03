package net.pfiers.shrinkwrap

import net.pfiers.shrinkwrap.exception.BadBaseDataException
import net.pfiers.shrinkwrap.exception.UnconnectedStartNodeException
import net.pfiers.shrinkwrap.util.allNodesFrom
import net.pfiers.shrinkwrap.util.doesntThrow
import net.pfiers.shrinkwrap.util.shrinkwrap
import net.pfiers.shrinkwrap.util.warnNot
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

class ShrinkwrapAction : JosmAction(
    ACTION_NAME, ICON_NAME,
    I18n.tr(
        "Create a \"shrinkwrapped\" (concave-hull-like) way around the selection. " +
                "Works on all elements."
    ),
    org.openstreetmap.josm.tools.Shortcut.registerShortcut(
        "tools:shrinkwrap",
        I18n.tr("Tool: {0}", ACTION_NAME),
        java.awt.event.KeyEvent.VK_W, org.openstreetmap.josm.tools.Shortcut.ALT_SHIFT
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
        val (selectedNodes, usableWays) = try {
            getBaseData(ds)
        } catch (ex: BadBaseDataException) {
            warnNot(ex.message)
            return
        }

        // Run alg
        val shrinkwrapHull = try {
            shrinkwrap(selectedNodes, usableWays)
        } catch (ex: UnconnectedStartNodeException) {
            warnNot("\"$ACTION_NAME\" failed: the start-node (leftmost node) is not connected to any other selected node")
            return
        }
        val shrinkwrapHullWay = Way()
        shrinkwrapHullWay.nodes = shrinkwrapHull

        // Set result
        val commands = listOf(
            AddCommand(ds, shrinkwrapHullWay),
            SelectCommand(ds, listOf(shrinkwrapHullWay))
        )
        UndoRedoHandler.getInstance().add(SequenceCommand(ACTION_NAME, commands))
    }

    companion object {
        val ACTION_NAME: String = I18n.tr("Shrinkwrap")
        const val ICON_NAME = "shrinkwrap"

        private fun getBaseData(ds: DataSet?): Pair<LinkedHashSet<Node>, LinkedHashSet<Way>> {
            if (ds == null || ds.isLocked)
                throw BadBaseDataException("\"$ACTION_NAME\" requires an active, editable layer")

            val selectedNodes = LinkedHashSet(allNodesFrom(ds.selected).filter(Node::isUsable))
            if (selectedNodes.size < 3)
                throw BadBaseDataException("\"$ACTION_NAME\" requires at least three (indirectly) selected nodes")

            val usableWays = LinkedHashSet(ds.ways.filter(Way::isUsable))
            if (selectedNodes.size < 1)
                throw BadBaseDataException("\"$ACTION_NAME\" requires at least one way on the active layer")

            return Pair(selectedNodes, usableWays)
        }
    }
}
