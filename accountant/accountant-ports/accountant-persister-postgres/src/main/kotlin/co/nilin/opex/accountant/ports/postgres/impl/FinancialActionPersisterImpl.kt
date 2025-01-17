package co.nilin.opex.accountant.ports.postgres.impl

import co.nilin.opex.accountant.core.model.FinancialAction
import co.nilin.opex.accountant.core.model.FinancialActionStatus
import co.nilin.opex.accountant.core.spi.FinancialActionPersister
import co.nilin.opex.accountant.ports.postgres.dao.FinancialActionRepository
import co.nilin.opex.accountant.ports.postgres.model.FinancialActionModel
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrElse
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FinancialActionPersisterImpl(val financialActionRepository: FinancialActionRepository) :
    FinancialActionPersister {

    override suspend fun persist(financialActions: List<FinancialAction>): List<FinancialAction> {
        financialActionRepository.saveAll(financialActions.map { fa ->
            FinancialActionModel(
                null,
                fa.parent?.id,
                fa.eventType,
                fa.pointer,
                fa.symbol,
                fa.amount.toDouble(),
                fa.sender,
                fa.senderWalletType,
                fa.receiver,
                fa.receiverWalletType,
                "",
                "",
                fa.createDate
            )
        }).awaitLast()
        return financialActions
    }

    override suspend fun updateStatus(financialAction: FinancialAction, status: FinancialActionStatus) {
        val existing = financialActionRepository.findById(financialAction.id!!).awaitFirstOrElse {
            throw IllegalArgumentException()
        }
        financialActionRepository.save(
            FinancialActionModel(
                existing.id,
                existing.parentId,
                existing.eventType,
                existing.pointer,
                existing.symbol,
                existing.amount,
                existing.sender,
                existing.senderWalletType,
                existing.receiver,
                existing.receiverWalletType,
                existing.agent,
                existing.ip,
                existing.createDate,
                status,
                1 + (existing.retryCount ?: 0),
                LocalDateTime.now()
            )
        ).awaitFirst()
    }
}