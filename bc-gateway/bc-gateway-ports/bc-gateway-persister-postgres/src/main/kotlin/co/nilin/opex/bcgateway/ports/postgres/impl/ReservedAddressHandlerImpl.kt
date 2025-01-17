package co.nilin.opex.bcgateway.ports.postgres.impl

import co.nilin.opex.bcgateway.core.model.AddressType
import co.nilin.opex.bcgateway.core.model.ReservedAddress
import co.nilin.opex.bcgateway.core.spi.ReservedAddressHandler
import co.nilin.opex.bcgateway.ports.postgres.dao.ReservedAddressRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component

@Component
class ReservedAddressHandlerImpl(private val reservedAddressRepository: ReservedAddressRepository) :
    ReservedAddressHandler {
    override suspend fun peekReservedAddress(addressType: AddressType): ReservedAddress? {
        return reservedAddressRepository.peekFirstAdded(addressType.id)
            .map { ReservedAddress(it.address, it.memo, addressType) }.awaitFirstOrNull()
    }

    override suspend fun remove(reservedAddress: ReservedAddress) {
        reservedAddressRepository.remove(reservedAddress.address, reservedAddress.memo).awaitFirst()
    }
}
