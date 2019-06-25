package com.example.speedywork.utils

import android.app.Activity
import com.example.speedywork.R

object Animation {

    fun FadeAnimation(activity: Activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}