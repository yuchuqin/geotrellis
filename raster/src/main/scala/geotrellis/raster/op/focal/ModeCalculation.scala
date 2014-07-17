package geotrellis.raster.op.focal

import geotrellis.raster._

/**
 * Computes the mode of a neighborhood for a given raster
 *
 * @note            Mode does not currently support Double raster data.
 *                  If you use a Tile with a Double CellType (TypeFloat, TypeDouble)
 *                  the data values will be rounded to integers.
 */
object ModeCalculation {
  def apply(tile: Tile, n: Neighborhood): FocalCalculation[Tile] with Initialization =
    n match {
      case Square(ext) => new CellwiseModeCalc(ext)
      case _ => new CursorModeCalc(n.extent)
    }
}


class CursorModeCalc(extent: Int)
  extends CursorCalculation[Tile]
  with IntArrayTileResult
  with MedianModeCalculation
{
  initArray(extent)

  def calc(r: Tile, cursor: Cursor) = {
    cursor.removedCells.foreach { (x, y) =>
      val v = r.get(x, y)
      if(isData(v)) {
        removeValue(v)
      }
    }
    cursor.addedCells.foreach { (x, y) =>
      val v = r.get(x, y)
      if(isData(v)) addValue(v)
    }
    tile.set(cursor.col, cursor.row, mode)
  }
}


class CellwiseModeCalc(extent: Int)
  extends CellwiseCalculation[Tile]
  with IntArrayTileResult
  with MedianModeCalculation
{
  initArray(extent)

  def add(r: Tile, x: Int, y: Int) = {
    val v = r.get(x, y)
    if (isData(v)) {
      addValue(v)
    }
  }

  def remove(r: Tile, x: Int, y: Int) = {
    val v = r.get(x, y)
    if (isData(v)) {
      removeValue(v)
    }
  }

  def setValue(x: Int, y: Int) = {
    if (x == 0 && y == 3) {
      println(s"mode=${mode}  ${arr.mkString(",")}")
    }
    tile.setDouble(x, y, mode)
  }
}