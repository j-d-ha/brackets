package com.github.jdha.brackets.services

import com.intellij.openapi.components.Service
import java.util.concurrent.ConcurrentHashMap

enum class BracketColorizerType {
    CSharp,
    FSharp,
}

@Service
class BracketColorizerServiceFactory {

    // Use ConcurrentHashMap for thread-safe access without synchronization overhead
    private val services = ConcurrentHashMap<BracketColorizerType, BracketColorizerService>()

    // Thread-safe, atomic get-or-create operation
    fun get(type: BracketColorizerType): BracketColorizerService =
        services.computeIfAbsent(type) { createColorizer(it) }

    // Separate service creation logic for better readability and performance
    private fun createColorizer(type: BracketColorizerType): BracketColorizerService =
        when (type) {
            BracketColorizerType.CSharp -> BracketColorizerService.default()
            BracketColorizerType.FSharp ->
                BracketColorizerService.withAdditionalBrackets(setOf("[<"), setOf(">]"))
        }
}
