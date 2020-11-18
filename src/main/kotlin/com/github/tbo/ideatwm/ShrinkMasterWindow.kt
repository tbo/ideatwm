package com.github.tbo.ideatwm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ShrinkMasterWindow : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val windowManager = event.project!!.getService(WindowManager::class.java)
        windowManager.shrinkMasterWindow()
    }
}