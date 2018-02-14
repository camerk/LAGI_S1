package Records

import scala.collection.mutable.Queue


class Record extends Serializable {
  var steps : Int = 0
  //val grade = new Array[Array[Int]]()// never assigned yet
  val grids = new Queue[Array[Array[Int]]]()
}
