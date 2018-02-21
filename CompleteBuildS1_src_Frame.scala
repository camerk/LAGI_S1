package Snakes

import Records.RecordC

import scala.Array.ofDim
import scala.collection.mutable.Queue

/*
    Modified by Thomas on 11/20/17
 */
object Frame {
  //declaring variables
  val Moves = new RecordC() // for Map Class
  val height: Int = 20
  val width: Int = 20
  val grid = ofDim[Int](height, width)
  var snakeDecision: Int = 0
  var LGDecision: Int = 0
  var snakeHeadY: Int = 6
  var snakeHeadX: Int = 6
  var LGY: Int = 5
  var LGX: Int = 17
  val portalY: Int = 1
  val portalX: Int = 1
  var snakeYs = new Queue[Int]()
  var snakeXs = new Queue[Int]()
  var isFound: Boolean = false
  var isEaten: Boolean = false
  var isDead: Boolean = false
  var isDay: Boolean = true
  var signal: Int = -999
  var totalSteps: Int = 0
  var check = false
  var door: Int = 7

  //all variables after this are for transport function
  var lgy: Int = 2
  var lgx: Int = 1
  val h = 7
  val w = 7
  val newenv = ofDim[Int](h, w)
  val gridT = ofDim[Int](height, width) //Added for Map implementation, this mirrors the new env array at a 20 by 20 size so that it can be pushed into the queue that only takes 20 by 20 arrays
  var LGDecision2: Int = 0


  def transport(): Boolean = {

    println("WELCOME TO THE MINI GAME")

    //Declaring the variables
    var exit = false
    var isDay: Boolean = true
    var signal: Int = -999
    check = true
    lgy = 2
    lgx = 1
    //nested for loop to populate the grid with 0's
    for (i <- 1 to height - 2) {
      for (j <- 1 to width - 2) {
        gridT(i)(j) = 0
      }
    }
    for (i <- 1 to h - 2) {
      for (j <- 1 to w - 2) {
        newenv(i)(j) = 0

      }
    }

    //nested for loop to put walls up
    for (i <- 0 to h - 1) {
      for (j <- 0 to w - 1) {
        newenv(i)(0) = -1
        gridT(i)(0) = -1
        newenv(i)(w - 1) = -1
        gridT(i)(w -1) = -1
        newenv(h-1)(j) = -1
        gridT(h - 1)(j) = -1
        newenv(0)(j) = -1
        gridT(0)(j) = -1
      }
    }

    //place player and features in grid
    newenv(lgy)(lgx) = 1
    gridT(lgy)(lgx) = 1
    newenv(2)(2) = 4
    gridT(2)(2) = 4
    newenv(3)(3) = 3
    gridT(3)(3) = 3

    printGrid(h, w, newenv)

    while(!isDead && (exit!=true)) {

      LGDecision2 = LGDecisionFactory.decision()
      println("LG wants to move : "+ LGDecision2)
      if (LGDecision2 == 1 && newenv(lgy - 1)(lgx) != 3) {
        if (newenv(lgy - 1)(lgx) == -1) {
          signal = -1
        }
        else {
          signal = 1
          if (newenv(lgy-1)(lgx) == 4) {
            signal = 4
          }
          //update grid
          newenv(lgy)(lgx) = 0
          gridT(lgy)(lgx) = 0
          lgy = lgy - 1
          newenv(lgy)(lgx) = 1
          gridT(lgy)(lgx) = 1
        }
      }
      else if (LGDecision2 == 2 && newenv(lgy + 1)(lgx) != 3) {
        //wall
        if (newenv(lgy + 1)(lgx) == -1) {
          signal = -1
        }
        else {
          //open spot
          signal = 1
          if (newenv(lgy+1)(lgx) == 4) {
            signal = 4
          }
          //update grid
          newenv(lgy)(lgx) = 0
          gridT(lgy)(lgx) = 0
          lgy = lgy + 1
          newenv(lgy)(lgx) = 1
          gridT(lgy)(lgx) = 1
        }
      }
      else if (LGDecision2 == 3 && newenv(lgy)(lgx - 1) != 3) {
        //wall
        if (newenv(lgy)(lgx-1) == -1) {
          signal = -1
        }
        else {
          //open spot or food
          signal = 1
          if (newenv(lgy)(lgx-1) == 4) {
            signal = 4
          }
          //update grid
          newenv(lgy)(lgx) = 0
          gridT(lgy)(lgx) = 0
          lgx = lgx - 1
          newenv(lgy)(lgx) = 1
          gridT(lgy)(lgx) = 1
        }
      }
      else if (LGDecision2 == 4 && newenv(lgy)(lgx + 1) != 3) {
        //wall
        if (newenv(lgy)(lgx+1) == -1) {
          signal = -1
        }
        else {
          //open spot
          signal = 1
          if (newenv(lgy)(lgx+1) == 4) {
            signal = 4
          }
          //update grid
          newenv(lgy)(lgx) = 0
          gridT(lgy)(lgx) = 0
          lgx = lgx + 1
          newenv(lgy)(lgx) = 1
          gridT(lgy)(lgx) = 1
        }
      }
      else if(LGDecision2 == 0){
        //lg not move
        signal = 1
      }
      //ADDED BEGINS
      else {
        //exit cave permanently
        LGDecisionFactory.receiveSignal(8)
        exit=true
        println("Game Continued")
      }

      //ADDED ENDS
      LGDecisionFactory.receiveSignal(signal)
      totalSteps = totalSteps +1
      if (totalSteps%12 == 0){
        isDay = !isDay
      }
      println("Steps Taken: "+totalSteps)
      Moves.rec.grids.enqueue(copyArrayTransport()) // enqueues transport function iteration
      printGrid(h, w,newenv)
    }
    if (isDead){
      println("LG died of Hunger :c")
    }
    check = false

    return exit
  }


