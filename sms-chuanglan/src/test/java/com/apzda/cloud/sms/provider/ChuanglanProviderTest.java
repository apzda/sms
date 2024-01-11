package com.apzda.cloud.sms.provider;

import com.apzda.cloud.sms.config.ProviderProperties;
import com.apzda.cloud.sms.dto.Sms;
import com.apzda.cloud.sms.dto.Variable;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author fengz (windywany@gmail.com)
 * @version 1.0.0
 * @since 1.0.0
 **/
class ChuanglanProviderTest {

    @Test
    void send() throws Exception {
        // given
        val pp = new ProviderProperties();
        pp.setUsername("YZM3603165");
        pp.setPassword("IiPNr6T57552e5");
        pp.setEndpoint("https://smssh1.253.com/msg/v1/");
        val smsProvider = new ChuanglanProvider();
        smsProvider.init(pp);
        val sms = new Sms();
        sms.setPhone("18088888888");
        sms.setPhone("18049920019");
        sms.setSignName("签名");
        sms.setContent("【趣微玩】验证码是：{$var},请不要把验证码透漏给其他人。");
        sms.setOriginal("【趣微玩】验证码是：{$var},请不要把验证码透漏给其他人。");
        sms.setVariables(List.of(new Variable("$var", "005588", 0)));
        // when
        var result = smsProvider.send(sms);

        // then
        assertThat(result).isTrue();
    }

}
