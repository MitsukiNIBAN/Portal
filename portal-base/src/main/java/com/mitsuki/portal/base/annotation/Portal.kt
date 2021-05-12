package com.mitsuki.portal.base.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Portal(
    val path: String,
    val group: String
)