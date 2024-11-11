/*
 * brackets - JetBrains plugin to provide matching bracket pair colorization.
 * Copyright (C) 2017-2024 Zhihao Zhang and the intellij-rainbow-brackets contributors
 * Copyright (C) 2024 Jonas Ha
 *
 * The following code is a derivative work of the code from the lite open source
 * version of the intellij-rainbow-brackets project, which is licensed under the
 * GPL-3.0 license. This code therefore is also licensed under the terms of the
 * GPL-3.0 license. The repository for th intellij-rainbow-brackets project can be
 * found here: https://github.com/izhangzhihao/intellij-rainbow-brackets.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.github.jdha.brackets.annotator

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
class BracketColorizer private constructor(val left: Set<String>, val right: Set<String>) {

    companion object {
        val left = setOf("[", "{", "(", "<")
        val right = setOf("]", "}", ")", ">")

        /**
         * Creates a default `BracketColorizer` configured with common brackets.
         *
         * @return a `BracketColorizer` instance with default brackets
         */
        fun default(): BracketColorizer = BracketColorizer(left, right)

        /**
         * Creates a `BracketColorizer` with additional custom brackets.
         *
         * @param addLeft a set of additional left brackets to include
         * @param addRight a set of additional right brackets to include
         * @return a `BracketColorizer` instance with the extended bracket sets
         */
        fun withAdditionalBrackets(addLeft: Set<String>, addRight: Set<String>): BracketColorizer =
            BracketColorizer(left = left union addLeft, right = right union addRight)

        /**
         * Creates a `BracketColorizer` instance with custom bracket sets.
         *
         * @param left a set of left brackets to use for highlighting
         * @param right a set of right brackets to use for highlighting
         * @return a `BracketColorizer` instance with custom brackets
         */
        fun withCustomBrackets(left: Set<String>, right: Set<String>): BracketColorizer =
            BracketColorizer(left, right)
    }

    private val colors =
        mapOf<Int, TextAttributesKey>(
            1 to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_1"),
            2 to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_2"),
            3 to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_3"),
        )

    private val angle = setOf("<", ">")
    private val max = colors.size

    /**
     * The current bracket nesting level, used to determine the appropriate color for highlighting.
     * - The `level` increases when a left bracket (from the `left` set) is encountered.
     * - The `level` decreases when a right bracket (from the `right` set) is encountered.
     * - The value of `level` is kept within the range `[1, max]`, where `max` is the total number
     *   of available colors.
     * - If `level` exceeds `max`, it wraps around to `1`.
     * - If `level` falls below `1`, it wraps around to `max`.
     *
     * This ensures that the color-coding cycles through the available colors for deeply nested or
     * unbalanced brackets.
     */
    private var level = 1
        set(value) {
            field =
                when {
                    value > max -> 1
                    value < 1 -> max
                    else -> value
                }
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
        // Return early if the element is not a valid leaf PSI element or not a recognized bracket
        if (
            element !is LeafPsiElement ||
                element.text !in left + right ||
                filterAngleBrackets(element)
        )
            return

        // if element is a left bracket we want to set value to 3 (-1 if we did not wrap) or 1 if
        // it's a right bracket. This gives us out starting level to calculate from.
        level = if (element.text in right) 3 else 1

        parsePsiElement(element)

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
     */
    private tailrec fun parsePsiElement(element: PsiElement) {
        // process and previous sibling if it exists
        element.prevSibling?.let { parsePsiElementSiblings(it, element) }

        // Recursively process the parent if it exists
        if (element.parent !is PsiFile) parsePsiElement(element.parent)
    }

    /**
     * This function is used to parse the PsiElements to the left of `startElement` and count the
     * number of left brackets and right brackets. The count is used to determine the color of
     * `startElement`.
     *
     * @param element the PsiElement to process
     * @param startElement the PsiElement that is being colored
     */
    private tailrec fun parsePsiElementSiblings(element: PsiElement, startElement: PsiElement) {
        // Process the current element if it is a valid leaf PSI element
        (element as? LeafPsiElement)
            ?.takeIf { !filterAngleBrackets(it) }
            ?.text
            ?.let { text ->
                when (text) {
                    in left -> level++
                    in right -> level--
                }
            }

        // Recursively process the previous sibling if it exists
        val nextSibling = element.prevSibling ?: return
        parsePsiElementSiblings(nextSibling, startElement)
    }
}
