package io.github.arrow.gen.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*
import java.util.Locale.getDefault

class ArrowGenProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {
    private val include = options["arrowGen.include"]?.split(",") ?: emptyList()
    private val exclude = options["arrowGen.exclude"]?.split(",") ?: emptyList()
    private val generateRaise = options["arrowGen.raise"]?.toBoolean() ?: false
    private val generateEither = options["arrowGen.either"]?.toBoolean() ?: false
    private val generateEffect = options["arrowGen.effect"]?.toBoolean() ?: false
    private val generatedFilesPaths = mutableSetOf<String>()

    enum class ApiKind {
        RAISE,
        EITHER,
        EFFECT,
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("Already generated files: $generatedFilesPaths")

        if (!generateRaise && !generateEither) {
            logger.warn("Neither 'raise' nor 'either' generation is enabled. No code will be generated.")
            return emptyList()
        }

        logger.info("Searching for functions $include")

        val memberFunctionsExtensions =
            include
                .mapNotNull { functionQualifiedName ->
                    val split = functionQualifiedName.split(".")
                    val classDeclaration =
                        resolver.getClassDeclarationByName(
                            split.dropLast(1).joinToString("."),
                        )
                    val function =
                        classDeclaration
                            ?.declarations
                            ?.filterIsInstance<KSFunctionDeclaration>()
                            ?.find { it.simpleName.asString() == split.last() }

                    function?.let { member ->
                        classDeclaration to member
                    }
                }.also { logger.info("Found ${it.size} member functions to process") }

        val memberFunctionsExtensionsFileSpecs =
            memberFunctionsExtensions
                .map { (classDeclaration, functionDeclaration) ->
                    arrowExtensionFileSpec(classDeclaration, functionDeclaration)
                }

        val topLevelFunctionsExtensionsFileSpecs =
            include
                .filterNot { functionQualifiedName ->
                    memberFunctionsExtensions.any { it.second.qualifiedName?.asString() == functionQualifiedName }
                }.flatMap { functionQualifiedName ->
                    resolver.getFunctionDeclarationsByName(functionQualifiedName, true)
                }.also { logger.info("Found ${it.size} top level functions to process") }
                .map { functionDeclaration ->
                    arrowExtensionFileSpec(null, functionDeclaration)
                }

        (memberFunctionsExtensionsFileSpecs + topLevelFunctionsExtensionsFileSpecs)
            .forEach {
                if (!generatedFilesPaths.any { generatedFilePath -> it.relativePath == generatedFilePath }) {
                    logger.info("Writing file ${it.relativePath}")

                    it.writeTo(codeGenerator, false)
                    generatedFilesPaths.add(it.relativePath)
                }
            }

        return emptyList()
    }

    private fun arrowExtensionFileSpec(
        classDeclaration: KSClassDeclaration?,
        function: KSFunctionDeclaration,
    ): FileSpec {
        val className = classDeclaration?.simpleName?.asString()
        val packageName = function.packageName.asString()
        val functionName = function.simpleName.asString()
        val capitalizedFunctionName =
            functionName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
        return FileSpec
            .builder(
                packageName = "$packageName.arrow",
                fileName = "${className ?: ""}${capitalizedFunctionName}Extensions",
            ).apply {
                addImport("arrow.core", "Either")
                addImport("arrow.core", "raise.Raise")
                addImport("arrow.core", "raise.Effect")
                addImport("arrow.core", "raise.catch")
                addImport("arrow.core", "raise.effect")
                addImport("arrow.core", "raise.either")

                if (classDeclaration == null) {
                    addImport(packageName, functionName)
                } else {
                    addImport(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString())
                }

                if (generateRaise) {
                    addFunction(arrowExtensionFunSpec(ApiKind.RAISE, classDeclaration, function))
                }

                if (generateEither) {
                    addFunction(arrowExtensionFunSpec(ApiKind.EITHER, classDeclaration, function))
                }

                if (generateEffect) {
                    addFunction(arrowExtensionFunSpec(ApiKind.EFFECT, classDeclaration, function))
                }
            }.build()
    }

