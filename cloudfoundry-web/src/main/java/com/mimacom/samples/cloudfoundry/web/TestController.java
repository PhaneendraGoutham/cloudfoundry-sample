package com.mimacom.samples.cloudfoundry.web;


import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/test")
public class TestController {

    @Autowired(required = false)
    private ConnectionFactory rabbitConnectionFactory;

    @Autowired(required = false)
    private EurekaClient eurekaClient;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, String> test() {
        Map<String, String> result = new HashMap<>();
        result.put("rabbit-mq", getRabbitInfoString());
        result.put("eureka", getEurekaInfoString());
        return result;
    }

    private String getRabbitInfoString() {
        if (rabbitConnectionFactory == null) {
            return "<none>";
        } else {
            String s = rabbitConnectionFactory.getHost() + ":" + rabbitConnectionFactory.getPort();
            if (this.rabbitTemplate == null) {
                s = s + " NO RABBIT TEMPLATE";
            }
            return s;
        }
    }

    private String getEurekaInfoString() {
        if (eurekaClient == null) {
            return "<none>";
        }
        return this.getApplications().toString();
    }

    private Map<String, Object> getApplications() {
        Applications applications = this.eurekaClient.getApplications();
        if (applications == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<>();
        for (Application application : applications.getRegisteredApplications()) {
            result.put(application.getName(), application.getInstances().size());
        }
        return result;
    }
}
