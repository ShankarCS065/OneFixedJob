// OpenUrlInBrowserIntent.kt
package com.devlopershankar.onefixedjob.ui.util

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun OpenUrlInBrowserIntent(url: String) {
    val context = LocalContext.current
    CustomTabsIntent.Builder().apply {
        setToolbarColor(ComposeColor.White.toArgb()) // Customize toolbar color
        setShowTitle(true)
        enableUrlBarHiding()
        // Optional: Add animations
        // setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
        // setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right)
    }.build().launchUrl(context, Uri.parse(url))
}
