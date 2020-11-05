package com.github.tbo.ideatwm


import com.intellij.ide.DataManager
import com.intellij.ide.actions.ShowContentAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.terminal.JBTerminalWidget
import com.intellij.terminal.JBTerminalWidgetListener
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.content.Content
import com.intellij.ui.tabs.TabInfo
import com.intellij.util.containers.ContainerUtil
import com.jediterm.terminal.RequestOrigin
import com.jediterm.terminal.ui.TerminalPanelListener
import com.jediterm.terminal.ui.TerminalSession
import org.jetbrains.plugins.terminal.TerminalView
import org.jetbrains.plugins.terminal.vfs.TerminalSessionVirtualFileImpl
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.KeyEvent
import java.time.Instant
import javax.swing.JLabel

class NewTerminal : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
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
                val title = p0 ?: "empty"
                val notification = Notification("group", title, title, NotificationType.INFORMATION)
                Notifications.Bus.notify(notification)
            }
        })

        val masterWindow = fileEditorManager.windows[0]

        val splitter: Splitter = OnePixelSplitter(false, 0.5f, 0.1f, 0.9f)
        val panel = masterWindow.tabbedPane.component.parent
        panel.remove(masterWindow.tabbedPane.component)
        panel.add(splitter)

        splitter.firstComponent = terminalWidget.component
        splitter.secondComponent = masterWindow.tabbedPane.component
        terminalWidget.requestFocus()

//        val fileEditorManagerEx = FileEditorManagerImpl(event.project!!)
//        val settingsProvider = terminalView.terminalRunner.settingsProvider
//        val terminalWidget = JBTerminalWidget(event.project!!, settingsProvider, fileEditorManager)
//        val file = TerminalSessionVirtualFileImpl(tabInfo, terminalWidget, settingsProvider)
//        val name = masterWindow.tabbedPane.component.parent.javaClass.name
//        val terminal = fileEditorManager.openFile(file, true).first()
//        masterWindow.split(1, true, file, true)
//        val terminalWindow = EditorWindow(masterWindow.tabbedPane, splitter)
//        val fileEditorManager = FileEditorManager.getInstance(event.project!!) as FileEditorManagerImpl
//        masterWindow.split(0, false, file, true)
//        val terminal = fileEditorManager.splitters.getOrCreateCurrentWindow(file)
//        val terminalWindow = fileEditorManager.splitters.create
//        val terminal = fileEditorManager.openFileInNewWindow(file).first[0]
//        val tabbedTerminal = EditorTabbedContainer(terminal, event.project!!, terminal)
//        EditorsSplitters(fileEditorManager as @org.jetbrains.annotations.NotNull FileEditorManagerImpl, true, terminal)
//        val terminalWindow = EditorWindow(
//                masterWindow.owner,
//                terminal
//        )
//        splitter.firstComponent.requestFocus()
//        splitter.firstComponent = terminal.tabbedPane.component
//        val component = JPanel(BorderLayout())
//        var notification = Notification("group", "no", "no", NotificationType.INFORMATION)
//        Notifications.Bus.notify(notification)
//        component.add(terminal.component)
    }
}