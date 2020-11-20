package com.github.tbo.ideatwm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import java.lang.Integer.min

class GotoNextTab : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val reorderService = event.project!!.getService(LruTabOrderService::class.java)
        reorderService.isTabSelectionActive = true

        if (event.project != null) {
            val manager = FileEditorManagerEx.getInstanceEx(event.project!!)
            val pane = manager.windows[0].tabbedPane
            val nextTab = pane.tabs.getTabAt(min(pane.selectedIndex + 1, pane.tabCount - 1))
            pane.tabs.select(nextTab, true)
        }
    }
}