package co.nilin.opex.bcgateway.app.controller

import co.nilin.opex.bcgateway.core.model.CurrencyInfo
import co.nilin.opex.bcgateway.core.spi.CurrencyLoader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class NetworkController(val currencyLoader: CurrencyLoader) {

    @GetMapping("currency/{currency}")
    suspend fun fetchCurrencyInfo(@PathVariable("currency") currency: String): CurrencyInfo {
        return currencyLoader.fetchCurrencyInfo(currency)
    }
}