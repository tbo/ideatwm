package com.github.tbo.ideatwm

import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project

class LruTabOrderService(project: Project) {

    private val fileEditorManagerEx = FileEditorManagerEx.getInstanceEx(project);

    var isTabSelectionActive = false
        set(value) {
            field = value
            if (!value) {
                reorderTabs()
            }
        }

    fun reorderTabs() {
        if (!isTabSelectionActive) {
            val pane = fileEditorManagerEx.currentWindow.tabbedPane;
            val position = pane.selectedIndex;
            val currentTab = pane.tabs.getTabAt(position);
            pane.tabs.removeTab(currentTab);
            pane.tabs.addTab(currentTab, 0);
            pane.tabs.select(currentTab, true);
        }
    }
}
