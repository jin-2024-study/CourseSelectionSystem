package liu.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.Queue;
import javax.jms.Topic;
import java.util.Arrays;

/**
 * ActiveMQ配置类
 * @author liu
 */
@Configuration
@EnableJms
public class ActiveMQConfig {
    
    public static final String COURSE_SELECTION_QUEUE = "course.selection.queue";
    public static final String COURSE_SELECTION_TOPIC = "course.selection.topic";
    
    /**
     * 消息转换器
     */
    @Bean
    @Primary
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
    
    /**
     * ActiveMQ连接工厂 - 连接外部ActiveMQ服务器
     */
    @Bean
    @Primary
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        // 连接外部ActiveMQ服务器
        connectionFactory.setBrokerURL("tcp://localhost:61616");
        connectionFactory.setUserName("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setTrustedPackages(Arrays.asList("*"));
        
        // 设置重新投递策略
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);
        redeliveryPolicy.setInitialRedeliveryDelay(1000);
        redeliveryPolicy.setRedeliveryDelay(2000);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        
        return connectionFactory;
    }
    
    /**
     * JMS模板配置
     */
    @Bean
    @Primary
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        return template;
    }
    
    /**
     * JMS监听器容器工厂配置
     */
    @Bean
    @Primary
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setConcurrency("1-3");
        factory.setAutoStartup(true);
        return factory;
    }
    
    /**
     * 选课通知队列
     */
    @Bean
    public Queue courseSelectionQueue() {
        return new ActiveMQQueue(COURSE_SELECTION_QUEUE);
    }
    
    /**
     * 选课通知主题
     */
    @Bean
    public Topic courseSelectionTopic() {
        return new ActiveMQTopic(COURSE_SELECTION_TOPIC);
    }
} 