package com.github.tbo.ideatwm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class FocusWindow : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        if (event.project != null) {
            val windowManager = event.project!!.getService(WindowManager::class.java)
            windowManager.focusCurrentWindow()
        }
    }
}