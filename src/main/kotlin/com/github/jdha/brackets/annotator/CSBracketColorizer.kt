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

package com.github.jdha.brackets.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

class CSBracketColorizer : Annotator {

    private val colorizer = BracketColorizer.default()

    /**
     * This function is called by the plugin to annotate a given element of the PSI structure.
     *
     * @param p0 the element to annotate
     * @param p1 the annotation holder to which color annotations are added
     */
    override fun annotate(p0: PsiElement, p1: AnnotationHolder) {
        colorizer.colorize(p0, p1)
    }
}
