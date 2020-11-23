package com.github.tbo.ideatwm

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import git4idea.branch.GitBranchUtil
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class EditorSelectionListener : FileEditorManagerListener {

    private val focusColor = Color(25, 40, 84)
    private val defaultColor = Color(11, 13, 15)

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val windowManager = source.project.getService(WindowManager::class.java)
        windowManager.focusEditorWindow()
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        event.manager.project.getService(LruTabOrderService::class.java).reorderTabs()
        val statusPanel = JPanel()
        statusPanel.layout = BorderLayout()
        statusPanel.background = focusColor
        statusPanel.border = EmptyBorder(8, 8, 8, 8)
        event.newEditor?.component?.add(statusPanel, BorderLayout.PAGE_END)
        event.newEditor?.preferredFocusedComponent?.addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent?) {
                statusPanel.background = focusColor
            }

            override fun focusLost(e: FocusEvent?) {
                statusPanel.background = defaultColor
            }
        })
        val repository = GitBranchUtil.getCurrentRepository(event.manager.project)
        val filenameLabel = JLabel(AllIcons.Debugger.Console)
        filenameLabel.text = repository?.root?.canonicalPath.let { event.newFile?.canonicalPath?.replace("$it/", "") }
        filenameLabel.icon = event.newFile?.fileType?.icon
        statusPanel.add(filenameLabel, BorderLayout.LINE_START)
        val gitLabel = JLabel()
        val branch = GitBranchUtil.getCurrentRepository(event.manager.project)?.currentBranch?.name
        gitLabel.text = branch
        if (branch != null) {
            gitLabel.icon = AllIcons.Vcs.Branch
        }

        statusPanel.add(gitLabel, BorderLayout.LINE_END)
    }
}