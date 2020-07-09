package data.service.impl;

import data.domain.DataParameter;
import data.service.ParameterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.avaje.agentloader.AgentLoader;
import org.junit.Test;

import java.util.List;

@Slf4j
public class DubboTest {
    static {
        // set ebean auto enhance
        if (!AgentLoader.loadAgentByMainClass("io.ebean.enhance.Transformer", "debug=1")) {
            log.info("ebean-agent not found in classpath - not dynamically loaded");
        }
    }

    @Test
    public void test() {

        ReferenceConfig<ParameterService> config = new ReferenceConfig<>();
        config.setUrl("dubbo://localhost:20881");
        config.setApplication(new ApplicationConfig("test"));
        config.setTimeout(50000);

        config.setInterface(ParameterService.class);

        ParameterService service = config.get();
        List<DataParameter> list = service.all();
        System.out.println(list.size());
    }
}
