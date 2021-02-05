# Shrinkwrap JOSM Plugin: Usage

Also see: [examples](EXAMPLES.md)

* [Convex Hull](#convex-hull)
* [Shrinkwrap](#shrinkwrap)
* [Balloon](#balloon)

![demonstration screenshots of the three actions](images/combined.png "Demo screenshots")

## Convex Hull

The simplest of the three. Uses [Jarvis' wrapping algorithm](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm) to create a [convex hull](https://en.wikipedia.org/wiki/Convex_hull) around the selection.

**Default shortcut:**  
```Alt+Shift+H```

### Usage
1. **Select elements** to wrap. Select at least three nodes and / or a way or relation that contains nodes.
2. **Active the action** by pressing the keyboard shortcut or going to ```Menu Bar > More tools > Convex Hull```
3. **Tada!** The plugin drew a new way around your selection.


## Shrinkwrap

Akin to the convex hull. Uses a modified version of [Jarvis' wrapping algorithm](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm).

**Default shortcut:**  
```Alt+Shift+W```

### Usage
1. Idem to Convex Hull: **Select elements** to wrap. Select at least three nodes and / or a way or relation that contains nodes.
2. **Active the action** by pressing the keyboard shortcut or going to ```Menu Bar > More tools > Shrinkwrap```
3. **Tada!** The plugin drew a new way wrapping your selection, following any nooks and crannies.

## Balloon

Imagine blowing up a balloon around your cursor. The resulting way will follow the balloon's outline.

Works very similarly to shrinkwrap, only from the inside out this time.

Make sure to position your cursor close to a node of the eventual "ballooned" way, as the closest node to your cursor is used as the starting point of the balloon.

**Default shortcut:**  
```Alt+Shift+B```

### Usage
1. **Position the cursor** in an enclosed space, close to a node you want to be part of the evetual way.
2. **Active the action** by pressing the keyboard shortcut. Using the menu bar is not really possible without moving the cursor...
3. **Tada!** The plugin drew a new way wrapping your selection, following any nooks and crannies.

