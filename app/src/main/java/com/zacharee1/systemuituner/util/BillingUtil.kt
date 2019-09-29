package com.zacharee1.systemuituner.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.PurchaseEvent
import com.zacharee1.systemuituner.R


class BillingUtil(private val activity: Activity) {
    private val client: BillingClient

    init {
        client = BillingClient.newBuilder(activity).setListener { responseCode, purchases ->
            if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                for (purchase in purchases) {
                    Answers.getInstance().logPurchase(PurchaseEvent()
                            .putItemId(purchase.sku)
                            .putSuccess(true)
                            .putCustomAttribute("orderId", purchase.orderId)
                            .putCustomAttribute("origJson", purchase.originalJson)
                            .putCustomAttribute("packageName", purchase.packageName)
                            .putCustomAttribute("token", purchase.purchaseToken)
                            .putCustomAttribute("time", purchase.purchaseTime)
                            .putCustomAttribute("signature", purchase.signature))
                    consumeAsync(purchase.purchaseToken)
                }
            }
        }.build()

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    val ppTitle = activity.findViewById<TextView>(R.id.paypal_title)
                    val ppButton = activity.findViewById<Button>(R.id.paypal_button)

                    if (ppTitle != null) ppTitle.visibility = View.GONE
                    if (ppButton != null) ppButton.visibility = View.GONE
                } else if (billingResponseCode == BillingClient.BillingResponse.BILLING_UNAVAILABLE) {
                    val gPlayD = activity.findViewById<LinearLayout>(R.id.google_play_donate)
                    val gPlayDT = activity.findViewById<TextView>(R.id.google_play_donate_title)

                    if (gPlayD != null) {
                        gPlayD.visibility = View.GONE
                    }

                    if (gPlayDT != null) {
                        gPlayDT.visibility = View.GONE
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to the
                // In-app Billing service by calling the startConnection() method.
            }
        })
    }

    private fun consumeAsync(token: String) {
        client.consumeAsync(token) { _, _ -> }
    }

    fun onDonateClicked(skuId: String): Int {
        val builder = BillingFlowParams.newBuilder()
                .setSku(skuId).setType(BillingClient.SkuType.INAPP)
        return client.launchBillingFlow(activity, builder.build())
    }

    companion object {
        fun onDonatePayPalClicked(activity: Activity) {
            val labsInstalled = activity.packageManager.isPackageInstalled("com.xda.labs")
            val uri = Uri.parse(if (labsInstalled) "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=zachary.wander@gmail.com" else "https://forum.xda-developers.com/donatetome.php?u=7055541")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }
    }
}
