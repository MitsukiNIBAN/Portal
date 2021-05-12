package com.mitsuki.portal.compiler.processor

import com.mitsuki.portal.base.annotation.Portal
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class BaseProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var elementUtil: Elements
    lateinit var typeUtil: Types

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)

        this.filer = processingEnvironment.filer
        this.elementUtil = processingEnvironment.elementUtils
        this.typeUtil = processingEnvironment.typeUtils
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(Portal::class.java.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion =
        SourceVersion.latestSupported()

    override fun process(
        annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?
    ): Boolean {
        if (annotations.isNullOrEmpty()) return false
        try {
            parse(roundEnv)
        } catch (inner: Exception) {

        }
        return true
    }

    abstract fun parse(roundEnv: RoundEnvironment?)
}