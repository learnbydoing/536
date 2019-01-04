package common
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.io.Source
 
case class ReduceIt[K, V](key: K, value: List[V])
case object Flush
case object Done
case object MAPME
case object REDUCEME

class Helper {

  var titleUrlMap = HashMap[String, String]()

  def createInputA : HashMap[String, String] = {

	  var inputA = HashMap[String, String]()
      val filenames = List("a.txt", "b.txt", "c.txt")
       for(f <- filenames)
       {
           var source = Source.fromFile(f)
           var content = try source.mkString finally source.close()
           inputA += f -> content
       }
       return inputA
    }//End createInputA
    
  def createInputB : HashMap[String, String] = {
      var titleUrlFile = "TitleURL.txt"
      var inputB = HashMap[String, String]()
      createInfoMap()
  
      for((title, url) <- titleUrlMap)
      {   
         var content = Source.fromURL(url).mkString
         inputB += title -> content
      }
      return inputB
  }
  
  
    def createInputC : HashMap[String, String] = {
       var inputC = HashMap[String, String]()
       val htmlFileNames = List("one.html", "two.html", "three.html", "four.html", "five.html")
       for(f <- htmlFileNames)
       {
          var source = Source.fromFile(f)
          var content = try source.mkString finally source.close()
          var parts = content.split("\n")
          inputC += f -> content
       }
       return inputC
  }
  
  def createInfoMap() = {
  	titleUrlMap += "The Pickwick Papers" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg580.txt"
  	titleUrlMap += "Life And Adventures Of Martin Chuzzlewit"  -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg968.txt"
  	titleUrlMap += "Hunted Down"  ->  "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg807.txt"
  	titleUrlMap += "Great Expectations"  -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1400.txt"
  	titleUrlMap += "A Tale of Two Cities" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg98.txt"
  	titleUrlMap += "The Cricket on the Hearth" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg20795.txt"
  	titleUrlMap += "Bleak House" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1023.txt"
  	titleUrlMap += "Our Mutual Friend" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg883.txt"
  	titleUrlMap += "Dombey and Son" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg821.txt"
  	titleUrlMap += "Oliver Twist" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg730.txt"
  	titleUrlMap += "The Old Curiosity Shop" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg700.txt"
  	titleUrlMap += "David Copperfield" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg766.txt"
  	titleUrlMap += "The Life And Adventures Of Nicholas Nickleby" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg967.txt"
  	titleUrlMap += "A Child's History of England" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg699.txt"
  	titleUrlMap += "A Christmas Carol" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg19337.txt"
  	titleUrlMap += "Little Dorrit" -> "http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg963.txt"
  }
  
  
  
    val mapperA: (String, String) => List[(String, Int)] = (file: String, content: String) => {
        val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be", "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
        var a : List[(String, Int)]  =  List()
        for (word <- content.toLowerCase.split("[\\p{Punct}\\s]+")) 
        {
            if ((!STOP_WORDS_LIST.contains(word))) 
            {
              a = a :+ (word, 1)
            }
         }
         a
     }
     
   val reducerA: (String, List[(Int)]) => (String, Int) = (out_key: String, inter_list: List[(Int)]) =>
   {
  	  (out_key, inter_list.size)
   }

  
   val mapperB: (String, String) => List[(String, String)] = (title: String, content: String) =>
   {
     var outList: List[(String, String)] = List()
     var namesFound = HashSet[String]()
     val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be", "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
     
     for(word <- content.split("[\\p{Punct}\\s]+")) 
     {
        if ((!STOP_WORDS_LIST.contains(word)) && word(0).isUpper && !namesFound.contains(word)) 
        {
            var capWord = word
            namesFound += word
            outList = outList :+ (capWord, title) 
        }   
     }
     outList
   }
 
  val reducerB: (String, List[(String)]) => (String, List[String]) = (out_key: String, inter_list: List[(String)]) =>
  {
  	  (out_key, inter_list)
  }
   
  val mapperC: (String, String) => List[(String, String)] = (fileName: String, content: String) =>
  {
        var outList: List[(String, String)] = List()
        var parts = content.split("\n")
        for(part <- parts)
        {
        	var partStr = part.toString
        	if(partStr.contains("<a href="))
        	{
        		var beg_index = part.indexOfSlice("<a href=") + 9
        		var end_index = part.indexOfSlice(">") - 1
        		var hrefFile = part.slice(beg_index, end_index)
        		outList = outList :+ (hrefFile, fileName)
            }
        }
        outList
    } 
    
  val reducerC: (String, List[(String)]) => (String, Int) = (out_key: String, inter_list: List[(String)]) =>
  {
  	  (out_key, inter_list.size)
  }
  
  
   val wordCountAgg: (List[(String, Int)]) => HashMap[String, List[Int]] = (mapper_out: List[(String, Int)]) =>
   {
   	  var dict_out = HashMap[String, List[Int]]()
   	  for( (k, v) <- mapper_out)
   	  {
   	     if(dict_out.contains(k))
   	     {	
   	     	 dict_out += (k -> (dict_out(k) :+ (v)))
   	     }
   	     else 
   	     {
   	     	dict_out += (k -> List(v))
   	     }
   	  }
   	  dict_out
   }
		
   
   val capWordAgg: (List[(String, String)]) => HashMap[String, List[String]] = (mapper_out: List[(String, String)]) =>
   {
   	  var dict_out = HashMap[String, List[String]]()
   	  for( (k, v) <- mapper_out )
   	  {
   	     if(dict_out.contains(k))
   	     {	
   	     	 dict_out += (k -> (dict_out(k) :+ (v)))
   	     }
   	     else 
   	     {
   	     	dict_out += (k -> List(v))
   	     }
   	  }
   	  dict_out
   }
   
   val webReversalAgg: (List[(String, String)]) => HashMap[String, List[String]] = (mapper_out: List[(String, String)]) =>
   {
   	  var dict_out = HashMap[String, List[String]]()
   	  for( (k, v) <- mapper_out)
   	  {
   	     if(dict_out.contains(k))
   	     {	
   	     	 dict_out += (k -> (dict_out(k) :+ (v)))
   	     }
   	     else 
   	     {
   	     	dict_out += (k -> List(v))
   	     }
   	  }
   	  dict_out
   }
  
  
}