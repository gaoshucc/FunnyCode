package com.funnycode.blog.configration;

import com.funnycode.blog.async.consumer.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author CC
 * @date 2019-09-23 09:31
 */
@Configuration
@EnableRabbit
public class RabbitMQConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.virtual-host}")
    private String vhost;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.listener.simple.concurrency}")
    private int concurrency;

    @Value("${spring.rabbitmq.listener.simple.max-concurrency}")
    private int maxConcurrency;

    @Value("${spring.rabbitmq.listener.direct.prefetch}")
    private int prefetch;

    /**
     * 交换器Key
     */
    public static final String EXCHANGE_BLOG = "exchange.blog";

    /**
     * 消息队列Key
     */
    public static final String QUEUE_DEFAULT = "queue.default";
    public static final String QUEUE_LIKE = "queue.like";
    public static final String QUEUE_COMMENT = "queue.comment";
    public static final String QUEUE_LOGIN = "queue.login";
    public static final String QUEUE_MAIL = "queue.mail";
    public static final String QUEUE_FOLLOW = "queue.follow";
    public static final String QUEUE_UNFOLLOW = "queue.unfollow";
    public static final String QUEUE_LOG = "queue.log";
    public static final String QUEUE_REGIST = "queue.regist";
    public static final String QUEUE_COLLECT = "queue.collect";
    public static final String QUEUE_FEED = "queue.feed";
    public static final String QUEUE_REMOVEFEED = "queue.removefeed";

    /**
     * RoutingKey
     */
    public static final String ROUTING_DEFAULT = "routing.default";
    public static final String ROUTING_LIKE = "routing.like";
    public static final String ROUTING_COMMENT = "routing.comment";
    public static final String ROUTING_LOGIN = "routing.login";
    public static final String ROUTING_MAIL = "routing.mail";
    public static final String ROUTING_FOLLOW = "routing.follow";
    public static final String ROUTING_UNFOLLOW = "routing.unfollow";
    public static final String ROUTING_LOG = "routing.log";
    public static final String ROUTING_REGIST = "routing.regist";
    public static final String ROUTING_COLLECT = "routing.collect";
    public static final String ROUTING_FEED = "routing.feed";
    public static final String ROUTING_REMOVEFEED = "routing.removefeed";

    @Bean
    public DirectExchange blogExchange(){
        return new DirectExchange(EXCHANGE_BLOG);
    }

    @Bean
    public Queue defaultQueue(){
        return new Queue(QUEUE_DEFAULT, true);
    }

    @Bean
    public Binding bindDefault(){
        return BindingBuilder.bind(defaultQueue()).to(blogExchange()).with(ROUTING_DEFAULT);
    }

    @Bean
    public Queue likeQueue(){
        return new Queue(QUEUE_LIKE, true);
    }

    @Bean
    public Binding bindLike(){
        return BindingBuilder.bind(likeQueue()).to(blogExchange()).with(ROUTING_LIKE);
    }

    @Bean
    public Queue commentQueue(){
        return new Queue(QUEUE_COMMENT, true);
    }

    @Bean
    public Binding bindComment(){
        return BindingBuilder.bind(commentQueue()).to(blogExchange()).with(ROUTING_COMMENT);
    }

    @Bean
    public Queue loginQueue(){
        return new Queue(QUEUE_LOGIN, true);
    }

    @Bean
    public Binding bindLogin(){
        return BindingBuilder.bind(loginQueue()).to(blogExchange()).with(ROUTING_LOGIN);
    }

    @Bean
    public Queue mailQueue(){
        return new Queue(QUEUE_MAIL, true);
    }

    @Bean
    public Binding bindMail(){
        return BindingBuilder.bind(mailQueue()).to(blogExchange()).with(ROUTING_MAIL);
    }

    @Bean
    public Queue followQueue(){
        return new Queue(QUEUE_FOLLOW, true);
    }

    @Bean
    public Binding bindFollow(){
        return BindingBuilder.bind(followQueue()).to(blogExchange()).with(ROUTING_FOLLOW);
    }

    @Bean
    public Queue unfollowQueue(){
        return new Queue(QUEUE_UNFOLLOW, true);
    }

    @Bean
    public Binding bindUnfollow(){
        return BindingBuilder.bind(unfollowQueue()).to(blogExchange()).with(ROUTING_UNFOLLOW);
    }

    @Bean
    public Queue logQueue(){
        return new Queue(QUEUE_LOG, true);
    }

    @Bean
    public Binding bindLog(){
        return BindingBuilder.bind(logQueue()).to(blogExchange()).with(ROUTING_LOG);
    }

    @Bean
    public Queue registQueue(){
        return new Queue(QUEUE_REGIST, true);
    }

    @Bean
    public Binding bindRegist(){
        return BindingBuilder.bind(registQueue()).to(blogExchange()).with(ROUTING_REGIST);
    }

    @Bean
    public Queue collectQueue(){
        return new Queue(QUEUE_COLLECT, true);
    }

    @Bean
    public Binding bindCollect(){
        return BindingBuilder.bind(collectQueue()).to(blogExchange()).with(ROUTING_COLLECT);
    }

    @Bean
    public Queue feedQueue(){
        return new Queue(QUEUE_FEED, true);
    }

    @Bean
    public Binding bindFeed(){
        return BindingBuilder.bind(collectQueue()).to(blogExchange()).with(ROUTING_FEED);
    }

    @Bean
    public Queue removefeedQueue(){
        return new Queue(QUEUE_REMOVEFEED, true);
    }

    @Bean
    public Binding bindRemovefeed(){
        return BindingBuilder.bind(collectQueue()).to(blogExchange()).with(ROUTING_REMOVEFEED);
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory());
        rabbitTemplate.setMandatory(true);

        return rabbitTemplate;
    }

    @Bean
    public DefaultConsumer defaultConsumer(){
        return new DefaultConsumer();
    }

    /**
     * 多个消费者
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer multiListenerContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(cachingConnectionFactory());
        container.setQueues(likeQueue(), commentQueue(), loginQueue(), mailQueue(),
                followQueue(), unfollowQueue(), logQueue(), defaultQueue());
        container.setMessageListener(defaultConsumer());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setConcurrentConsumers(concurrency);
        container.setMaxConcurrentConsumers(maxConcurrency);
        container.setPrefetchCount(prefetch);

        return container;
    }


    /*
    *//**
     * 单一消费者
     * @return
     *//*
    @Bean
    public SimpleMessageListenerContainer listenerContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(cachingConnectionFactory());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setPrefetchCount(1);
        container.setTxSize(1);
        return container;
    }
    */
}
