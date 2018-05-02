package Snakes

import scala.util.Random
import scala.Array.ofDim

import scala.collection.mutable.Stack
import java.io._

/**
  * Changed by Thomas on 10/20/17.
  */
object LGDecisionFactory {

  //declare variables
  var lastSignal: Int = -999  // 1: successful, -1: wall collision, 2: Portal , 3: Eaten , 4: Food
  var lastMove: Int = 0
  var stepsSinceFood: Int = 0
  var hasHunger: Boolean = false
  var shortTermMem = Stack[Int]()
  var popped: Boolean = false

  def STMPush(dec : Int)= {

    //Records the movement of LG but opposite direction gets pushed
    // (i.e LG moves left[3], stack records right[4])

    if(dec!=0){
      if(dec<3)
        shortTermMem.push((dec%2)+1)
      else
        shortTermMem.push((dec%2)+3)
    }

    //This line prints out top of the stack, shows movement from above grid to below grid
    println("New movement " + shortTermMem.top)
  }
  def decision(): Int = {
    popped = false
    var decision: Int = 0

    //determine if LG is hungry/dead
    if (stepsSinceFood>35){
      Frame.isDead=true
    }
    else if (stepsSinceFood>11){
      hasHunger=true
      println("LG: I'M HANGRY!!")
    }

    //Ability to see based on the changing conditions
    if(Frame.check && hasHunger){
      decision = tHungerSight()
    }
    else if(Frame.check){
      decision = transportSight()
    }
    else if (hasHunger) {
      decision = hungerSight()
    }
    else if (Frame.isDay){
      decision = LGDaySight()
    }
    else if(!Frame.isDay){
      decision = LGNightSight()
    }

    //if nothing was seen, do slightlyMoreIntellegentWalk
    if (decision == 0) {
      decision = Random.nextInt(4)+1

      //This gets a new value if LG just hit a wall, and tries to continue into the wall again
      while (decision == lastMove && lastSignal == -1) {
        decision = Random.nextInt(4)+1
      }

      if (lastSignal == 1) {

        //if LG went up and his last move was down, get a new random number and vice versa
        //prevents oscillation
        while ((decision == 1 && lastMove == 2) || (decision == 2 && lastMove == 1)) {
          decision = Random.nextInt(4)+1
        }

        //same for left and right
        while ((decision == 3 && lastMove == 4) || (decision == 4 && lastMove == 3)) {
          decision = Random.nextInt(4)+1
        }
      }
    }
    if(!Frame.check && !popped)
      STMPush(decision) //LG has ran through all checks and can add the step to memory

    var temp: Int = decision
    decision = 0
    //update last move
    lastMove = temp
    stepsSinceFood=stepsSinceFood+1
    temp
  }


  def receiveSignal( sig: Int ) = {
    lastSignal = sig
    sig match {
      case -1 => {
        println("LG: wall collision\n")
        shortTermMem.pop() // if LG runs into wall, pushes to stack, needs to get rid of the memory b/c didn't actually move
      }
      case 1 => {
        println("LG: successful move\n")
      }
      case 2 => {
        println("LG: PORTALATED!!\n")
      }
      case 3 => {
        println("LG: YOU GOT EATEN!!!!\n")
      }
      case 4 => {
        println("LG: You found some food. Good Job!\n")
        stepsSinceFood=0
        hasHunger = false
      }
      case _ => {
        ;
        ;
      }
    }
  }

