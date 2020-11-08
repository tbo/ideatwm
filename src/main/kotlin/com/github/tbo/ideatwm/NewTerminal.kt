package com.github.tbo.ideatwm


import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.jediterm.terminal.RequestOrigin
import com.jediterm.terminal.ui.TerminalPanelListener
import com.jediterm.terminal.ui.TerminalSession
import org.jetbrains.plugins.terminal.TerminalView
import java.awt.Dimension

class NewTerminal : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val windowManager = event.project!!.getService(WindowManager::class.java)
        val terminalView = TerminalView.getInstance(event.project!!)
        val fileEditorManager = FileEditorManagerImpl.getInstance(event.project!!) as FileEditorManagerImpl
        val terminalWidget = terminalView.terminalRunner.createTerminalWidget(fileEditorManager, null)
        terminalWidget.setTerminalPanelListener(object : TerminalPanelListener {
            override fun onPanelResize(p0: Dimension?, p1: RequestOrigin?) {
//                TODO("Not yet implemented")
            }

            override fun onSessionChanged(terminalSession: TerminalSession?) {
                val notification = Notification("group", "Session changed", terminalSession.toString(), NotificationType.INFORMATION)
                Notifications.Bus.notify(notification)
                if (terminalSession != null && !terminalSession.ttyConnector.isConnected) {
                    terminalSession.close()
                }
            }

            override fun onTitleChanged(p0: String?) {
//                val title = p0 ?: "empty"
//                val notification = Notification("group", title, title, NotificationType.INFORMATION)
//                Notifications.Bus.notify(notification)
            }
        })

        windowManager.addWindow(terminalWidget.component)
    }
}