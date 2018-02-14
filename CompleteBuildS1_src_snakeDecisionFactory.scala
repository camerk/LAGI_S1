package Snakes

import scala.util.Random

/**
  * Created by Bizu on 5/3/17.
  */
object snakeDecisionFactory {

  var lastSignal: Int = -999
  var lastMove: Int = 4

  def opposite(move: Int): Int={
    var retval = 0
    if (move%2 ==0 ){//move is 2 or 4 or 0
      retval = move -1
    }
    else{
      retval = move +1
    }
    retval
  }
  //If you couldnt move and you get the same move or the opposite direction
  //of the same move pick another random direction
  def testDecision(): Int={
    var retval: Int = lastMove
    if(lastSignal== -1){
      retval = Random.nextInt(4)+1
      while(retval ==lastMove || retval == opposite(lastMove)){
        retval = Random.nextInt(4)+1
      }
    }
    lastMove=retval
    retval
  }
  def decision(): Int={
    var retval :Int = 0
    if (!Frame.isDay){
      retval = snakeSight()
    }
    if (retval == 0){ //get direction based on 3 directions he can go
      retval= Random.nextInt(4)+1
      while (retval == opposite(lastMove)){//snake is facing up, pick left, up, or right
        retval = Random.nextInt(4)+1
      }
    }
    lastMove = retval
    retval
  }


  def snakeSight(): Int = {
    var retval: Int = 0
    if (lastMove == 1) {
      //the snake is looking up
      for (i <- 1 until Frame.snakeHeadY) {
        if (Frame.grid(i)(Frame.snakeHeadX) == 1) {
          retval = 1 //keep moving up
        }
      }
    }
    else if (lastMove == 2) {
      //the snake is looking down
      for (i <- Frame.snakeHeadY until Frame.height) {
        if (Frame.grid(i)(Frame.snakeHeadX) == 1) {
          retval = 2 //keep moving down
        }
      }
    }
    else if (lastMove == 3) {
      //the snake is looking left
      for (i <- 1 until Frame.snakeHeadX) {
        if (Frame.grid(Frame.snakeHeadY)(i) == 1) {
          retval = 3 //keep moving left
        }
      }
    }
    else if (lastMove == 4) {
      //the snake is looking right
      for (i <- Frame.snakeHeadX until Frame.width) {
        if (Frame.grid(Frame.snakeHeadY)(i) == 1) {
          retval = 4
        }
      }
    }
    println("Snake Sight returns : "+ retval)
    retval
  }
  def receiveSignal( sig: Int ) = {
    lastSignal = sig
    sig match {
      case -1 => {
        println("Snake: wall collision\n")
      }
      case 1 => {
        println("Snake: successful move\n")
      }
      case 2 => {
        println("Snake: You ate the Little Guy!!\n");
      }
      case _ => {
        ;
        ;
      }
    }
  }

}