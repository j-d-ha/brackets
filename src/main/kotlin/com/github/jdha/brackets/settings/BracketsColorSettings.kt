/*
 * brackets - JetBrains plugin to provide matching bracket pair colorization.
 * Copyright (C) 2024 Jonas Ha
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

package com.github.jdha.brackets.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.PlainTextSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.NlsContexts
import javax.swing.Icon
import org.jetbrains.annotations.NonNls

class BracketsColorSettings : ColorSettingsPage {
    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter =
        PlainTextSyntaxHighlighterFactory().getSyntaxHighlighter(null, null)

    override fun getDemoText(): @NonNls String =
        """<l1>(</l1><l2>(</l2><l3>(</l3><l3>)</l3><l2>)</l2><l1>)</l1>"""

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String?, TextAttributesKey?>? =
        mapOf(
            "l1" to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_1"),
            "l2" to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_2"),
            "l3" to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_3"),
        )

    override fun getAttributeDescriptors(): Array<out AttributesDescriptor?> =
        arrayOf(
            AttributesDescriptor(
                "Bracket Level 1",
                TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_1"),
            ),
            AttributesDescriptor(
                "Bracket Level 2",
                TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_2"),
            ),
            AttributesDescriptor(
                "Bracket Level 3",
                TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_3"),
            ),
        )

    override fun getColorDescriptors(): Array<out ColorDescriptor?> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): @NlsContexts.ConfigurableName String = "Brackets"
}
