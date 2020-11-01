package com.github.tbo.ideatwm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx

class GotoPreviousTab : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val reorderService = event.project!!.getService(LruTabOrderService::class.java)
        reorderService.isTabSelectionActive = true

        if (event.project != null) {
            val manager = FileEditorManagerEx.getInstanceEx(event.project!!);
            val pane = manager.currentWindow.tabbedPane;
            val previousTab = pane.tabs.getTabAt(Integer.max(pane.selectedIndex - 1, 0));
            pane.tabs.select(previousTab, true);
        }
    }
}