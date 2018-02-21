package Records

import scala.collection.mutable.Queue


class Record extends Serializable {// Record is a "box" that holds the information from Frame to be used in Map
  var steps : Int = 0
  //val grade = new Array[Array[Int]]()// never assigned yet
  val grids = new Queue[Array[Array[Int]]]()
}
