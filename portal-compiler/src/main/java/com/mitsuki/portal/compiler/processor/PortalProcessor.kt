package com.mitsuki.portal.compiler.processor

import com.mitsuki.portal.base.GroupLoader
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

    private lateinit var moduleName: String

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        this.filer = processingEnvironment.filer
        this.elementUtil = processingEnvironment.elementUtils
        this.typeUtil = processingEnvironment.typeUtils
        this.messager = processingEnvironment.messager

        moduleName = processingEnvironment.options[Constants.MODULE_NAME]?.nameFilter() ?: ""
        if (moduleName.isEmpty()) throw  IllegalStateException()
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
            val groupMap: MutableMap<String, MutableSet<PortalMeta>> =
                HashMap<String, MutableSet<PortalMeta>>()

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
                        String::class.asTypeName(),
                        Class::class.asTypeName()
                            .parameterizedBy(WildcardTypeName.producerOf(ItemLoader::class.asTypeName()))
                    )
                )

            groupMap.forEach { group ->
                FunSpec.builder(Constants.METHOD_LOAD_INTO)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(
                        "map",
                        ClassName("kotlin.collections", "MutableMap").parameterizedBy(
                            String::class.asTypeName(), PortalMeta::class.asTypeName()
                        )
                    ).apply {

                        group.value.forEach { meta ->
                            addStatement(
                                "map[%S] = %T(%S, %S, %T::class.java)",
                                meta.path,
                                PortalMeta::class,
                                meta.path,
                                meta.group,
                                ClassName.bestGuess(meta.requireClassName())
                            )
                        }

                        FileSpec
                            .builder(
                                Constants.PACKAGE_OF_GENERATE_FILE,
                                Constants.GROUP + group.key.nameFilter()
                            )
                            .addType(
                                TypeSpec.classBuilder(
                                    Constants.GROUP + group.key.nameFilter()
                                )
                                    .addSuperinterface(ItemLoader::class.asClassName())
                                    .addFunction(build())
                                    .build()
                            ).build().writeTo(filer)
                    }


                rootLoadFuncBuilder.addStatement(
                    "map[%S] = %T::class.java",
                    group.key,
                    ClassName(
                        Constants.PACKAGE_OF_GENERATE_FILE,
                        Constants.GROUP + group.key.nameFilter()
                    )
                )
            }

            FileSpec.builder(Constants.PACKAGE_OF_GENERATE_FILE, Constants.ROOT + moduleName)
                .addType(
                    TypeSpec.classBuilder(Constants.ROOT + moduleName)
                        .addSuperinterface(GroupLoader::class.asClassName())
                        .addFunction(rootLoadFuncBuilder.build())
                        .build()
                ).build().writeTo(filer)
        }

    }

    private fun String.nameFilter(): String {
        return replace(Regex("[^0-9a-zA-Z_]+"), "")
    }
}