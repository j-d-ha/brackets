package com.github.jdha.brackets.services

import com.github.jdha.brackets.settings.BracketsSettingsState
import com.github.jdha.brackets.util.getContainingFileLineCount
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * A utility class for coloring bracket elements in PSI structures based on their nesting levels.
 *
 * The `BracketColorizer` class provides functionality to annotate and highlight brackets (such as
 * `{`, `[`, `(`, `<`) in a PSI tree with distinct colors corresponding to their nesting depth. This
 * is useful for visually distinguishing matching brackets in complex syntax trees.
 *
 * The class is instantiated through factory methods and provides customization for the sets of
 * brackets it recognizes. The highlighting logic handles nested structures and applies appropriate
 * color attributes to the brackets.
 *
 * @property left the set of left brackets (e.g., `{`, `[`, `(`, `<`)
 * @property right the set of right brackets (e.g., `}`, `]`, `)`, `>`)
 */
class BracketColorizerService private constructor(val left: Set<String>, val right: Set<String>) {

    init {
        println("BracketColorizerService init")
    }

    private val settings = BracketsSettingsState.Companion.getInstance()

    companion object {
        val left = setOf("[", "{", "(", "<")
        val right = setOf("]", "}", ")", ">")

        /**
         * Creates a default `BracketColorizer` configured with common brackets.
         *
         * @return a `BracketColorizer` instance with default brackets
         */
        fun default(): BracketColorizerService = BracketColorizerService(left, right)

        /**
         * Creates a `BracketColorizer` with additional custom brackets.
         *
         * @param addLeft a set of additional left brackets to include
         * @param addRight a set of additional right brackets to include
         * @return a `BracketColorizer` instance with the extended bracket sets
         */
        fun withAdditionalBrackets(
            addLeft: Set<String>,
            addRight: Set<String>,
        ): BracketColorizerService =
            BracketColorizerService(left = left union addLeft, right = right union addRight)
    }

    private val colors =
        mapOf(
            1 to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_1"),
            2 to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_2"),
            3 to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_3"),
        )

    private val angle = setOf("<", ">")
    private val max = colors.size

    /**
     * Adjusts the bracket nesting level to ensure it stays within the valid range.
     * - If level exceeds max, it wraps around to 1.
     * - If level falls below 1, it wraps around to max.
     * - Otherwise, returns the original value.
     *
     * @param level The current level value to adjust
     * @return The adjusted level value
     */
    private fun adjustLevel(level: Int): Int =
        when {
            level > max -> 1
            level < 1 -> max
            else -> level
        }

    /**
     * Colors a given bracket element within the PSI structure.
     *
     * This function checks if the provided element is a leaf PSI element and belongs to the defined
     * set of brackets. It calculates the nesting level of the bracket and determines the
     * appropriate color to apply based on this level. The annotation is then added to the holder
     * with the specified color attributes.
     *
     * @param element the PSI element representing a bracket
     * @param holder the annotation holder to which color annotations are added
     */
    fun colorize(element: PsiElement, holder: AnnotationHolder) {
        // early return if long file disable is enabled and if long file
        if (
            settings.disableForLongFiles &&
                element.getContainingFileLineCount() > settings.longFileLineCountThreshold
        )
            return

        // Return early if the element is not a valid leaf PSI element or not a recognized bracket
        if (
            element !is LeafPsiElement ||
                element.text !in left + right ||
                filterAngleBrackets(element)
        )
            return

        // if element is a left bracket we want to set value to 3 (-1 if we did not wrap) or 1 if
        // it's a right bracket. This gives us out starting level to calculate from.
        var level = if (element.text in right) 3 else 1

        level = parsePsiElement(element, level)

        // get color as TextAttributesKey based on level. Note that due to how the setter wraps
        // values, we should ALWAYS be to get a color here.
        val color = colors[level] ?: return

        // set bracket color with TextAttributesKey
        holder
            .newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element as PsiElement)
            .textAttributes(color)
            .create()
    }

    /**
     * Filters out angled brackets used in contexts where they should not be colored.
     *
     * Currently, angled brackets used in generic type parameters are not colored.
     *
     * @param element the PsiElement to filter
     * @return whether the PsiElement should be filtered
     */
    private fun filterAngleBrackets(element: LeafPsiElement) =
        element.text in angle &&
            with(element.parent) { firstChild.text !in angle || lastChild.text !in angle }

    /**
     * A recursive function to parse the PsiElements above the given PsiElement.
     *
     * The function will count the number of left and right brackets in the PsiElements above the
     * given PsiElement. The count is used to determine the color of the given PsiElement.
     *
     * @param element the PsiElement to start parsing from
     * @param level the current bracket nesting level
     * @return the updated bracket nesting level
     */
    private tailrec fun parsePsiElement(element: PsiElement, level: Int): Int {
        // process and previous sibling if it exists
        val newLevel =
            element.prevSibling?.let { parsePsiElementSiblings(it, element, level) } ?: level

        // Recursively process the parent if it exists
        return if (element.parent !is PsiFile) parsePsiElement(element.parent, newLevel)
        else newLevel
    }

    /**
     * This function is used to parse the PsiElements to the left of `startElement` and count the
     * number of left brackets and right brackets. The count is used to determine the color of
     * `startElement`.
     *
     * @param element the PsiElement to process
     * @param startElement the PsiElement that is being colored
     * @param level the current bracket nesting level
     * @return the updated bracket nesting level
     */
    private tailrec fun parsePsiElementSiblings(
        element: PsiElement,
        startElement: PsiElement,
        level: Int,
    ): Int {
        // Process the current element if it is a valid leaf PSI element
        var newLevel = level
        (element as? LeafPsiElement)
            ?.takeIf { !filterAngleBrackets(it) }
            ?.text
            ?.let { text ->
                newLevel =
                    when (text) {
                        in left -> adjustLevel(level + 1)
                        in right -> adjustLevel(level - 1)
                        else -> level
                    }
            }

        // Recursively process the previous sibling if it exists
        val nextSibling = element.prevSibling ?: return newLevel
        return parsePsiElementSiblings(nextSibling, startElement, newLevel)
    }
}
