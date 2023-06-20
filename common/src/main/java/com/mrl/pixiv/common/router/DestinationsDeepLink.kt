package com.mrl.pixiv.common.router

import android.net.Uri
import androidx.core.net.toUri

object DestinationsDeepLink {

    private val BaseUri = "pipixiv://com.mrl.pixiv".toUri()

    val HomePattern = "$BaseUri/${Destination.HomeScreen.route}"
    val ProfilePattern = "$BaseUri/${Destination.ProfileScreen.route}"

    fun getProfileUri(): Uri =
        ProfilePattern.toUri()


    fun getHomeUri(): Uri =
        HomePattern.toUri()
}