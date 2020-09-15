package com.yunli.imbot.core.service.base;

import com.yunli.imbot.core.service.initialize.SparkBaseContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.LongAccumulator;
import org.deeplearning4j.clustering.util.MathUtils;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.LabelsSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataLabelAwareIterator implements LabelAwareIterator, Serializable {

    private static final long serialVersionUID = 1L;

    private int totalCount;
    private List<String> original;
    private List<String> normSource;
    private final Random rng;
    private final int[] order;
    private LabelsSource source;
    private int cursor = 0;

    public DataLabelAwareIterator(JavaRDD<String> rdd,SparkBaseContext sparkBaseContext) {
        this(rdd, new Random(),sparkBaseContext);
    }

    public DataLabelAwareIterator(JavaRDD<String> rdd, Random rng, SparkBaseContext sparkBaseContext) {
        totalCount = 0;
        original = new ArrayList<String>();
        normSource = new ArrayList<>();
        CollectionAccumulator<String> originalAccumulator=sparkBaseContext.getSc().sc().collectionAccumulator();
        CollectionAccumulator<String> normSourceAccumulator=sparkBaseContext.getSc().sc().collectionAccumulator();
        LongAccumulator countAccumulator=sparkBaseContext.getSc().sc().longAccumulator();
        long start=System.currentTimeMillis();
        rdd.repartition(6).foreachPartition(v->{
            if(v.hasNext()){
                String line=v.next();
                System.out.println();
            }
        });
        System.out.println("处理数据x："+(start-System.currentTimeMillis())+" ms");
        totalCount=countAccumulator.value().intValue();
        original.addAll(originalAccumulator.value());
        normSource.addAll(normSourceAccumulator.value());
        this.rng = rng;
        if (rng == null) {
            order = null;
        } else {
            order = new int[totalCount];
            for (int i = 0; i < totalCount; i++) {
                order[i] = i;
            }
            MathUtils.shuffleArray(order,rng);
        }
        source = new LabelsSource(normSource);
    }

    @Override
    public boolean hasNext() {
        return cursor < totalCount;
    }
    @Override
    public LabelledDocument next() {
        return nextDocument();
    }
    @Override
    public boolean hasNextDocument() {
        return cursor < totalCount;
    }
    @Override
    public LabelledDocument nextDocument() {
        LabelledDocument document = new LabelledDocument();
        int idx;
        if (rng == null) {
            idx = cursor++;
        } else {
            idx = order[cursor++];
        }
        if(normSource.size()>0){
            String label = normSource.get(idx);
            document.addLabel(label);
        }
        String sentence;
        sentence = original.get(idx).split(",")[1];
        document.setContent(sentence);
        return document;
    }
    @Override
    public void reset() {
        cursor = 0;
        if (rng != null) {
            MathUtils.shuffleArray(order,rng);
        }
    }
    @Override
    public LabelsSource getLabelsSource() {
        return source;
    }
    @Override
    public void shutdown() {
    }

    public List<String> getOriginal() {
        return original;
    }

}