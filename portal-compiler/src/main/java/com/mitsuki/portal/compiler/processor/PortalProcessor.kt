package com.mitsuki.portal.compiler.processor

import com.mitsuki.portal.base.ItemLoader
import com.mitsuki.portal.base.PortalMeta
import com.mitsuki.portal.base.annotation.Portal
import com.mitsuki.portal.compiler.Constants
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

class PortalProcessor : AbstractProcessor() {

    private lateinit var filer: Filer
    private lateinit var elementUtil: Elements
    private lateinit var typeUtil: Types
    private lateinit var messager: Messager

//    private val groupMap: MutableMap<String, MutableSet<PortalMeta>> = HashMap<String, MutableSet<PortalMeta>>()

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
//            val portalMeta: ClassName = PortalMeta::class.java.asClassName()
            val groupMap: MutableMap<String, MutableSet<PortalMeta>> =
                HashMap<String, MutableSet<PortalMeta>>()

            //遍历注解节点

            for (element in routeElements) {
                val route: Portal = element.getAnnotation(Portal::class.java)
                val meta = PortalMeta(route, (element as TypeElement).qualifiedName.toString())
                if (meta.path.isEmpty()) continue
                groupMap[meta.group]?.apply { add(meta) } ?: {
                    groupMap[meta.group] = TreeSet(Comparator<PortalMeta> { p0, p1 ->
                        try {
                            p0.complex.compareTo(p1.complex)
                        } catch (inner: Exception) {
                            0
                        }
                    }).apply { add(meta) }
                }()
            }

            val rootLoadFuncBuilder = FunSpec.builder(Constants.METHOD_LOAD_INTO)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(
                    "map",
                    ClassName("kotlin.collections", "MutableMap").parameterizedBy(
                        String::class.asTypeName(), ItemLoader::class.asTypeName()
                    )
                )

            val groupLoadFuncBuilder = FunSpec.builder(Constants.METHOD_LOAD_INTO)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(
                    "map",
                    ClassName("kotlin.collections", "MutableMap").parameterizedBy(
                        String::class.asTypeName(), PortalMeta::class.asTypeName()
                    )
                )


            groupMap.forEach { group ->
                group.value.forEach { meta ->
                    groupLoadFuncBuilder.addStatement(
                        "map[%S] = %T(%S, %S, %T::class.java)",
                        meta.path,
                        PortalMeta::class,
                        meta.path,
                        meta.group,
                        ClassName.bestGuess(meta.requireClassName())
                    )
                }


                rootLoadFuncBuilder.addStatement(
                    "map[%S] = %T"
                )



            }


            FileSpec.builder(Constants.PACKAGE_OF_GENERATE_FILE, "Main")
                .addType(
                    TypeSpec.classBuilder("Main")
                        .addSuperinterface(ItemLoader::class.asClassName())
                        .addFunction(groupLoadFuncBuilder.build())
                        .build()
                ).build().writeTo(filer)

//            this.messager.printMessage(
//                Diagnostic.Kind.WARNING,
//                "=============> RouterProcessor"
//            )
        }

    }
}