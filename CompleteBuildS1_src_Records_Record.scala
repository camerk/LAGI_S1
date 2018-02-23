package Records

import scala.collection.mutable.Queue


class Record extends Serializable {// Record is a "box" that holds the information from Frame to be used in Map
  var steps : Int = 0
  //val grade = new Array[Array[Int]]()// never assigned yet

  /*
   *@HP THIS IS WHERE YOU WILL ADD VARIABLES FROM THE FRAME CLASS
   *You will have to make queues for each variable you put in here
   */
  val grids = new Queue[Array[Array[Int]]]()
}
