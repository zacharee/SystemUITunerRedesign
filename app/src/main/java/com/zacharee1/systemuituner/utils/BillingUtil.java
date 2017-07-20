package com.zacharee1.systemuituner.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.zacharee1.systemuituner.R;

import java.util.ArrayList;
import java.util.List;

import static com.zacharee1.systemuituner.utils.Utils.isPackageInstalled;


public class BillingUtil
{
    private Activity mActivity;
    private BillingClient mBillingClient;
//    private final List<String> mSkuList;

    public BillingUtil(Activity activity) {
        mActivity = activity;

        mBillingClient = new BillingClient.Builder(mActivity).setListener(new PurchasesUpdatedListener()
        {
            @Override
            public void onPurchasesUpdated(int responseCode, List<Purchase> purchases)
            {
                if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        mBillingClient.consumeAsync(purchase.getPurchaseToken(), null);
                    }
                }
            }
        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                Log.e("BillingResult", billingResponseCode + "");

                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    //        mSkuList = new ArrayList<>();
                    //        mSkuList.add("donate_1");
                    //        mSkuList.add("donate_2");
                    //        mSkuList.add("donate_5");
                    //        mSkuList.add("donate_10");
                    //        mBillingClient.querySkuDetailsAsync(BillingClient.SkuType.INAPP , mSkuList,
                    //                new SkuDetailsResponseListener() {
                    //                    @Override
                    //                    public void onSkuDetailsResponse(SkuDetails.SkuDetailsResult result) {
                    //                        // Process the result.
                    //                    }
                    //                });
                } else if (billingResponseCode == BillingClient.BillingResponse.BILLING_UNAVAILABLE) {
                    LinearLayout gPlayD = mActivity.findViewById(R.id.google_play_donate);
                    TextView gPlayDT = mActivity.findViewById(R.id.google_play_donate_title);

                    if (gPlayD != null) {
                        gPlayD.setVisibility(View.GONE);
                    }

                    if (gPlayDT != null) {
                        gPlayDT.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to the
                // In-app Billing service by calling the startConnection() method.
            }
        });
    }

    public int onDonateClicked(String skuId) {
        BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                .setSku(skuId).setType(BillingClient.SkuType.INAPP);
        return mBillingClient.launchBillingFlow(mActivity, builder.build());
    }

    public static void onDonatePayPalClicked(Activity activity) {
        boolean labsInstalled = isPackageInstalled("com.xda.labs", activity.getPackageManager());
        Uri uri = Uri.parse(labsInstalled ? "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=andywander@yahoo.com" : "https://forum.xda-developers.com/donatetome.php?u=7055541");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
