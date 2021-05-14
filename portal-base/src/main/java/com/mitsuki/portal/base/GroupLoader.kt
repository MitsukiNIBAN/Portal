package com.mitsuki.portal.base

interface GroupLoader {
    fun loadInto(map: MutableMap<String, Class<ItemLoader>>)
}

