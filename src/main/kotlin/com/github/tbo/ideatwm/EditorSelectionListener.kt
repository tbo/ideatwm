package com.github.tbo.ideatwm

import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.ui.Messages

class EditorSelectionListener : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) {
        event.manager.project.getService(LruTabOrderService::class.java).reorderTabs()
    }
}