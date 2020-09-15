package com.yunli.imbot.core.service.process.impl;

import com.yunli.imbot.common.util.IKAnalyzerUtil;
import com.yunli.imbot.core.service.base.TxtFileLabelAwareIterator;
import com.yunli.imbot.core.service.base.VectorsBuilder;
import com.yunli.imbot.core.service.bean.AfterDataBean;
import com.yunli.imbot.core.service.initialize.SparkBaseContext;
import com.yunli.imbot.core.service.process.MachineLearningService;
import lombok.RequiredArgsConstructor;
import org.apache.spark.util.CollectionAccumulator;
import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.util.SerializationUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * com.yunli.imbot.core.service.process.impl
 *
 * @author duanpeng
 * @version Id: AiBotTrainService.java, v 0.1 2020年08月27日 17:44 duanpeng Exp $
 */
@Service
@RequiredArgsConstructor
public class MachineLearningServiceImpl implements MachineLearningService {

    private final SparkBaseContext sparkBaseContext;

    @Value("${target.path}")
    private String targetPath;
    @Value("${target.factory-path}")
    private String factoryPath;

    @Override
    public void train() {
        ch.qos.logback.classic.LoggerContext loggerContext =(ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("root").setLevel(ch.qos.logback.classic.Level.toLevel("WARN"));

        long start=System.currentTimeMillis();
        TxtFileLabelAwareIterator iterator=new TxtFileLabelAwareIterator(targetPath);
        System.out.println("读数据："+(start-System.currentTimeMillis())+" ms");

        long start2=System.currentTimeMillis();
        TfidfVectorizer tfidfVectorizer=new VectorsBuilder().tfidfBuilder(iterator);
        tfidfVectorizer.fit();
        System.out.println("训练耗时："+(start2-System.currentTimeMillis())+" ms");

        long start3=System.currentTimeMillis();
        SerializationUtils.saveObject(tfidfVectorizer,new File(factoryPath));
        System.out.println("保存tfidfVectorizer耗时："+(start3-System.currentTimeMillis())+" ms");

        long start4=System.currentTimeMillis();
        sparkBaseContext.refreshContainer(sparkBaseContext.getSc().parallelize(iterator.getOriginal()));
        System.out.println("list转rdd："+(start4-System.currentTimeMillis())+" ms");

        System.out.println("模型训练完成..");
        System.out.println("总耗时："+(start-System.currentTimeMillis())+" ms");
        System.out.println();
    }

    @Override
    public void predict(String request) {
        long start=System.currentTimeMillis();
        TfidfVectorizer tfidfVectorizer=SerializationUtils.readObject(new File(factoryPath));
        System.out.println("读tfidfVectorizer工厂耗时："+(start-System.currentTimeMillis())+" ms");

        long start1=System.currentTimeMillis();
        System.out.println("开始处理向量..");
        CollectionAccumulator<AfterDataBean> collectionAccumulator=sparkBaseContext.getSc().sc().collectionAccumulator();
        sparkBaseContext.getSourceContainer().repartition(6).foreachPartition(v-> {
            TokenizerFactory tokenizerFactory=new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
            tfidfVectorizer.setTokenizerFactory(tokenizerFactory);
            if(v.hasNext()){
                String[] line=v.next().split(",");
                AfterDataBean tr=new AfterDataBean(line[0], Transforms.cosineSim(tfidfVectorizer.transform(IKAnalyzerUtil.cutToSpace(request)), tfidfVectorizer.transform(line[1])));
                if(tr.getScore()>0.01){
                    collectionAccumulator.add(tr);
                }
            }
        });
        System.out.println("处理向量数据耗时："+(start1-System.currentTimeMillis())+" ms");

        System.out.println("总耗时："+(start-System.currentTimeMillis())+" ms");
        System.out.println("智能机器人问答训练精准率："+(collectionAccumulator.value().size()==0?0:collectionAccumulator.value().get(0).getId()+" = "+collectionAccumulator.value().get(0).getScore()));
        System.out.println();
    }
}
