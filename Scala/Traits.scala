

/*Example trait working*/
/*trait Philosophical {
  def philosophize() {
    println("I consume memory, therefore I am!")
  }
}
class Frog extends Philosophical {
  override def toString = "green"
}

val frog = new Frog
frog.philosophize()

val phil:Philosophical = new Frog
phil.philosophize()*/
/*End - Example trait working*/


/*Another example overriding the method philosophize in frog class*/
/*
trait Philosophical {
  def philosophize() {
    println("I consume memory, therefore I am!")
  }
}
class Animal
class Frog extends Animal with Philosophical {
  override def toString = "green"
  override def philosophize() {
    println("It ain't easy being "+ toString +"!")
  }
}

val phrog: Philosophical = new Frog
phrog.philosophize()*/


/*Example - Rectangular objects*/
/*Note that method topleft and bottomright are dont have implementation in trait
* but its implemented in rectangle class using val. Also note that its not necessary
* to implemented as method in rectangle class*/
/*If there is no implementation in class rectangle, then it will not compile*/
/*
class Point(val x: Int, val y: Int)


trait Rectangular {
  def topLeft: Point
  def bottomRight: Point
  //def typei:Int
  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
  // and many more geometric methods...
}

class Rectangle(val topLeft: Point, val bottomRight: Point)
  extends Rectangular {
  // other methods...
}


val rect = new Rectangle(new Point(1, 1),
  new Point(10, 10))

rect.left
rect.right
*/

/*End Example - Rectangular objects*/


/*Example - The Ordered trait*/
/*
class Rational(n: Int, d: Int) extends Ordered[Rational] {
  require(d != 0)
  val numer1: Int = n
  val denom1: Int = d
  override def toString = numer1 +"/"+ denom1

  def compare(that: Rational) =
    (this.numer1 * that.denom1)- (that.numer1 * this.denom1)
}

val half = new Rational(1, 2)

val third = new Rational(1, 3)

half < third

half > third*/

/*End Example - The Ordered trait*/

/* Example Traits as stackable modifications */
/*
abstract class IntQueue
{
  def get():Int
  def put(x:Int)
}

import scala.collection.mutable.ArrayBuffer
class BasicIntQueue extends IntQueue
{
  private val buf = new ArrayBuffer[Int]
  def get() = buf.remove(0)
  def put (x:Int) {buf += x}
}

val queue = new BasicIntQueue

queue.put(10)
queue.put(20)

queue.get()
queue.get()
*/

/* End Example Traits as stackable modifications */


abstract class IntQueue
{
  def get():Int
  def put(x:Int)
}

import scala.collection.mutable.ArrayBuffer
class BasicIntQueue extends IntQueue
{
  private val buf = new ArrayBuffer[Int]
  def get() = buf.remove(0)
  def put (x:Int) {buf += x}
}


trait Doubling extends IntQueue
{
  abstract override def put(x: Int): Unit = {super.put(2*x)}
}

class MyQueue extends BasicIntQueue with Doubling

val queue = new MyQueue

queue.put(10)
queue.put(20)

queue.get()
queue.get()