    private fun arrowExtensionFunSpec(
        apiKind: ApiKind,
        classDeclaration: KSClassDeclaration?,
        functionDeclaration: KSFunctionDeclaration,
    ): FunSpec {
        logger.info("Generating extension function ${functionDeclaration.qualifiedName?.asString()}")

        val classTypeParametersResolver = classDeclaration?.typeParameters?.toTypeParameterResolver()
        val returnType =
            functionDeclaration.returnType
                ?: throw IllegalArgumentException("${functionDeclaration.qualifiedName?.asString()} does not have a return type")
        val functionName = functionDeclaration.simpleName.asString()
        val parameters = functionDeclaration.parameters
        val typeParameters = functionDeclaration.typeParameters
        val typeParameterResolver = typeParameters.toTypeParameterResolver(classTypeParametersResolver)

        return FunSpec
            .builder(
                when (apiKind) {
                    ApiKind.RAISE -> "${functionName}OrRaise"
                    ApiKind.EITHER -> "${functionName}Either"
                    ApiKind.EFFECT -> "${functionName}Effect"
                },
            ).returns(
                when (apiKind) {
                    ApiKind.RAISE ->
                        LambdaTypeName.get(
                            receiver =
                                ClassName
                                    .bestGuess("arrow.core.raise.Raise")
                                    .parameterizedBy(ClassName("kotlin", "Throwable")),
                            returnType = returnType.toTypeName(typeParameterResolver),
                        )

                    ApiKind.EITHER ->
                        ClassName
                            .bestGuess("arrow.core.Either")
                            .parameterizedBy(Throwable::class.asTypeName(), returnType.toTypeName(typeParameterResolver))

                    ApiKind.EFFECT ->
                        ClassName
                            .bestGuess("arrow.core.raise.Effect")
                            .parameterizedBy(Throwable::class.asTypeName(), returnType.toTypeName(typeParameterResolver))
                },
            ).apply {
                classDeclaration?.let {
                    val classTypeParametersUsedInValueParameters =
                        classDeclaration.typeParameters.filter { typeParameter ->
                            parameters.any {
                                it.type.toTypeName(typeParameterResolver) ==
                                    typeParameter.toTypeVariableName(
                                        typeParameterResolver,
                                    )
                            }
                        }

                    classTypeParametersUsedInValueParameters.forEach { typeParameter ->
                        addTypeVariable(
                            TypeVariableName(
                                typeParameter.name.asString(),
                                typeParameter.bounds
                                    .map { bound ->
                                        bound.toTypeName(typeParameterResolver)
                                    }.toList(),
                            ),
                        )
                    }

                    val typeArguments =
                        classDeclaration.typeParameters.map {
                            typeParameterResolver[it.name.asString()]
                        }

                    receiver(
                        classDeclaration.let {
                            if (classTypeParametersUsedInValueParameters.isNotEmpty()) {
                                it.toClassName().parameterizedBy(typeArguments)
                            } else {
                                it.asStarProjectedType().toTypeName(typeParameterResolver)
                            }
                        },
                    )
                }

                typeParameters.forEach { param ->
                    addTypeVariable(param.toTypeVariableName(typeParameterResolver))
                }

                parameters.forEach { param ->
                    val paramName = param.name?.asString() ?: return@forEach
                    val paramType = param.type.toTypeName(typeParameterResolver)
                    addParameter(paramName, paramType)
                }

                addCode(
                    buildCodeBlock {
                        when (apiKind) {
                            ApiKind.RAISE -> {
                                add("return ")
                                add("{\n")
                            }

                            ApiKind.EITHER -> add("return either {\n")
                            ApiKind.EFFECT -> add("return effect {\n")
                        }

                        indent()

                        arrowCatch(functionName, parameters)

                        unindent()

                        add("\n}")
                    },
                )
            }.build()
    }

    private fun CodeBlock.Builder.arrowCatch(
        functionName: String,
        parameters: List<KSValueParameter>,
    ) {
        add("catch(\n")
        indent()

        add("block = { %N", functionName)

        add("(")
        parameters.forEachIndexed { index, parameter ->
            if (index > 0) add(", ")
            add("%N", parameter.name?.asString())
        }
        add(") },\n")
        add("catch = { e: Throwable -> raise(e) }\n")
        unindent()
        add(")")
    }
}

class ArrowGenProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        ArrowGenProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            options = environment.options,
        )
}
