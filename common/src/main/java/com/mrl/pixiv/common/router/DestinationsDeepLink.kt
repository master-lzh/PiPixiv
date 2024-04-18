package com.mrl.pixiv.common.router

import android.net.Uri
import androidx.core.net.toUri

object DestinationsDeepLink {

    private val BaseUri = "pipixiv://com.mrl.pixiv".toUri()

    val HomePattern = "$BaseUri/${Destination.HomeScreen.route}"
    val ProfileDetailPattern = "$BaseUri/${Destination.ProfileDetailScreen.route}"
    val PicturePattern = "$BaseUri/${Destination.PictureScreen.route}"

    fun getProfileUri(): Uri =
        ProfileDetailPattern.toUri()


    fun getHomeUri(): Uri =
        HomePattern.toUri()
}