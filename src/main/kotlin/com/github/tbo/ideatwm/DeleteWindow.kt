package com.github.tbo.ideatwm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class DeleteWindow : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        if (event.project != null) {
            val windowManager = event.project!!.getService(WindowManager::class.java)
            windowManager.deleteWindow()
        }
    }
}