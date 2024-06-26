package net.pfiers.shrinkwrap

import net.pfiers.shrinkwrap.exception.BadBaseDataException
import net.pfiers.shrinkwrap.exception.BalloonDurationLimitExceededException
import net.pfiers.shrinkwrap.exception.IterationLimitExceededException
import net.pfiers.shrinkwrap.exception.NoInnerConcaveHullException
import net.pfiers.shrinkwrap.util.balloon
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
import org.openstreetmap.josm.gui.MainApplication
import org.openstreetmap.josm.tools.I18n.tr
import java.awt.event.ActionEvent

class BalloonAction : JosmAction(
        ACTION_NAME, ICON_NAME,
        tr(
                "Blow up a \"balloon\" around the cursor and trace the polygon it fills (inverse shrinkwrap). " +
                        "No selection needed. The balloon won''t pop on sharp points ;)."
        ),
        org.openstreetmap.josm.tools.Shortcut.registerShortcut(
                "tools:balloon",
                tr("Tool: {0}", ACTION_NAME),
                java.awt.event.KeyEvent.VK_B, org.openstreetmap.josm.tools.Shortcut.ALT_SHIFT
        ),
        true
) {
    override fun updateEnabledState() {
        updateEnabledStateOnCurrentSelection()
    }

    override fun updateEnabledState(selection: Collection<OsmPrimitive>?) {
        isEnabled = hasUsableData(layerManager.editDataSet)
    }

    override fun actionPerformed(e: ActionEvent?) {
        // Get data
        val ds = layerManager.editDataSet
        val (usableNodes, usableWays) = try {
            getBaseData(ds)
        } catch (ex: BadBaseDataException) {
            warnNotification(ex.message)
            return
        }
        val mapView = MainApplication.getMap()?.mapView ?: run {
            warnNotification("\"$ACTION_NAME\" failed: map is not available yet. Create a new layer and try again.")
            return
        }
        val mousePos = mapView.mousePosition ?: run {
            warnNotification("\"$ACTION_NAME\" failed: mouse position is not available. Try again.")
            return
        }
        val mouseLatLon = mapView.getLatLon(mousePos.x, mousePos.y)

        // Run alg
        val balloonHull = try {
            balloon(mouseLatLon, usableNodes, usableWays)
        } catch (ex: NoInnerConcaveHullException) {
            warnNotification("\"$ACTION_NAME\" failed: balloon popped because it got too big (no inner concave hull found)")
            return
        } catch (ex: IterationLimitExceededException) {
            warnNotification("\"$ACTION_NAME\" failed: iteration limit exceeded, please file an issue if this was unexpected")
            return
        } catch (ex: BalloonDurationLimitExceededException) {
            warnNotification("\"$ACTION_NAME\" failed: it took too long to blow up the balloon, please file an issue if this was unexpected")
            return
        }
        val balloonHullWay = Way()
        balloonHullWay.nodes = balloonHull.toList()

        // Set result
        val commands = listOf(
                AddCommand(ds, balloonHullWay),
                SelectCommand(ds, listOf(balloonHullWay))
        )
        UndoRedoHandler.getInstance().add(SequenceCommand(ACTION_NAME, commands))
    }

    companion object {
        val ACTION_NAME: String = tr("Balloon")
        const val ICON_NAME = "balloon"

        private fun hasUsableData(ds: DataSet?): Boolean {
            if (ds == null || ds.isLocked) return false
            return ds.nodes.stream().filter(Node::isUsable).limit(3).count() >= 3 &&
                    ds.ways.stream().filter(Way::isUsable).findAny().isPresent
        }

        private fun getBaseData(ds: DataSet?): Pair<LinkedHashSet<Node>, LinkedHashSet<Way>> {
            if (ds == null || ds.isLocked) {
                throw BadBaseDataException("${ShrinkwrapAction.ACTION_NAME} requires an active, editable layer")
            }

            val usableNodes = LinkedHashSet(ds.nodes.filter(Node::isUsable))
            if (usableNodes.size < 3) {
                throw BadBaseDataException("\"$ACTION_NAME\" requires at least three nodes on the active layer")
            }

            val usableWays = LinkedHashSet(ds.ways.filter(Way::isUsable))
            if (usableWays.isEmpty()) {
                throw BadBaseDataException("\"$ACTION_NAME\" requires at least one way on the active layer")
            }

            return Pair(usableNodes, usableWays)
        }
    }
}
