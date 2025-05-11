package com.github.jdha.brackets.util

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiElement

/** Gets the line count for a given PSI file */
fun PsiElement.getContainingFileLineCount(): Int =
    containingFile?.virtualFile?.let {
        FileDocumentManager.getInstance().getDocument(it)?.lineCount
    } ?: 0
