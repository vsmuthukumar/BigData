/*When assigning mutable variable to val words, then reassignment
  is possible you can see this is in for loop */
/*Also set is removing duplicates and its case sensitive for removing
 duplicates */

/*Eg-1*/
import scala.collection.mutable
val text = "Hi See Spot run. Run, Spot. Run!"
val wordsArray = text.split("[ .!,]+")
val words = mutable.Set.empty[String]
for(word <- wordsArray)
  words +=  word.toLowerCase()

words

/*Eg-2*/
var words1 = Set("Hi", "See", "Spot", "run", "Run", "Spot", "Run")
words1

/*Eg-3*/
/*Below implementation are immutable set..You cannot
assign the element using equal symbol..nums +=5..It will
throw error.

But this is possible in mutable set
*/
val nums = Set(1,2,3)

nums +5 //Adds an element (returns Set(1, 2, 3, 5))
//Removes an element (returns Set(1, 2))
nums -3
//Adds multiple elements (returns   Set(1, 2, 3, 5, 6))
nums ++ List(5, 6)
//Removes multiple elements (returns Set(3))
nums -- List(1, 2)
//Takes the intersection of two sets (returns Set(1, 3))
nums & Set(1, 3, 5, 7)
//Returns the size of the set (returns 3)
nums.size

/*Eg-4*/
/*Mutable Example*/
val words4 =   scala.collection.mutable.Set.empty[String]
words4 += "the"
words4 - "the"
//Use ++ when adding multiple items
words4 ++= List("do", "re", "mi")
//Use -- when removing multiple items
words4 --= List("do", "re")
//This method is  only available for mutable class
words4.clear


def countWords(text: String) = {
  val counts = mutable.Map.empty[String, Int]
  for (rawWord <- text.split("[ ,!.]+"))
  {
  val word = rawWord.toLowerCase
  val oldCount =
    if (counts.contains(word))
      counts(word)
    else 0

    //println(  word -> (oldCount + 1))
  counts += (word -> (oldCount + 1))
}
counts
}

countWords("See Spot run! Run, Spot. Run!")

/*Map Examples*/

/*Creating Immutable Map*/
/*When creating mutable map val and var is not a problem.
* Assignment (++=) is working fine for both val and var for
* mutable map, but assignment is not working for immutable map for val
* and working for var.
* */
val mapNums =  mutable.Map("i" -> 1,"ii" ->2)
//Adding a entry
mapNums+("vi" -> 6)
//Removing a entry
mapNums-"ii"
//Adding multiple items
mapNums++=List("iii" ->3,"v" -> 5)
//Removing multiple items
mapNums--List("i","ii")


/*Eg - Tuples*/
def longestWord(words: Array[String]) = {
  var word = words(0)
  var idx = 0
  for (i <- 1 until words.length)
    if (words(i).length > word.length)
    {
      word = words(i)
      idx = i
    }
  (word, idx)
}
val longest =  longestWord("The quick brown fox".split(" "))

val (word, idx) = longest
val word1, idx1 = longest