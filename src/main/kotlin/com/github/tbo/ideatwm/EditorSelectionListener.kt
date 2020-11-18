package com.github.tbo.ideatwm

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import git4idea.branch.GitBranchUtil
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class EditorSelectionListener : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) {
        event.manager.project.getService(LruTabOrderService::class.java).reorderTabs()
        val statusPanel = JPanel()
        statusPanel.layout = BorderLayout()
        statusPanel.border = EmptyBorder(8, 8, 8, 8)
        event.newEditor?.component?.add(statusPanel, BorderLayout.PAGE_END)
        val filenameLabel = JLabel(AllIcons.Debugger.Console)
        filenameLabel.text = event.newFile?.canonicalPath
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