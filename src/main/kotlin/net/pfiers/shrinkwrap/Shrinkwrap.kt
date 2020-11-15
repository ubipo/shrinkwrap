package net.pfiers.shrinkwrap

import org.openstreetmap.josm.gui.MainApplication
import org.openstreetmap.josm.gui.MainMenu
import org.openstreetmap.josm.plugins.Plugin
import org.openstreetmap.josm.plugins.PluginInformation

/**
 * Create a "shrinkwrapped" or convex hull way around the selection.
 * https://github.com/ubipo/center-node
 *
 * @author Pieter Fiers (Ubipo)
 */
@Suppress("unused")
class Shrinkwrap(info: PluginInformation?) : Plugin(info) {
    init {
        val moreToolsMenu = MainApplication.getMenu().moreToolsMenu
        MainMenu.add(moreToolsMenu, ConvexHullAction())
        MainMenu.add(moreToolsMenu, ShrinkwrapAction())
        MainMenu.add(moreToolsMenu, BalloonAction())
    }
}
