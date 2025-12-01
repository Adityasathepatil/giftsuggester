package com.techtool.giftsuggester.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAdView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(adUnitId)
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            // Reload ad if needed
            adView.loadAd(AdRequest.Builder().build())
        }
    )
}

object AdUnitIds {
    // Test Banner Ad Unit ID - Replace with your actual Ad Unit IDs for production
    const val BANNER_INFO_SCREEN = "ca-app-pub-5829896408548217/7733201115" // Test ID
    const val BANNER_SCAN_SCREEN = "ca-app-pub-5829896408548217/2480874436" // Test ID

    // For production, use your actual Ad Unit IDs:
    // const val BANNER_INFO_SCREEN = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
    // const val BANNER_SCAN_SCREEN = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
}
