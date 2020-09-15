package com.yunli.imbot.core.service.process;

/**
 * com.yunli.imbot.core.service.process
 *
 * @author duanpeng
 * @version Id: AiBotTrainFactory.java, v 0.1 2020年08月27日 17:42 duanpeng Exp $
 */
public interface MachineLearningService {

    void train();

    void predict(String request);
}
