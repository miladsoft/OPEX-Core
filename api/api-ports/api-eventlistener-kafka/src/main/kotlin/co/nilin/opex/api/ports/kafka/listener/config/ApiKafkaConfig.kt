package co.nilin.opex.api.ports.kafka.listener.config

import co.nilin.opex.matching.engine.core.eventh.events.CoreEvent
import co.nilin.opex.api.ports.kafka.listener.consumer.EventKafkaListener
import co.nilin.opex.api.ports.kafka.listener.consumer.OrderKafkaListener
import co.nilin.opex.api.ports.kafka.listener.consumer.TradeKafkaListener
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.regex.Pattern

@Configuration
class ApiKafkaConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Value("\${spring.kafka.consumer.group-id}")
    private val groupId: String? = null

    @Bean("apiConsumerConfig")
    fun consumerConfigs(): Map<String, Any?>? {
        val props: MutableMap<String, Any?> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[JsonDeserializer.TRUSTED_PACKAGES] = "co.nilin.opex.*"
        return props
    }

    @Bean("apiConsumerFactory")
    fun consumerFactory(@Qualifier("apiConsumerConfig") consumerConfigs: Map<String, Any?>): ConsumerFactory<String, CoreEvent> {
        return DefaultKafkaConsumerFactory(consumerConfigs)
    }

    @Bean("apiProducerConfig")
    fun producerConfigs(): Map<String, Any?> {
        val props: MutableMap<String, Any?> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return props
    }

    @Bean("apiProducerFactory")
    fun producerFactory(@Qualifier("apiProducerConfig") producerConfigs: Map<String, Any?>): ProducerFactory<String?, CoreEvent> {
        return DefaultKafkaProducerFactory(producerConfigs)
    }

    @Bean("apiKafkaTemplate")
    fun kafkaTemplate(@Qualifier("apiProducerFactory") producerFactory: ProducerFactory<String?, CoreEvent>): KafkaTemplate<String?, CoreEvent> {
        return KafkaTemplate(producerFactory)
    }


    @Autowired
    @ConditionalOnBean(TradeKafkaListener::class)
    fun configureTradeListener(
        tradeListener: TradeKafkaListener,
        @Qualifier("apiConsumerFactory") consumerFactory: ConsumerFactory<String, CoreEvent>
    ) {
        val containerProps = ContainerProperties(Pattern.compile("richTrade"))
        containerProps.messageListener = tradeListener
        val container = ConcurrentMessageListenerContainer(consumerFactory, containerProps)
        container.setBeanName("ApiTradeKafkaListenerContainer")
        container.start()
    }

    @Autowired
    @ConditionalOnBean(EventKafkaListener::class)
    fun configureEventListener(
        eventListener: EventKafkaListener,
        @Qualifier("apiConsumerFactory") consumerFactory: ConsumerFactory<String, CoreEvent>
    ) {
        val containerProps = ContainerProperties(Pattern.compile("events_.*"))
        containerProps.messageListener = eventListener
        val container = ConcurrentMessageListenerContainer(consumerFactory, containerProps)
        container.setBeanName("ApiEventKafkaListenerContainer")
        container.start()
    }

    @Autowired
    @ConditionalOnBean(OrderKafkaListener::class)
    fun configureOrderListener(
        orderListener: OrderKafkaListener,
        @Qualifier("apiConsumerFactory") consumerFactory: ConsumerFactory<String, CoreEvent>
    ) {
        val containerProps = ContainerProperties(Pattern.compile("richOrder"))
        containerProps.messageListener = orderListener
        val container = ConcurrentMessageListenerContainer(consumerFactory, containerProps)
        container.setBeanName("ApiOrderKafkaListenerContainer")
        container.start()
    }

    @Autowired
    fun createTopics(applicationContext: GenericApplicationContext) {
        applicationContext.registerBean("topic_richOrder", NewTopic::class.java, "richOrder", 10, 1)
        applicationContext.registerBean("topic_richTrade", NewTopic::class.java, "richTrade", 10, 1)
    }


}