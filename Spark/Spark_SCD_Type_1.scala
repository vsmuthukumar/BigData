/*SCD Type-1 Implementation in Spark */
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrameReader
import org.apache.spark.sql.types._

object sparkscd1 {
	def main(args: Array[String]) {
		val scdf1g = 1;
		if (scdf1g == 1) {
			val spark = SparkSession.builder().appName("Sample sql app").master("local[*]").getOrCreate()
					val sc = spark.sparkContext
					val sqlc = spark.sqlContext
					import sqlc.implicits._
					sc.setLogLevel("ERROR")
					val schema = new StructType()
					.add("id", IntegerType, true)
					.add("exchang", StringType, true)
					.add("company", StringType, true)
					.add("timestamp", DateType, true)
					.add("value",DoubleType,true)

					val dfsrc1 = spark.read.option("inferschema", "true")
					.option("delimiter", ",")
					.option("header", "true")
					.schema(schema)
					.csv("file:///home/hduser/scd/scd1/histdata.csv")

					val dfnew1 = spark.read.option("inferschema", "true")
					.option("delimiter", ",")
					.option("header", "true")
					.schema(schema)
					.csv("file:///home/hduser/scd/scd1/newdata1.csv")

					dfsrc1.createOrReplaceTempView("initialdata")
					dfnew1.createOrReplaceTempView("incremental")
					try {
						//Incremental Load
						if (!dfsrc1.rdd.isEmpty()) {
							println("Historical data is available for scd1")
							val historydf = sqlc.sql("select id,exchang,company,timestamp,value from initialdata")
								historydf.show
							println("printing new data for reference")
							val newdftest = sqlc.sql("select id,exchang,company,timestamp,value from incremental")
						 newdftest.show
							if (!dfnew1.rdd.isEmpty()) {
								println("History excluded data for scd1")
								//first day Load
								val historydffiltered = sqlc.sql("""select i.id,i.exchang,i.company,i.timestamp,i.value from initialdata i
										left outer join incremental o on i.id = o.id where o.id is null""")
								historydffiltered.show
								println("History+New data")
								val newdf = historydffiltered.union(dfnew1).toDF()
								newdf.cache
								newdf.count
								newdf.show
								newdf.coalesce(1).write.mode("overwrite").csv("file:///home/hduser/scd/scd1/histdata.csv")
								
							}
						} else {
							println("No Historical data to process for scd1 , loading as history")
							dfnew1.coalesce(1).write.mode("overwrite").csv("file:///home/hduser/scd/scd1/histdata.csv")
						}
					} catch {
					case eaa: org.apache.spark.sql.AnalysisException => {
						println(s"Exception Occured $eaa")
					}
					case unknown: Exception => println(s"Unknown exception: $unknown")
					}
		}
	}
}