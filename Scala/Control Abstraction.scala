/*Example - 1, this example is refined in example 2
object FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles
  def filesEnding(query: String) =
    for (file <- filesHere; if file.getName.endsWith(query))
      yield file

  def filesContaining(query: String) =
    for (file <- filesHere; if file.getName.contains(query))
      yield file

  def filesRegex(query: String) =
    for (file <- filesHere; if file.getName.matches(query))
      yield file
}
*/

/*Example - 2, this example is refined in example 3
object FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles

  def filesMatching(query: String,
                    matcher: (String, String) => Boolean) = {
    for (file <- filesHere; if matcher(file.getName, query))
      yield file
  }

  def filesEnding(query: String) =
    filesMatching(query, _.endsWith(_))

  def filesContaining(query: String) =
    filesMatching(query, _.contains(_))

  def filesRegex(query: String) =
    filesMatching(query, _.matches(_))

}
*/
/*Example - 3
object FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles
  private def filesMatching(matcher: String => Boolean) =
    for (file <- filesHere; if matcher(file.getName))
      yield file
  def filesEnding(query: String) =
    filesMatching(_.endsWith(query))
  def filesContaining(query: String) =
    filesMatching(_.contains(query))
  def filesRegex(query: String) =
    filesMatching(_.matches(query))
}
*/


/*
Example 4 - Passing function as parameter
def executeAndPrint(f: (Int, Int) => Int, x: Int, y: Int): Unit = {
  val result = f(x, y)
  println(result)
}

def sum(x: Int, y: Int) = x + y

def multiply(x: Int, y: Int) = x * y

executeAndPrint(sum, 3, 11)       // prints 14

executeAndPrint(multiply, 3, 9)   // prints 27
*/

/* Example 5 - Passing two function as parameter, also we you define
Tuple2, then you need to give 2 parameter in that, otherwise error will
throw
def execTwoFunctions(f1: (Int, Int) => Int,
                     f2: (Int, Int) => Int,
                     a: Int,
                     b: Int): Tuple2[Int, Int] = {
  val result1 = f1(a, b)
  val result2 = f2(a, b)
  (result1, result2)
}

def sum(x: Int, y: Int) = x + y

def multiply(x: Int, y: Int) = x * y

val results = execTwoFunctions(sum, multiply, 2, 10)
*/

/*
var assertionsEnabled = true

def myAssert(p: () => Boolean) =
  if (!p())
    throw new AssertionError

myAssert(() => 3 > 5)
*/

/*
/*Normal Function*/
object Test extends App {

    def time(): Long = {
        println("In time()")
        System.nanoTime
    }

    def exec(t: Long): Long = {
        println("Entered exec, calling t ...")
        println("t = " + t)
        println("Calling t again ...")
        t
    }

    println(exec(time()))

}
*/
/*Call by name function*/

object TestCallByName {

    def time():Long = {

        println("In time function")
        System.nanoTime()
    }
    def exec(t: =>Long): Unit =
    {
        println("Entering exec function")
        println("t value first time ="+t)
        println("t value second time="+t)
    }

    println(exec(time()))
}