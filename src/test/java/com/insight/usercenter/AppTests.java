package com.insight.usercenter;

import com.insight.usercenter.common.utils.message.Message;
import com.insight.usercenter.common.utils.message.SmsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AppTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testApp() {
        Message message = new Message();
        message.setTemplate("SMS_70105327");

        Map map = new HashMap();
        map.put("code", "123456");
        map.put("product", "apin-sms");
        message.setParams(map);

        List<String> list = new ArrayList<>();
        list.add("11111111111");

        message.setReceivers(list);
//        message.setSmsTypeEnum(SmsTypeEnum.Verify);

        SmsUtils.sendSms(message);
    }
}
