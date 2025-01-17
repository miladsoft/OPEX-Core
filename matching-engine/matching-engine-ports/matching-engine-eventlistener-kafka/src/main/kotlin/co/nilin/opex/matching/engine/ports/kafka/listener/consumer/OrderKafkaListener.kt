package co.nilin.opex.matching.engine.ports.kafka.listener.consumer

import co.nilin.opex.matching.engine.ports.kafka.listener.inout.OrderSubmitRequest
import co.nilin.opex.matching.engine.ports.kafka.listener.spi.OrderSubmitRequestListener
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.listener.MessageListener
import org.springframework.stereotype.Component

@Component
class OrderKafkaListener : MessageListener<String, OrderSubmitRequest> {
    val orderListeners = arrayListOf<OrderSubmitRequestListener>()
    override fun onMessage(data: ConsumerRecord<String, OrderSubmitRequest>) {
        orderListeners.forEach { tl ->
            runBlocking {
                tl.onOrder(data.value(), data.partition(), data.offset(), data.timestamp())
            }
        }
    }

    fun addOrderListener(tl: OrderSubmitRequestListener) {
        orderListeners.add(tl)
    }

    fun removeOrderListener(tl: OrderSubmitRequestListener) {
        orderListeners.removeIf { item ->
            item.id() == tl.id()
        }
    }
}