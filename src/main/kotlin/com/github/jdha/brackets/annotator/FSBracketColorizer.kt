package com.github.jdha.brackets.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

class FSBracketColorizer : Annotator {

    private val colorizer = BracketColorizer.withAdditionalBrackets(setOf("[<"), setOf(">]"))

    override fun annotate(p0: PsiElement, p1: AnnotationHolder) {
        colorizer.colorize(p0, p1)
    }
}