  def copyArrayTransport(): Array[Array[Int]] = {//copy function for array in transport function that corrects referencing error
    val temp =ofDim[Int](height, width)
    var i = 0
    while ( {i < 20}) {
      var j = 0
      while ( {j < 20}) {
        temp(i)(j) = gridT(i)(j)
        j += 1
      }
      i += 1
    }
    return temp
  }

  def copyArray(): Array[Array[Int]] = {// copy function that fixes referencing error
    val temp =ofDim[Int](height, width)
    var i = 0
    while ( {i < 20}) {
      var j = 0
      while ( {j < 20}) {
        temp(i)(j) = grid(i)(j)
        j += 1
      }
      i += 1
    }
    return temp
  }

  def main(args: Array[String]): RecordC = {// main function of fram now returns a RecordC object (box of info describing events of the simulation) and can only be run via Demo class
    //nested for loop to populate grid with 0's
    for (i <- 1 to height - 2) {
      for (j <- 1 to width - 2) {
        grid(i)(j) = 0
      }
    }

    //nested for loop to make wall
    for (i <- 0 to height - 1) {
      for (j <- 0 to width - 1) {
        grid(i)(0) = -1
        grid(i)(width - 1) = -1
        grid(height-1)(j) = -1
        grid(0)(j) = -1
      }
    }

    snakeYs.enqueue(6,6,6)
    snakeXs.enqueue(4,5,6)

    //initial position of snake
    grid(6)(6) = 5 //snake head
    grid(6)(5) = 5
    grid(6)(4) = 5

    grid(LGY)(LGX) = 1
    grid(portalY)(portalX) = 2
    grid(2)(4) = 4
    grid(5)(18) = 4
    grid(7)(10) = 4
    grid(3)(17) = 4
    grid(12)(12) = 4
    grid(17)(3) = 4
    grid(5)(6) = door
    grid(6)(11) = door
    grid(10)(8) = door

    //CONSTRUCTOR COMPLETE, CAN BE CONSIDERED LEVEL 1
    Moves.rec.grids.enqueue(copyArray())//copies first iteration into the queue
    printGrid(height, width,grid)


    while(!isFound && !isEaten && !isDead) {
      snakeDecision = snakeDecisionFactory.decision()
      println("Snake wants to move : "+ snakeDecision)
      if (snakeDecision == 1) {
        if (grid(snakeHeadY - 1)(snakeHeadX) == -1 || grid(snakeHeadY-1)(snakeHeadX) == 4 || grid(snakeHeadY - 1)(snakeHeadX) == 2 || grid(snakeHeadY-1)(snakeHeadX) == 5) {
          signal = -1
        }
        else {
          signal = 1
          snakeHeadY = snakeHeadY - 1
          snakeYs.enqueue(snakeHeadY)
          snakeXs.enqueue(snakeHeadX)
          if (grid(snakeHeadY)(snakeHeadX) == 1){
            isEaten = true
            signal = 2
          }
          grid(snakeHeadY)(snakeHeadX) = 5
          grid(snakeYs.dequeue())(snakeXs.dequeue()) = 0
        }
      }
      else if (snakeDecision == 2) {
        if (grid(snakeHeadY + 1)(snakeHeadX) == -1 || grid(snakeHeadY+1)(snakeHeadX) == 4 || grid(snakeHeadY + 1)(snakeHeadX) == 2 || grid(snakeHeadY + 1)(snakeHeadX) == 5) {
          signal = -1
        }
        else {
          signal = 1
          snakeHeadY = snakeHeadY + 1
          snakeYs.enqueue(snakeHeadY)
          snakeXs.enqueue(snakeHeadX)
          if (grid(snakeHeadY)(snakeHeadX) == 1){
            isEaten = true
            signal = 2
          }
          grid(snakeHeadY)(snakeHeadX) = 5
          grid(snakeYs.dequeue())(snakeXs.dequeue()) = 0
        }
      }

      else if (snakeDecision == 3) {
        if (grid(snakeHeadY)(snakeHeadX - 1) == -1 || grid(snakeHeadY)(snakeHeadX-1) == 4 || grid(snakeHeadY)(snakeHeadX - 1) == 2 ||grid(snakeHeadY)(snakeHeadX - 1) == 5) {
          signal = -1
        }
        else {
          signal = 1
          snakeHeadX = snakeHeadX - 1
          snakeYs.enqueue(snakeHeadY)
          snakeXs.enqueue(snakeHeadX)
          if (grid(snakeHeadY)(snakeHeadX) == 1){
            isEaten = true
            signal = 2
          }
          grid(snakeHeadY)(snakeHeadX) = 5
          grid(snakeYs.dequeue())(snakeXs.dequeue()) = 0
        }
      }
      else if (snakeDecision == 4) {
        if (grid(snakeHeadY)(snakeHeadX + 1) == -1 || grid(snakeHeadY)(snakeHeadX + 1) == 4 || grid(snakeHeadY)(snakeHeadX + 1) == 2 ||grid(snakeHeadY)(snakeHeadX + 1) == 5) {
          signal = -1
        }
        else {
          signal = 1
          snakeHeadX = snakeHeadX + 1
          snakeYs.enqueue(snakeHeadY)
          snakeXs.enqueue(snakeHeadX)
          if (grid(snakeHeadY)(snakeHeadX) == 1){
            isEaten = true
            signal = 2
          }
          grid(snakeHeadY)(snakeHeadX) = 5
          grid(snakeYs.dequeue())(snakeXs.dequeue()) = 0
        }
      }
      else {
        //snake doesnt move
        signal = 1
      }
      snakeDecisionFactory.receiveSignal(signal)

      if (!isEaten) {
        LGDecision = LGDecisionFactory.decision()
        println("LG wants to move : "+ LGDecision)
        if (LGDecision == 1) {
          //LG hit wall
          if (grid(LGY - 1)(LGX) == -1) {
            signal = -1
          }
          //LG hit snake
          else if (LGY - 1 == snakeHeadY && LGX == snakeHeadX) {
            isEaten = true
            signal = 3
          }
          else if (grid(LGY - 1)(LGX) == 5) {
            //cant move onto body of the snake
            signal = -2
          }
          else if (grid(LGY - 1)(LGX) == 2) {
            //portal found
            signal = 2
            isFound = true
          }
          //TRANSPORT
          else if (grid(LGY - 1)(LGX) == door) {
            //door found
            signal = 7
            transport()
          }
          //END TRANSPORT

          else {
            //open spot
            signal = 1
            if (grid(LGY-1)(LGX) == 4) {
              signal = 4
            }
            //update grid
            grid(LGY)(LGX) = 0
            LGY = LGY - 1
            grid(LGY)(LGX) = 1
          }
        }
        else if (LGDecision == 2) {
          //wall
          if (grid(LGY + 1)(LGX) == -1) {
            signal = -1
          }
          //snake head
          else if (LGY + 1 == snakeHeadY && LGX == snakeHeadX) {
            isEaten = true
            signal = 3
          }
          else if (grid(LGY + 1)(LGX) == 5) {
            //cant move at body of snake
            signal = -2
          }

          else if (grid(LGY + 1)(LGX) == 2) {
            //portal found
            signal = 2
            isFound = true
          }
          //TRANSPORT
          else if (grid(LGY + 1)(LGX) == door) {
            //door found
            signal = 7
            transport()
          }
          //END TRANSPORT
          else {
            //open spot
            signal = 1
            if (grid(LGY+1)(LGX) == 4) {
              signal = 4
            }
            //update grid
            grid(LGY)(LGX) = 0
            LGY = LGY + 1
            grid(LGY)(LGX) = 1
          }
        }

        else if (LGDecision == 3) {
          //wall
          if (grid(LGY)(LGX-1) == -1) {
            signal = -1
          }
          //snakehead
          else if (LGY == snakeHeadY && LGX-1 == snakeHeadX) {
            isEaten = true
            signal = 3
          }

          else if (grid(LGY)(LGX-1) == 5) {
            //cant move on snake body
            signal = -2
          }
          else if (grid(LGY)(LGX-1) == 2) {
            //found portal
            signal = 2
            isFound = true
          }
          //TRANSPORT
          else if (grid(LGY)(LGX-1) == door) {
            //DOOR FOUND
            signal = 7

            transport()
          }
          //END TRANSPORT
          else {

            signal = 1
            if (grid(LGY)(LGX-1) == 4) {
              signal = 4
            }
            //update grid
            grid(LGY)(LGX) = 0
            LGX = LGX - 1
            grid(LGY)(LGX) = 1
          }
        }
        else if (LGDecision == 4) {
          //wall
          if (grid(LGY)(LGX+1) == -1) {
            signal = -1
          }
          //snake head
          else if (LGY == snakeHeadY && LGX+1 == snakeHeadX) {
            isEaten = true
            signal = 3
          }

          else if (grid(LGY)(LGX+1) == 5) {
            //cant move on snake body
            signal = -2
          }
          else if (grid(LGY)(LGX+1) == 2) {
            //found portal
            isFound = true
          }
          //TRANSPORT
          else if (grid(LGY)(LGX+1) == door) {
            //door found
            signal = 7

            transport()

          }
          //END TRANSPORT
          else {
            //open spot
            signal = 1
            if (grid(LGY)(LGX+1) == 4) {
              signal = 4
            }
            //update grid
            grid(LGY)(LGX) = 0
            LGX = LGX + 1
            grid(LGY)(LGX) = 1
          }
        }

        else {
          //LG not move
          signal = 1
        }
        LGDecisionFactory.receiveSignal(signal)
      }
      //switch between day and night
      totalSteps = totalSteps +1
      if (totalSteps%12 == 0){
        isDay = !isDay
      }
      println("Steps Taken: "+totalSteps)
      //This is the spot to record any data from this iteration of the simulation. Everything is finalized at this point.
      Moves.rec.grids.enqueue(copyArray())// enqueues new iteration now that all decisions are made
      printGrid(height, width,grid)

    }
    if (isDead){
      println("LG died of Hunger :c")
    }
    else if (isEaten){
      println("LG was eaten by the snake :S")
    }
    else{
      println("LG found the portal! :D")
    }
    return Moves
  }

  def printGrid(h: Int, w: Int, arg: Array[Array[Int]]): Unit = {
    for (i <- 0 until h) {
      for (j <- 0 until w) {
        //leaves blank if nothing there
        if (arg(i)(j) == 0) {
          print("   ")
        }
        //snake body is 'S'
        else if (arg(i)(j) == 5){
          printf("%3c", 'S')
        }
        //LG is 'P'
        else if(arg(i)(j) == 1){
          printf("%3c", 'P')
        }
        //Portal is 'O'
        else if (arg(i)(j) == 2){
          printf("%3c", 'O')
        }
        //food is 'f'
        else if (arg(i)(j) == 4){
          printf("%3c", 'f')
        }
        else if(arg(i)(j) == door){
          printf("%3c", 't')
        }
        //wall is '+'
        else{
          printf("%3c", '+')
        }
      }
      println("")
    }
  }

}