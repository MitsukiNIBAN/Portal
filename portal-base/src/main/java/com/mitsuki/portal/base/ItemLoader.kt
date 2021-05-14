package com.mitsuki.portal.base

interface ItemLoader {
    fun loadInto(map: MutableMap<String, PortalMeta>)
}

