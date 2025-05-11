package com.github.jdha.brackets.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean

@State(name = "BracketsSettings", storages = [Storage("brackets.xml")])
class BracketsSettingsState : PersistentStateComponent<BracketsSettingsState> {

    var disableForLongFiles: Boolean = false
    var longFileLineCountThreshold: Int = 1000

    override fun getState(): BracketsSettingsState? = this

    override fun loadState(state: BracketsSettingsState) = copyBean(state, this)

    companion object {
        fun getInstance(): BracketsSettingsState =
            ApplicationManager.getApplication().getService(BracketsSettingsState::class.java)
    }
}
