/*
* 1. Datalake Creation.
* 2. Read data from RDBS and Store it in HDFS(CSV format),Hive and Elastic Search
* */

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import java.io.FileInputStream
import java.util.Properties
import java.sql.DriverManager
import java.sql.Connection
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.elasticsearch.spark._
import org.apache.spark.sql.Row;
import org.elasticsearch.spark.sql._
import java.util.Date
import org.apache.spark.sql.hive.orc._
import org.apache.spark.sql._
import org.apache.spark.sql.hive.HiveContext

object DataLake_Scoring {
  //case class rddtoes (customernumber: String,customername: String,city:String,creditlimit:String,MSRP:String,orderdate:String);
  /* function to connect and pull data from mysql database */
  def getRdbmsData(sqlc: SQLContext,DatabaseName: String, TableName: String, PartitionColumn:String, ConnFile: String): DataFrame= {
    val conn=new Properties()
    val probFile= new FileInputStream(s"$ConnFile")
    conn.load(probFile)
    /* Reading mysql server connection detail from property file */
    val Driver=conn.getProperty("driver")
    val Host =conn.getProperty("host")
    val Port =conn.getProperty("port")
    val User =conn.getProperty("user")
    val Pass =conn.getProperty("pass")
    val url=Host+":"+Port+"/"+s"$DatabaseName"
    sqlc.read.format("jdbc")
      .option("driver",s"$Driver")
      .option("url",s"$url")
      .option("user",s"$User")
      .option("lowerBound",1)
      .option("upperBound",10000)
      .option("numPartitions",4)
      .option("partitionCol",s"$PartitionColumn")
      .option("password",s"$Pass")
      .option("dbtable",s"$TableName").load()
  }