  def LGDaySight(): Int = {
    var retval: Int = 0
    val sawPortal = new Array[Boolean](4)//all four slots are initialized as false
    val sawSnake = new Array[Boolean](4) //all four slots are initialized as false

    val sawSnakeUp = new Array[Boolean](6) // initialized as false, will be for probability if snake is above
    val sawSnakeDown = new Array[Boolean](6) // " ", for probability if snake is below
    val sawSnakeLeft = new Array[Boolean](6) // " ", for probability if snake is to the left
    val sawSnakeRight = new Array[Boolean](6) // " ", for probability if snake is to the right


    //determine what is in all four directions, then make a decision
    //checking up
    if(Frame.LGY-5 >= 0) {
      for (i <- Frame.LGY-5 until Frame.LGY) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(0) = true
          sawSnakeUp(5-i-(Frame.LGY-5)) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(0) = true
        }
      }
    }
    else{
      for (i <- 0 until Frame.LGY) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(0) = true
          sawSnakeUp(5-i) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(0) = true
        }
      }
    }
    //checking down
    if(Frame.LGY+5 < Frame.height-1) {
      for (i <- Frame.LGY until (Frame.LGY + 5)) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(1) = true
          sawSnakeDown(Frame.LGY+5-i) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(1) = true
        }
      }
    }
    else{
      for (i <- Frame.LGY until Frame.height-1) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(1) = true
          sawSnakeDown(Frame.height-1-i) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(1) = true
        }
      }
    }
    //checking left
    if(Frame.LGX-5 >= 0) {
      for (i <- Frame.LGX-5 until Frame.LGX) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(2) = true
          sawSnakeLeft(Frame.LGX-i) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(2) = true
        }
      }
    }
    else{
      for (i <- 0 until Frame.LGX) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(2) = true
          sawSnakeLeft(Frame.LGX-i) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(2) = true
        }
      }
    }
    //checking right
    if(Frame.LGX+5 <= Frame.width-1) {
      for (i <- Frame.LGX until Frame.LGX+5) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(3) = true
          sawSnakeRight(Frame.LGX+5-i) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(3) = true
        }
      }
    }
    else{
      for (i <- Frame.LGX until Frame.width-1) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(3) = true
          sawSnakeRight(Frame.width-1-i) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(3) = true
        }
      }
    }
    //now depending on last move, check what sort of information he saw
    //with danger taking priority.
    if (sawSnake.contains(true)) {
      println("Saw snake")
      if (sawSnake(0)) {
        //snake is up
        if(shortTermMem.top!=1){
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY+1)(Frame.LGX) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 2
        }
      }
      else if(sawSnake(1)){
        //snake is down
        if(shortTermMem.top!=2){
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY-1)(Frame.LGX) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 1
        }
      }
      else if(sawSnake(2)){
        //snake left
        if(shortTermMem.top!=3){
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY)(Frame.LGX+1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else
          retval = 4

      }
      else if(sawSnake(3)){
        //snake right
        if(shortTermMem.top!=4) {
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY)(Frame.LGX-1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else
          retval = 3
      }
    }
    else if (sawPortal.contains(true)) {
      if (sawPortal(0)) {
        //portal is up
        retval = 1
      }
      else if (sawPortal(1)) {
        //portal is down
        retval = 2
      }
      else if (sawPortal(2)) {
        //portal is left
        retval = 3
      }
      else {
        //portal is right
        retval = 4
      }

    }
    else if (retval == lastMove && lastSignal== -1){
      if(retval == 1 || retval == 2){
        retval = Random.nextInt(1)+3
      }
      else{
        retval =Random.nextInt(1)+1
      }
    }
    for(i<-0 until 3){
      sawPortal(i) = false
      sawSnake(i) = false
    }
    println("DaySight returned : "+ retval)
    retval
  }
  def hungerSight(): Int = {
    var retval: Int = 0
    val sawFood = new Array[Boolean](4)//all four slots are initialized as false
    val sawSnake = new Array[Boolean](4) //all four slots are initialized as false
    val distance = new Array[Int](4) // stores how far food is, initializes as 0 at first

    for(i<-0 until 3){
      distance(i) = 6           //initialize distance array to 6
    }

    //determine what is in all four directions, then make a decision
    //checking up
    if(Frame.LGY-5 >= 0) {
      for (i <- Frame.LGY-5 until Frame.LGY) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(0) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 4) {
          sawFood(0) = true
          distance(0) = Frame.LGY-i
        }
      }
    }
    else{
      for (i <- 0 until Frame.LGY) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(0) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 4) {
          sawFood(0) = true
          distance(0) = Frame.LGY-i
        }
      }
    }
    //checking down
    if(Frame.LGY+5 < Frame.height-1) {
      for (i <- Frame.LGY until (Frame.LGY + 5)) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(1) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 4) {
          sawFood(1) = true
          distance(1) = i- Frame.LGY
        }
      }
    }
    else{
      for (i <- Frame.LGY until Frame.height-1) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(1) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 4) {
          sawFood(1) = true
          distance(1) = i- Frame.LGY
        }
      }
    }
    //checking left
    if(Frame.LGX-5 >= 0) {
      for (i <- Frame.LGX-5 until Frame.LGX) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(2) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 4) {
          sawFood(2) = true
          distance(2) = Frame.LGX-i
        }
      }
    }
    else{
      for (i <- 0 until Frame.LGX) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(2) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 4) {
          sawFood(2) = true
          distance(2) = Frame.LGX-i
        }
      }
    }
    //checking right
    if(Frame.LGX+5 <= Frame.width-1) {
      for (i <- Frame.LGX until Frame.LGX+5) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(3) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 4) {
          sawFood(3) = true
          distance(3) = i- Frame.LGX
        }
      }
    }
    else{
      for (i <- Frame.LGX until Frame.width-1) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(3) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 4) {
          sawFood(3) = true
          distance(3) = i- Frame.LGX
        }
      }
    }
    //now check what kinda information he saw and make a decision
    if (sawSnake.contains(true)) {
      println("Saw snake")
      if (sawSnake(0)) {
        //snake is up
        if(shortTermMem.top!=1){
          retval = shortTermMem.top
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY+1)(Frame.LGX) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 2
        }
      }
      else if(sawSnake(1)){
        //snake is down
        if(shortTermMem.top!=2){
          retval = shortTermMem.top
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY-1)(Frame.LGX) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 1
        }
      }
      else if(sawSnake(2)){
        //snake left
        if(shortTermMem.top!=3){
          retval = shortTermMem.top
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY)(Frame.LGX+1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else
          retval = 4

      }
      else if(sawSnake(3)){
        //snake right
        if(shortTermMem.top!=4) {
          retval = shortTermMem.top
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY)(Frame.LGX-1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else
          retval = 3
      }
    }
    else if (sawFood.contains(true)) {

      var temp = 6
      for(i <- 1 until 4){
        if(distance(i-1) < temp){
          temp = distance(i-1)
          retval = i
        }
      }


    }
    else if (retval == lastMove && lastSignal== -1){
      if(retval == 1 || retval == 2){
        retval = Random.nextInt(1)+3
      }
      else{
        retval =Random.nextInt(1)+1
      }
    }
    for(i<-0 until 3){
      sawFood(i) = false
      sawSnake(i) = false
    }
    println("HungerSight returned : "+ retval)
    retval
  }
  def LGNightSight(): Int = {
    var retval: Int = 0
    val sawPortal = new Array[Boolean](4)//all four slots are initialized as false
    val sawSnake = new Array[Boolean](4) //all four slots are initialized as false
    //determine what is in all four directions, then make a decision
    //checking up
    if(Frame.LGY-4 >= 0) {
      for (i <- Frame.LGY-4 until Frame.LGY) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(0) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(0) = true
        }
      }
    }
    else{
      for (i <- 0 until Frame.LGY) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(0) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(0) = true
        }
      }
    }
    //checking down
    if(Frame.LGY+4 < Frame.height-1) {
      for (i <- Frame.LGY until (Frame.LGY + 5)) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(1) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(1) = true
        }
      }
    }
    else{
      for (i <- Frame.LGY until Frame.height-1) {
        if (Frame.grid(i)(Frame.LGX) == 5) {
          sawSnake(1) = true
        }
        else if (Frame.grid(i)(Frame.LGX) == 2) {
          sawPortal(1) = true
        }
      }
    }
    //checking left
    if(Frame.LGX-4 >= 0) {
      for (i <- Frame.LGX-4 until Frame.LGX) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(2) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(2) = true
        }
      }
    }
    else{
      for (i <- 0 until Frame.LGX) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(2) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(2) = true
        }
      }
    }
    //checking right
    if(Frame.LGX+4 <= Frame.width-1) {
      for (i <- Frame.LGX until Frame.LGX+4) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(3) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(3) = true
        }
      }
    }
    else{
      for (i <- Frame.LGX until Frame.width-1) {
        if (Frame.grid(Frame.LGY)(i) == 5) {
          sawSnake(3) = true
        }
        else if (Frame.grid(Frame.LGY)(i) == 2) {
          sawPortal(3) = true
        }
      }
    }
    //now depending on last move, check what sort of information he saw.
    //** It's a heck of a lot easier to let LG see in all four directions.
    if (sawSnake.contains(true)) {
      println("Saw snake")
      if (sawSnake(0)) {
        //snake is up
        if(shortTermMem.top!=1){
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY+1)(Frame.LGX) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 2
        }
      }
      else if(sawSnake(1)){
        //snake is down
        if(shortTermMem.top!=2){
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY-1)(Frame.LGX) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 1
        }
      }
      else if(sawSnake(2)){
        //snake left
        if(shortTermMem.top!=3){
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY)(Frame.LGX+1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else
          retval = 4

      }
      else if(sawSnake(3)){
        //snake right
        if(shortTermMem.top!=4) {
          retval = shortTermMem.top //FIX THIS PLEASE, LG CAN MOVE TO SNAKE
          println("POPPED: " + shortTermMem.top)
          popped = true
          shortTermMem.pop()
        }
        else if(Frame.grid(Frame.LGY)(Frame.LGX-1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else
          retval = 3
      }
    }
    else if (sawPortal.contains(true)) {
      if (sawPortal(0)) {
        //portal is up
        retval = 1
      }
      else if(sawPortal(1)){
        //portal is down
        retval = 2
      }
      else if(sawPortal(2)){
        //portal left
        retval = 3
      }
      else if(sawPortal(3)){
        //portal right
        retval = 4
      }
    }
    else if (retval == lastMove && lastSignal== -1){
      if(retval == 1 || retval == 2){
        retval = Random.nextInt(1)+3
      }
      else{
        retval =Random.nextInt(1)+1
      }
    }
    for(i<-0 until 3){
      sawPortal(i) = false
      sawSnake(i) = false
    }
    println("NightSight returned : "+ retval)
    retval
  }

  def transportSight(): Int = {
    var retval: Int = 0
    val sawPortal = new Array[Boolean](4)//all four slots are initialized as false
    val sawSnake = new Array[Boolean](4) //all four slots are initialized as false

    val sawSnakeUp = new Array[Boolean](6) // initialized as false, will be for probability if snake is above
    val sawSnakeDown = new Array[Boolean](6) // " ", for probability if snake is below
    val sawSnakeLeft = new Array[Boolean](6) // " ", for probability if snake is to the left
    val sawSnakeRight = new Array[Boolean](6) // " ", for probability if snake is to the right


    //determine what is in all four directions, then make a decision
    //checking up
    if(Frame.lgy-5 >= 0) {
      for (i <- Frame.lgy-5 until Frame.lgy) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(0) = true
          sawSnakeUp(5-i-(Frame.lgy-5)) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 2) {
          sawPortal(0) = true
        }
      }
    }
    else{
      for (i <- 0 until Frame.lgy) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(0) = true
          sawSnakeUp(5-i) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 2) {
          sawPortal(0) = true
        }
      }
    }
    //checking down
    if(Frame.lgy+5 < Frame.h-1) {
      for (i <- Frame.lgy until (Frame.lgy + 5)) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(1) = true
          sawSnakeDown(Frame.lgy+5-i) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 2) {
          sawPortal(1) = true
        }
      }
    }
    else{
      for (i <- Frame.lgy until Frame.h-1) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(1) = true
          sawSnakeDown(Frame.h-1-i) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 2) {
          sawPortal(1) = true
        }
      }
    }
    //checking left
    if(Frame.lgx-5 >= 0) {
      for (i <- Frame.lgx-5 until Frame.lgx) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(2) = true
          sawSnakeLeft(Frame.lgx-i) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 2) {
          sawPortal(2) = true
        }
      }
    }
    else{
      for (i <- 0 until Frame.lgx) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(2) = true
          sawSnakeLeft(Frame.lgx-i) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 2) {
          sawPortal(2) = true
        }
      }
    }
    //checking right
    if(Frame.lgx+5 <= Frame.w-1) {
      for (i <- Frame.lgx until Frame.lgx+5) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(3) = true
          sawSnakeRight(Frame.lgx+5-i) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 2) {
          sawPortal(3) = true
        }
      }
    }
    else{
      for (i <- Frame.lgx until Frame.w-1) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(3) = true
          sawSnakeRight(Frame.w-1-i) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 2) {
          sawPortal(3) = true
        }
      }
    }
    //now depending on last move, check what sort of information he saw
    //with danger taking priority.
    if (sawSnake.contains(true)) {
      println("Saw snake")
      if (sawSnake(0)) {
        //snake is up
        if(Frame.newenv(Frame.lgy+1)(Frame.lgx) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 2
        }
      }
      else if(sawSnake(1)){
        //snake is down
        if(Frame.newenv(Frame.lgy-1)(Frame.lgx) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 1
        }
      }
      else if(sawSnake(2)){
        //snake left
        if(Frame.newenv(Frame.lgy)(Frame.lgx+1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else {
          retval = 4
        }
      }
      else if(sawSnake(3)){
        //snake right
        if(Frame.newenv(Frame.lgy)(Frame.lgx-1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else {
          retval = 3
        }
      }
    }
    else if (sawPortal.contains(true)) {
      if (sawPortal(0)) {
        //portal is up
        retval = 1
      }
      else if (sawPortal(1)) {
        //portal is down
        retval = 2
      }
      else if (sawPortal(2)) {
        //portal is left
        retval = 3
      }
      else {
        //portal is right
        retval = 4
      }

    }
    else if (retval == lastMove && lastSignal== -1){
      if(retval == 1 || retval == 2){
        retval = Random.nextInt(1)+3
      }
      else{
        retval =Random.nextInt(1)+1
      }
    }
    for(i<-0 until 3){
      sawPortal(i) = false
      sawSnake(i) = false
    }
    println("TransportSight returned : "+ retval)
    retval
  }

  def tHungerSight() : Int = {
    var retval: Int = 0
    val sawFood = new Array[Boolean](4)//all four slots are initialized as false
    val sawSnake = new Array[Boolean](4) //all four slots are initialized as false
    val distance = new Array[Int](4) // stores how far food is, initializes as 0 at first

    for(i<-0 until 3){
      distance(i) = 6           //initialize distance array to 6
    }

    //determine what is in all four directions, then make a decision
    //checking up
    if(Frame.lgy-5 >= 0) {
      for (i <- Frame.lgy-5 until Frame.lgy) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(0) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 4) {
          sawFood(0) = true
          distance(0) = Frame.lgy-i
        }
      }
    }
    else{
      for (i <- 0 until Frame.lgy) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(0) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 4) {
          sawFood(0) = true
          distance(0) = Frame.lgy-i
        }
      }
    }
    //checking down
    if(Frame.lgy+5 < Frame.h-1) {
      for (i <- Frame.lgy until (Frame.lgy + 5)) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(1) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 4) {
          sawFood(1) = true
          distance(1) = i- Frame.lgy
        }
      }
    }
    else{
      for (i <- Frame.lgy until Frame.h-1) {
        if (Frame.newenv(i)(Frame.lgx) == 5) {
          sawSnake(1) = true
        }
        else if (Frame.newenv(i)(Frame.lgx) == 4) {
          sawFood(1) = true
          distance(1) = i- Frame.lgy
        }
      }
    }
    //checking left
    if(Frame.lgx-5 >= 0) {
      for (i <- Frame.lgx-5 until Frame.lgx) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(2) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 4) {
          sawFood(2) = true
          distance(2) = Frame.lgx-i
        }
      }
    }
    else{
      for (i <- 0 until Frame.lgx) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(2) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 4) {
          sawFood(2) = true
          distance(2) = Frame.lgx-i
        }
      }
    }
    //checking right
    if(Frame.lgx+5 <= Frame.w-1) {
      for (i <- Frame.lgx until Frame.lgx+5) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(3) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 4) {
          sawFood(3) = true
          distance(3) = i- Frame.lgx
        }
      }
    }
    else{
      for (i <- Frame.lgx until Frame.w-1) {
        if (Frame.newenv(Frame.lgy)(i) == 5) {
          sawSnake(3) = true
        }
        else if (Frame.newenv(Frame.lgy)(i) == 4) {
          sawFood(3) = true
          distance(3) = i- Frame.lgx
        }
      }
    }
    //now check what kinda information he saw and make a decision
    if (sawSnake.contains(true)) {
      println("Saw snake")
      if (sawSnake(0)) {
        //snake is up
        if(Frame.newenv(Frame.lgy+1)(Frame.lgx) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 2
        }
      }
      else if(sawSnake(1)){
        //snake is down
        if(Frame.newenv(Frame.lgy-1)(Frame.lgx) == -1){
          retval = Random.nextInt(1) + 3
        }
        else {
          retval = 1
        }
      }
      else if(sawSnake(2)){
        //snake left
        if(Frame.newenv(Frame.lgy)(Frame.lgx+1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else {
          retval = 4
        }
      }
      else if(sawSnake(3)){
        //snake right
        if(Frame.newenv(Frame.lgy)(Frame.lgx-1) == -1){
          retval = Random.nextInt(1) + 1
        }
        else {
          retval = 3
        }
      }
    }
    else if (sawFood.contains(true)) {

      var temp = 6
      for(i <- 1 until 4){
        if(distance(i-1) < temp){
          temp = distance(i-1)
          retval = i
        }
      }


    }
    else if (retval == lastMove && lastSignal== -1){
      if(retval == 1 || retval == 2){
        retval = Random.nextInt(1)+3
      }
      else{
        retval =Random.nextInt(1)+1
      }
    }
    for(i<-0 until 3){
      sawFood(i) = false
      sawSnake(i) = false
    }
    println("tHungerSight returned : "+ retval)
    retval

  }
}