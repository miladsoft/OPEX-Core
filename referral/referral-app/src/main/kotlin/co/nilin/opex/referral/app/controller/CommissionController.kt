package co.nilin.opex.referral.app.controller

import co.nilin.opex.referral.core.model.CommissionReward
import co.nilin.opex.referral.core.spi.CommissionRewardHandler
import org.springframework.web.bind.annotation.*

@RestController
class CommissionController(private val commissionRewardHandler: CommissionRewardHandler) {
    @GetMapping("/commissions/{code}")
    suspend fun getCommissionsByReferrerAndCode(
        @PathVariable code: String
    ): List<CommissionReward> {
        return commissionRewardHandler.findCommissions(referralCode = code)
    }

    @GetMapping("/commissions")
    suspend fun getCommissionsByReferent(
        @RequestParam referrerUuid: String?,
        @RequestParam referentUuid: String?
    ): List<CommissionReward> {
        return commissionRewardHandler.findCommissions(referentUuid = referentUuid, referrerUuid = referrerUuid)
    }

    @DeleteMapping("/commissions")
    suspend fun deleteCommissions(
        @RequestParam code: String?,
        @RequestParam referrerUuid: String?,
        @RequestParam referentUuid: String?
    ) {
        commissionRewardHandler.deleteCommissions(code, referrerUuid, referentUuid)
    }

    @DeleteMapping("/commissions/{id}")
    suspend fun deleteCommissionById(@PathVariable id: Long) {
        commissionRewardHandler.deleteCommissionById(id)
    }
}