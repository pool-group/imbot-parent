package com.yunli.imbot.core.service.initialize;

import com.assembly.common.util.DataUtil;
import com.assembly.common.util.LogUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunli.imbot.core.service.config.SqlConfig;
import com.yunli.imbot.core.service.process.MachineLearningService;
import com.yunli.imbot.core.service.regiter.MyRegistrator;
import lombok.val;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * com.yunli.imbot.core.service.initialize
 *
 * @author duanpeng
 * @version Id: SparkContextFactory.java, v 0.1 2020年08月27日 13:57 duanpeng Exp $
 */
@Component
public class SparkBaseContext extends SqlConfig implements Serializable {

    private SparkSession sparkSession;
    private JavaSparkContext sc;
    private static JavaRDD<String> sourceContainer;
    private SQLContext sqlContext;

    @Autowired
    private MachineLearningService machineLearningService;

    private static final String inPath="D:/gitpace/imbot-parent/imbot-app/src/main/resources/file/knowledge_base.txt";

    @PostConstruct
    public void refreshContext() {
        val sparkConf = new SparkConf()
                .setAppName("imbot app")
                .setMaster("local[6]")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.kryo.registrator", MyRegistrator.class.getName())
                .set("spark.kryoserializer.buffer", "512k")
                .set("spark.kryoserializer.buffer.max", "512m")
                .set("spark.rpc.message.maxSize", "512")
                .set("spark.default.parallelism", "12")
                .set("spark.driver.maxResultSize", "3g")
                /*.set("spark.sql.shuffle.partitions", "6")*/;
        sc = new JavaSparkContext(sparkConf);
        sparkSession = SparkSession
                .builder().sparkContext(sc.sc())/*.config("spark.sql.shuffle.partitions", 6)*/
                .getOrCreate();
        sqlContext = new SQLContext(sc.sc());

//        machineLearningService.train();
//
//        machineLearningService.predict("你好");

        readSql();
    }

    public JavaRDD<String> leadFile(String path){
        if(null==sparkSession)
            refreshContext();
        JavaRDD<String> javaRDD = sparkSession.read().textFile(path).javaRDD().cache();
        return javaRDD;
    }

    public JavaRDD<String> readSql(){
        if(null==sqlContext)
            refreshContext();
        Properties connectionProperties = new Properties();
        connectionProperties.put("user",getUsername());
        connectionProperties.put("password",getPassword());
        connectionProperties.put("driver",getDriverClassName());

        DataFrameReader dataFrameReader=sqlContext.read().format("jdbc")
                .option("url",getUrl())
                .option("dbtable","(select id,ask_value from knowledge_base) T")
                .option("user",getUsername())
                .option("password",getPassword())
                .option("driver",getDriverClassName())
                .option("lowerBound",1)
                .option("upperBound",3000000)
                .option("partitionColumn","ID")
                .option("numPartitions",6);

        Dataset<Row> dataset=dataFrameReader.load().cache();

//        dataset.createOrReplaceTempView("knowledge_base_view");
//        Dataset<Row> dataset1=sparkSession.sql("select id,ask_value from knowledge_base_view");
//        dataset1.show(10);

//        Dataset<Row> dataset1=dataset.repartition(6);

        System.out.println("==================> 分区数："+dataset.javaRDD().getNumPartitions());

        dataset.count();

//        dataset.repartition(6).foreachPartition(v1 -> {
//            System.out.println();
//        });


//        Stopwatch watch1 = Stopwatch.createStarted();
//        dataset.printSchema();
//        dataset.show(10);
//        LogUtil.info("============take耗时:"+watch1.elapsed(TimeUnit.MILLISECONDS)+" ms");

        return dataset.toJSON().javaRDD().cache();
    }

    public void insertSql(){
        if(null==sqlContext)
            refreshContext();
        Properties connectionProperties = new Properties();
        connectionProperties.put("user",getUsername());
        connectionProperties.put("password",getPassword());
        connectionProperties.put("driver",getDriverClassName());

        List structFields = new ArrayList();
        structFields.add(DataTypes.createStructField("id",DataTypes.LongType,true));
        structFields.add(DataTypes.createStructField("ask_value",DataTypes.StringType,true));
        structFields.add(DataTypes.createStructField("ans_value",DataTypes.StringType,true));
        structFields.add(DataTypes.createStructField("hit",DataTypes.IntegerType,true));

        //构建StructType，用于最后DataFrame元数据的描述
        StructType structType = DataTypes.createStructType(structFields);
        List<Map<String,Object>> lst= Lists.newArrayList();

        long start=System.currentTimeMillis();
        for(int i=10;i<3000000;i++){
            Map<String,Object> mod= Maps.newHashMap();
            mod.put("id",Long.valueOf(i));
            mod.put("ask_value","如果目标文件目录中数据已经存在"+i);
            mod.put("ans_value","则用需要保存的数据覆盖掉已经存在的数据,但不同时候的参数是不同的"+i);
            mod.put("hit",0);
            lst.add(mod);
        }
        JavaRDD<Map<String,Object>> rdd=getSc().parallelize(lst);
        JavaRDD<Row> personsRDD =rdd.map(v->{
            return RowFactory.create(v.get("id"),v.get("ask_value"),v.get("ans_value"),v.get("hit"));
        });

        Dataset<Row> rowDataset = sparkSession.createDataFrame(personsRDD, structType);
        System.out.println("转换数据完成："+(start-System.currentTimeMillis())+" ms");

        rowDataset.write().mode("append").format("jdbc")
                .option("url",getUrl())
                .option("dbtable","knowledge_base")
                .option("user", getUsername())
                .option("password", getPassword())
                .option("driver", getDriverClassName())
                .option("batchsize",20000)
                .option("isolationLevel","NONE")
                .option("truncate","false").save();
        System.out.println("插入数据完成："+(start-System.currentTimeMillis())+" ms");
    }

    public JavaSparkContext getSc() {
        return sc;
    }

    public JavaRDD<String> getSourceContainer() {
        return sourceContainer;
    }

    public void refreshContainer(JavaRDD<String> sourceContainer){
        this.sourceContainer=sourceContainer;
        this.sourceContainer.persist(StorageLevel.MEMORY_AND_DISK_SER());
    }
}
