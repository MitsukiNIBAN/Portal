package com.mitsuki.portal.base

interface Loader {
    fun loadInto(map: MutableMap<String, PortalMeta>)
}

