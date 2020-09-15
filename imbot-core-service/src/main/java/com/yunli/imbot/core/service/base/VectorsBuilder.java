package com.yunli.imbot.core.service.base;

import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.models.embeddings.learning.impl.sequence.DM;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.util.ArrayList;

/**
 * Created by duanp on 2020/7/26.
 */
public class VectorsBuilder {

    public ParagraphVectors paragraphBuilder(LabelAwareIterator iterate){
        AbstractCache<VocabWord> cache=new AbstractCache<>();
        TokenizerFactory tokenizerFactory=new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        return new ParagraphVectors.Builder()
                .minWordFrequency(1)
                .iterations(1)
                .seed(119)
                .epochs(1)
                .layerSize(100)
                .learningRate(0.025)
                .minLearningRate(0.001)
                .labelsSource(iterate.getLabelsSource())
                .windowSize(5)
                .sequenceLearningAlgorithm(new DM<VocabWord>())
                .iterate(iterate)
//                .trainWordVectors(false)
//                .usePreciseWeightInit(true)
                .batchSize(8192)
                .tokenizerFactory(tokenizerFactory)
                .workers(4)
                .sampling(0)
                .build();
    }

    public TfidfVectorizer tfidfBuilder(LabelAwareIterator iterate){
        AbstractCache<VocabWord> cache=new AbstractCache<>();
        TokenizerFactory tokenizerFactory=new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        return new TfidfVectorizer.Builder()
                .setMinWordFrequency(1)
                .setStopWords(new ArrayList())
                .setTokenizerFactory(tokenizerFactory)
                .setIterator(iterate)
//                .setVocab(cache)
                .build();
    }
}
