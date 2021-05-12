package com.mitsuki.portal.compiler.processor

import com.mitsuki.portal.base.PortalMeta
import com.mitsuki.portal.base.annotation.Portal
import com.mitsuki.portal.compiler.Constants
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class PortalProcessor : AbstractProcessor() {

    private lateinit var filer: Filer
    private lateinit var elementUtil: Elements
    private lateinit var typeUtil: Types
    private lateinit var messager: Messager

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        this.filer = processingEnvironment.filer
        this.elementUtil = processingEnvironment.elementUtils
        this.typeUtil = processingEnvironment.typeUtils
        this.messager = processingEnv.messager
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(Portal::class.java.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion =
        SourceVersion.latestSupported()

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        if (annotations.isNullOrEmpty()) return false
        try {
            parse(roundEnv.getElementsAnnotatedWith(Portal::class.java))
        } catch (inner: Exception) {

        }
        return true
    }


    private fun parse(routeElements: Set<Element>) {
        if (routeElements.isNotEmpty()) {
            val typeLoader: TypeElement = elementUtil.getTypeElement(Constants.LOADER)
            val portalMeta: ClassName = PortalMeta::class.java.asClassName()

            routeElements.forEach {

            }
        }

    }

}