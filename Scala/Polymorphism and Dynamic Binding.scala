/*Abstract Class*/

/*Ex-Invoking superclass constructors*/



/*
abstract class Element {
  def contents: Array[String]
  def height = contents.length
  def width =
    if (height == 0) 0 else contents(0).length
}

class ArrayElement(x123: Array[String]) extends Element {
  val contents: Array[String] = x123
}

class LineElement(s: String) extends ArrayElement(Array(s)) {
  override def width = s.length
  override def height = 1
}
*/
//End Example - Invoking superclass constructors


/*Ex - Polymorphism and dynamic binding*/

/*If there is no implementation for demo function in abstract class element
* then error will thrown because its not overrided in uniform element class*/
/*Also notice that, we can override the implementation in subclass even though
* there is a implementation in superclass*/
/*UniformElement does not override demo, it inherits the implementation
of demo from its superclass, Element.*/

abstract class Element {
  def demo() {
    println("Element's implementation invoked")
  }
}
class ArrayElement extends Element {
  override def demo() {
    println("ArrayElement's implementation invoked")
  }
}
class LineElement extends ArrayElement {
  override def demo() {
    println("LineElement's implementation invoked")
  }
}
// UniformElement inherits Element’s demo
class UniformElement extends Element
{
  def invokeDemo(e: Element) {
    e.demo()
  }
}

val e = new UniformElement();
e.invokeDemo(new ArrayElement);
e.invokeDemo(new LineElement);
e.invokeDemo(new UniformElement);


/*Ex - Declaring final members*/
/*Will throw error, because of final*/
/*abstract class Element {
  def demo() {
    println("Element's implementation invoked")
  }
}
class ArrayElement extends Element {
  final override def demo() {
    println("ArrayElement's implementation invoked")
  }
}
class LineElement extends ArrayElement {
  override def demo() {
    println("LineElement's implementation invoked")
  }
}
// UniformElement inherits Element’s demo
class UniformElement extends Element
{
  def invokeDemo(e: Element) {
    e.demo()
  }
}*/

/*End Ex - Declaring final members*/

/*
/*Ex - Declaring final members*/
/*Will throw error, because of final*/
abstract class Element {
  def demo() {
    println("Element's implementation invoked")
  }
}
final class ArrayElement extends Element {
  override def demo() {
    println("ArrayElement's implementation invoked")
  }
}
class LineElement extends ArrayElement {
  override def demo() {
    println("LineElement's implementation invoked")
  }
}
// UniformElement inherits Element’s demo
class UniformElement extends Element
{
  def invokeDemo(e: Element) {
    e.demo()
  }
}

/*End Ex - Declaring final members*/

 */