package com.mitsuki.portal.compiler.processor

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.tools.Diagnostic

class PortalProcessor : BaseProcessor() {

    private lateinit var messager: Messager

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        this.messager = processingEnv.messager
    }


    override fun parse(roundEnv: RoundEnvironment?) {
        this.messager.printMessage(
            Diagnostic.Kind.WARNING,
            "=============> PortalProcessor is running"
        )
    }
}