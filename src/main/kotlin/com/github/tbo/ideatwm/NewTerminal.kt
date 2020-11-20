package com.github.tbo.ideatwm


import com.intellij.icons.AllIcons
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
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class NewTerminal : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val windowManager = event.project!!.getService(WindowManager::class.java)
        val terminalView = TerminalView.getInstance(event.project!!)
        val fileEditorManager = FileEditorManagerImpl.getInstance(event.project!!) as FileEditorManagerImpl
        val terminalWidget = terminalView.terminalRunner.createTerminalWidget(fileEditorManager, null)
        val filenameLabel = JLabel(AllIcons.Debugger.Console)
        val gitLabel = JLabel()

        terminalWidget.setTerminalPanelListener(object : TerminalPanelListener {
            override fun onPanelResize(p0: Dimension?, p1: RequestOrigin?) {}

            override fun onSessionChanged(terminalSession: TerminalSession?) {
                if (terminalSession != null && !terminalSession.ttyConnector.isConnected) {
                    terminalSession.close()
                }
            }

            override fun onTitleChanged(title: String?) {
                if (title != null) {
                    val (path, branch) = title.split(",")
                    if (path.startsWith("ls ") || path.startsWith("cd ")) {
                        // Avoid unnecessary flickering
                        return
                    }
                    filenameLabel.text = path.replace("fish ", "")
                    gitLabel.text = branch
                    if (branch.isNotBlank()) {
                        gitLabel.icon = AllIcons.Vcs.Branch
                    }
                }
            }
        })
        val statusPanel = JPanel()
        statusPanel.layout = BorderLayout()
        statusPanel.border = EmptyBorder(8, 8, 8, 8)
        statusPanel.add(filenameLabel, BorderLayout.LINE_START)
        statusPanel.add(gitLabel, BorderLayout.LINE_END)

        terminalWidget.add(statusPanel, BorderLayout.PAGE_END)

        windowManager.addWindow(terminalWidget.component)
    }
}