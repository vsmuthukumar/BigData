/*
* 1. READ THE TWITTER Content using Nifi.
* 2. From Nifi Push it into Kafka
* 3. Read content from Kafka using Spark Streaming
* 4. Chop the data in SS and Load it in  Elastic Search
* */

import org.apache.spark.SparkConf
import  org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import StreamingContext._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import org.apache.spark.storage.StorageLevel
import scala.collection.mutable.ListBuffer

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{LongWritable, Writable, IntWritable, Text}
import org.apache.hadoop.mapred.{TextOutputFormat, JobConf}
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat

import org.elasticsearch.spark._ 
import org.elasticsearch.spark.sql._; 
// twitter -> nifi -> kafka -> ss -> es -> kibana
object nifikafkatweetes {
  
  def main(args:Array[String])
  {
        /*Initialization stuffs and spark context creation*/
       val sparkConf = new SparkConf().setAppName("kafkatweets").setMaster("local[*]")
       sparkConf.set("es.nodes", "localhost")
       sparkConf.set("es.port", "9200")
       sparkConf.set("es.index.auto.create", "true");
       sparkConf.set("es.mapping.id","id");
        
       val sparkcontext = new SparkContext(sparkConf)
        sparkcontext.setLogLevel("ERROR")
        val ssc = new StreamingContext(sparkcontext, Seconds(10))
        
  
        ssc.checkpoint("checkpointdir")
        val sqlcontext = new SQLContext(sparkcontext)
   
/*       val swdf=sqlcontext.read.csv("file:///home/hduser/mrdata/stopwords").toDF("stopwords")
       swdf.createOrReplaceTempView("swview")*/
       
       /*READ DATA FROM KAFKA*/
       val kafkaParams = Map[String, Object](
          "bootstrap.servers" -> "localhost:9092",
          "key.deserializer" -> classOf[StringDeserializer],
          "value.deserializer" -> classOf[StringDeserializer],
          "group.id" -> "nifikafkatopic",
          "auto.offset.reset" -> "earliest"
          )

        val topics = Array("tkk1")
        val stream = KafkaUtils.createDirectStream[String, String](
          ssc,
          PreferConsistent,
          Subscribe[String, String](topics, kafkaParams)
        )



        /*Read Specific content from tweets*/
        val kafkastream = stream.map(record => (record.key, record.value))
        val inputStream = kafkastream.map(rec => rec._2);
//        inputStream.print()
        inputStream.foreachRDD(rdd =>
          {
            if(!rdd.isEmpty())
            {
               val df = sqlcontext.read.json(rdd)
                   df.printSchema()
                   //sdf.show(false)
                   df.select("id","coordinates","text","user.followers_count","entities.urls").show(false)
                   df.createOrReplaceTempView("tweets")
sqlcontext.sql("""SELECT place,max(favorite_count) as retweets
FROM tweets 
GROUP BY  place
""").show

              /*Save it to Elastic Search*/
val dfes=df.select("id","text","user.followers_count","entities.urls");
               
       dfes.saveToEs("twitter/tweets")
       println("Data written to Elasticsearch successfully")
            }
          })
          ssc.start()
        ssc.awaitTermination()    

  }

}