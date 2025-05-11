package com.github.jdha.brackets.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*

class BracketsSettingsConfigurable : BoundConfigurable("Brackets") {
    // Get a reference to the current settings
    private val currentSettings = BracketsSettingsState.getInstance()

    override fun createPanel(): DialogPanel = panel {
        row {
            checkBox("Disable bracket pair colorization for files with more than")
                .bindSelected(currentSettings::disableForLongFiles)
                .gap(RightGap.SMALL)

            intTextField(range = IntRange(1, Int.MAX_VALUE), keyboardStep = 5)
                .bindIntText(currentSettings::longFileLineCountThreshold)
                .gap(RightGap.SMALL)

            label("lines")
        }
    }
}
