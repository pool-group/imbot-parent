package com.yunli.imbot.common.util;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * com.yunli.spbot.app.util
 *
 * @author duanpeng
 * @version Id: IKAnalyzerUtil.java, v 0.1 2020年08月25日 13:09 duanpeng Exp $
 */
public class IKAnalyzerUtil {

    public static List<String> cutToList(String seq) throws IOException {
        StringReader sr=new StringReader(seq);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        List<String> list=new ArrayList<>();
        while((lex=ik.next())!=null){
            list.add(lex.getLexemeText());
        }
        return list;
    }

    public static String cutToSpace(String seq) throws IOException {
        StringReader sr=new StringReader(seq);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        List<String> list=new ArrayList<>();
        while((lex=ik.next())!=null){
            list.add(lex.getLexemeText());
        }
        return list.stream().collect(Collectors.joining(" "));
    }

    public static String toHash(String key) {
        int arraySize = 99991; //取素数
        int hashCode = 0;
        for (int i = 0; i < key.length(); i++) { // 从字符串的左边开始计算
            int letterValue = key.charAt(i) - 96;// 将获取到的字符串转换成数字，比如a的码值是97，则97-96=1
            hashCode = ((hashCode << 5) + letterValue) % arraySize;// 防止编码溢出，对每步结果都进行取模运算
        }
        return String.valueOf(hashCode);
    }

}