package co.nilin.opex.api.core.spi

import co.nilin.opex.api.core.inout.PairInfoResponse

interface AccountantProxy {

    suspend fun getPairConfigs(): List<PairInfoResponse>

}