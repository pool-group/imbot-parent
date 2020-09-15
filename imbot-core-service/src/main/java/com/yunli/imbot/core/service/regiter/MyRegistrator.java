package com.yunli.imbot.core.service.regiter;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.yunli.imbot.core.service.bean.AfterDataBean;
import com.yunli.imbot.core.service.bean.PreDataBean;
import org.apache.spark.serializer.KryoRegistrator;
import org.springframework.stereotype.Component;

/**
 * com.yunli.imbot.app.config
 *
 * @author duanpeng
 * @version Id: MyRegistrator.java, v 0.1 2020年08月03日 11:51 duanpeng Exp $
 */
@Component
public class MyRegistrator implements KryoRegistrator {

    @Override
    public void registerClasses(Kryo arg0) {
        arg0.register(PreDataBean.class, new FieldSerializer(arg0, PreDataBean.class));
        arg0.register(AfterDataBean.class, new FieldSerializer(arg0, AfterDataBean.class));
    }
}
