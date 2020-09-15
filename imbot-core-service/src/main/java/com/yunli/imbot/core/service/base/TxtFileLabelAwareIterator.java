package com.yunli.imbot.core.service.base;

import org.apache.commons.lang.StringUtils;
import org.deeplearning4j.clustering.util.MathUtils;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.LabelsSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TxtFileLabelAwareIterator implements LabelAwareIterator {

    private static final long serialVersionUID = 1L;

    private int totalCount;
    private List<String> original;
    private List<String> normSource;
    private final Random rng;
    private final int[] order;
    private LabelsSource source;
    private int cursor = 0;

    public TxtFileLabelAwareIterator(String path) {
        this(path, new Random());
    }
    public TxtFileLabelAwareIterator(String path, Random rng) {
        totalCount = 0;
        original = new ArrayList<String>();
        normSource = new ArrayList<>();
        BufferedReader buffered = null;
        try {
            buffered = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path)));
            String line = buffered.readLine();
            while (line != null) {
                String[] lines= StringUtils.split(line,",");
                normSource.add(lines[0]);
                original.add(line);
                totalCount++;
                line = buffered.readLine();
            }
            buffered.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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