package com.mitsuki.portal.base

import com.mitsuki.portal.base.annotation.Portal
import javax.lang.model.element.Element

data class PortalMeta(
    val path: String,
    val group: String,
    val className: String? = null,
    val destination: Class<*>? = null
) {
    constructor(portal: Portal, name: String) : this(portal.path, portal.group, name)

    constructor(path: String, group: String, destination: Class<*>) : this(
        path,
        group,
        null,
        destination
    )

    val complex get() = "$group/$path"

    fun requireDestination(): Class<*> {
        if (destination == null) throw IllegalStateException("PortalMeta $this: destination is null!")
        return destination
    }

    fun requireClassName(): String {
        if (className == null) throw IllegalStateException("PortalMeta $this: className is null!")
        return className
    }

}