  /**/
  def processCustPayData(sqlc: HiveContext) = {
    //processing customer details and registering as custdetcomplextypes
    sqlc.sql("select customerNumber,customerName,concat(contactFirstName,' ',contactLastName) as contactfullname,addressLine1,city,state,postalCode,country,phone,creditLimit,checknumber,amount,paymentdate from cust_payment ").createOrReplaceTempView("custdetcomplextypes")
    //processing order details and registering as orddetcomplextypes
    sqlc.sql("select customernumber,ordernumber,shippeddate,status,comments,productcode,quantityordered,priceeach,orderlinenumber,productName,productLine,productScale,productVendor,productDescription,quantityInStock,buyPrice,MSRP,orderdate from order_products").createOrReplaceTempView("orddetcomplextypes")

    //getting customer payment detail and saving as CSV file
    val custpaymentcomplextypes=sqlc.sql("select CONCAT(customerNumber,'~',checknumber,'~',CONCAT(creditLimit,'$',amount),'~',paymentdate) from cust_payment")
    custpaymentcomplextypes.write.mode(SaveMode.Overwrite).option("header", "true").csv("hdfs://localhost:54310/user/hduser/output/CustPayment")

    /*creating retail_mart and custordfinal table in hive*/
    //sqlcontext.sql("drop database if exists retail_mart cascade")
    sqlc.sql("create database if not exists retail_mart")
    sqlc.sql("create external table IF NOT EXISTS retail_mart.custordfinal (customernumber STRING, customername STRING, contactfullname string, addressLine1 string,city string,state string,country string,phone bigint,creditlimit float,checknum string,checkamt int,ordernumber STRING,shippeddate date,status string, comments string,productcode string,quantityordered int,priceeach double,orderlinenumber int,productName STRING,productLine STRING,productScale STRING,productVendor STRING,productDescription STRING,quantityInStock int,buyPrice double,MSRP double,orderdate date) stored as orcfile location 'hdfs://localhost:54310/user/hive/warehouse/retail_mart.db/custordfinal/'")

    //Processing cust and order data and loading into orc table
    val custfinal=sqlc.sql("insert into retail_mart.custordfinal select cd.customernumber customernumber,cd.customername customername,cd.contactfullname contactfullname,cd.addressLine1 addressLine1,cd.city city,cd.state state,cd.country country,cast(cd.phone as bigint) phone,cast(cd.creditlimit as float) creditlimit,cd.checknumber checknum,cast(cd.amount as int) checkamt,o.ordernumber ordernumber,cast(o.shippeddate as date) shippeddate,o.status,o.comments,o.productcode,cast(o.quantityordered as int) quantityordered,cast(o.priceeach as double) priceeach,cast(o.orderlinenumber as int) orderlinenumber,o.productName ,o.productLine,productScale,o.productVendor,o.productDescription,cast(o.quantityInStock as int) quantityInStock,cast(o.buyPrice as double) buyPrice,cast(o.MSRP as double) MSRP,cast(o.orderdate as date) orderdate from custdetcomplextypes cd inner join orddetcomplextypes o on cd.customernumber=o.customernumber")
    val custfinales = sqlc.sql("select cd.customernumber customernumber,cd.customername customername,cd.city city,cast(o.quantityInStock as int) quantityInStock,cast(o.MSRP as double) MSRP,cast(o.orderdate as date) orderdate from custdetcomplextypes cd inner join orddetcomplextypes o on cd.customernumber=o.customernumber")

    println("Calling the writetoes function to load the custfinales dataframe into Elastic search index" )
    val writees= writetoes(custfinales)
  }
  def writetoes (custfinal:DataFrame) = {
    //Writing the processed data to elastic search index
    //val custfinaltoes=custfinal.rdd
    //println(custfinaltoes.count)
    //val toes = custfinaltoes.map(x => rddtoes(x(0).toString(),x(1).toString(), x(2).toString, x(3).toString,x(4).toString(),x(5).toString()))
    custfinal.saveToEs("custfinales2/doc1");
  }
  def main( args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("Frustration_Scoring_SQL_Hive")
      .setMaster("local") //creating spark configuration
    val sc =new SparkContext(conf) //creating SparkContext
    val sqlcontext=new HiveContext(sc) //Creating hive Context
    import sqlcontext.implicits._
    val confile="/home/hduser/Spark-Retail/connection.prob"
    //val confile=args(0) //getting mysql connection property file
    //val infiledir=args(1) //getting inputfile dir

    //calling method to pull employees table data from mysql db under empoffice schema
    //Format: methodname(<hive/sqlcontext object>,<schemaname>,<TableName>,<partitioncolumn>)
    val db1= getRdbmsData(sqlcontext,"empoffice","employees","employeeNumber",s"$confile")
    db1.createOrReplaceTempView("employee") //registering as temporary table
    println("Employee data successfully pulled from Mysql!!!" )

    //calling method to pull offices table data from mysql db under empoffice schema
    val db2= getRdbmsData(sqlcontext,"empoffice","offices","officeCode",s"$confile")
    db2.createOrReplaceTempView("offices") //registering as temporary view to write queries
    println("Offices data successfully pulled from Mysql!!!")

    //calling method to pull data from mysql db under custpayments schema using custom sql
    val custSql="(select c.customerNumber, upper(c.customerName) as customerName,c.contactFirstName, c.contactLastName,c.phone,c.addressLine1,c.city as city,c.state,c.postalCode,c.country ,c.salesRepEmployeeNumber,c.creditLimit ,p.checknumber,p.paymentdate,p.amount from customers c inner join payments p on c.customernumber=p.customernumber) as cust_pay"
    val db3= getRdbmsData(sqlcontext,"custpayments",s"$custSql","city",s"$confile")
    db3.createOrReplaceTempView("cust_payment") //registering as temporary table
    println("Customer and payment joined data successfully pulled from Mysql!!!")

    //calling method to pull data from mysql db under ordersproducts schema using custom sql
    val ordSql="(select o.customernumber,o.ordernumber,o.orderdate as orderdate,o.shippeddate,o.status,o.comments,od.quantityordered,od.priceeach, od.orderlinenumber,p.productCode,p.productName,p.productLine,p.productScale,p.productVendor,p.productDescription,p.quantityInStock,p.buyPrice,p.MSRP from orders o inner join orderdetails od on o.ordernumber=od.ordernumber inner join products p on od.productCode=p.productCode) as order_prod"
    val db4= getRdbmsData(sqlcontext,"ordersproducts",s"$ordSql","orderdate",s"$confile")
    db4.createOrReplaceTempView("order_products") //registering as temporary table
    println("Order data successfully pulled from Mysql!!!")

    //Calling method to process customer data
    processCustPayData(sqlcontext)
    println("Customer data has been processed successfully and loaded into retail_mart.custordfinal Hive table")
    println("Cutomer payment details availabe in /user/hduser/output/CustPayment file")
    sc.stop()
  }
}
