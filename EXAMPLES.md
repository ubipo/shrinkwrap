# Shrinkwrap JOSM Plugin: Examples

Also see: [usage](USAGE.md)  

* [landuse=residential around buildings](#landuseresidential-around-buildings)
* [Copying a way](#copying-a-way)
* [leisure=park enclosed by walls](#leisurepark-enclosed-by-walls)

Any other examples are welcome!

## landuse=residential around buildings

If you use the [landuse=residential-on-buildings scheme](https://wiki.openstreetmap.org/wiki/Tag:landuse%3Dresidential#Separation_from_roads), you can use [*Convex Hull*](USAGE.md#Convex%20Hull) to quickly draw the outer way:

![screenshot demonstrating usage of Convex Hull](images/landuse-convex-1.png "Landuse Convex Hull 1")

Select the buildings and activate *Convex Hull*.
![screenshot demonstrating usage of Convex Hull](images/landuse-convex-2.png "Landuse Convex Hull 2")

Add landuse tag.
![screenshot demonstrating usage of Convex Hull](images/landuse-convex-3.png "Landuse Convex Hull 3")

Done
![screenshot demonstrating usage of Convex Hull](images/landuse-convex-4.png "Landuse Convex Hull 4")

Alternatively you can use [*Shrinkwrap*](USAGE.md#Shrinkwrap) to fill in the holes on the NE-side.

![screenshot demonstrating usage of Shrinkwrap](images/landuse-shrinkwrap.png "Landuse Shrinkwrap")

## Copying a way

Say you want create a way that's just a little different from an existing one. Just select the way and use [*Shrinkwrap*](USAGE.md#Shrinkwrap) to make a copy.

As an example I want to create way for the leisure=park within this tourism=museum, but that way needs to wrap around the main building.

![screenshot demonstrating usage of Shrinkwrap](images/copy-way-1.png "Copy way Shrinkwrap 1")

Select the way I want to copy and activate *Shrinkwrap*.
![screenshot demonstrating usage of Shrinkwrap](images/copy-way-2.png "Copy way Shrinkwrap 2")

![screenshot demonstrating usage of Shrinkwrap](images/copy-way-3.png "Copy way Shrinkwrap 3")

Modify the new way to wrap around the building. 
![screenshot demonstrating usage of Shrinkwrap](images/copy-way-4.png "Copy way Shrinkwrap 4")

You could of course also solve this with multipolygons.

## leisure=park enclosed by walls

I already mapped all the buildings and walls enclosing this park.

To add the inner leisure=park way without having to press [Ctrl+f](https://josm.openstreetmap.de/wiki/Help/Action/FollowLine) a million times I can use *Balloon*.

*Notice the cursor within the empty area*
![screenshot demonstrating usage of Balloon](images/leisure-balloon-1.png "Leisure Balloon 1")

Way created
![screenshot demonstrating usage of Balloon](images/leisure-balloon-2.png "Leisure Balloon 2")

Add leisure tag
![screenshot demonstrating usage of Balloon](images/leisure-balloon-3.png "Leisure Balloon 3")
