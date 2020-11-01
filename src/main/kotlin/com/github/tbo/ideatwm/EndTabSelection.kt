package com.github.tbo.ideatwm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class EndTabSelection : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.project!!.getService(LruTabOrderService::class.java).isTabSelectionActive = false
    }
}