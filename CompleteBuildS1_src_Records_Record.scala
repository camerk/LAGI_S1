package Records

import scala.collection.mutable.Queue

/*
Last Edited by Cameron Kane 5/2/18
*/


class Record extends Serializable {// Record is a "box" that holds the information from Frame to be used in Map
  var steps : Int = 0
  //val grade = new Array[Array[Int]]()// never assigned yet

  /*
   *@HP THIS IS WHERE YOU WILL ADD VARIABLES FROM THE FRAME CLASS
   *You will have to make queues for each variable you put in here
   */
  val grids = new Queue[Array[Array[Int]]]()
  val isfounds1  = new Queue[Boolean]()
  val isEaten1 = new Queue[Boolean]()
  val isDead1 =new Queue[Boolean]()
  val isDay1 =new Queue[Boolean]()
  val signal1 = new  Queue[Int] ()
  var totalSteps1: Int = 0
  val door1 = new Queue[Int]()
}
