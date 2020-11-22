package com.github.tbo.ideatwm

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import git4idea.branch.GitBranchUtil
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class EditorSelectionListener : FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val windowManager = source.project.getService(WindowManager::class.java)
        windowManager.focusEditorWindow()
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        event.manager.project.getService(LruTabOrderService::class.java).reorderTabs()
        val statusPanel = JPanel()
        statusPanel.layout = BorderLayout()
        statusPanel.border = EmptyBorder(8, 8, 8, 8)
        event.newEditor?.component?.add(statusPanel, BorderLayout.PAGE_END)
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