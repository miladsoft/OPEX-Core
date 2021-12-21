package co.nilin.opex.referral.app.config

import co.nilin.opex.accountant.core.inout.RichTrade
import co.nilin.opex.referral.core.api.CommissionRewardCalculator
import co.nilin.opex.referral.core.spi.CommissionRewardPersister
import co.nilin.opex.referral.ports.kafka.listener.consumer.RichTradeKafkaListener
import co.nilin.opex.referral.ports.kafka.listener.spi.RichTradeListener
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {
    @Bean
    fun referralListener(
        commissionRewardPersister: CommissionRewardPersister,
        commissionRewardCalculator: CommissionRewardCalculator
    ): ReferralListenerImpl {
        return ReferralListenerImpl(commissionRewardPersister, commissionRewardCalculator)
    }

    @Autowired
    fun configureListeners(
        richTradeKafkaListener: RichTradeKafkaListener,
        appListener: ReferralListenerImpl
    ) {
        richTradeKafkaListener.addTradeListener(appListener)
    }

    class ReferralListenerImpl(
        private val commissionRewardPersister: CommissionRewardPersister,
        private val commissionRewardCalculator: CommissionRewardCalculator
    ) : RichTradeListener {
        override fun id() = "ReferralListener"

        override fun onTrade(
            richTrade: RichTrade,
            partition: Int,
            offset: Long,
            timestamp: Long
        ) {
            runBlocking(AppDispatchers.kafkaExecutor) {
                val makeCommission = commissionRewardCalculator.calculate(richTrade.makerUuid, richTrade)
                val takerCommission = commissionRewardCalculator.calculate(richTrade.makerUuid, richTrade)
                commissionRewardPersister.save(makeCommission)
                commissionRewardPersister.save(takerCommission)
            }
        }
    }
}