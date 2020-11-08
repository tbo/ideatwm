package com.github.tbo.ideatwm

import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.OnePixelSplitter
import javax.swing.JComponent

class WindowManager(project: Project) {
    private val fileEditorManager = FileEditorManagerImpl.getInstance(project) as FileEditorManagerImpl

    fun addWindow(component: JComponent) {
        val masterWindow = fileEditorManager.windows[0]

        val panel = masterWindow.owner
        val currentComponent = panel.getComponent(0)
        var proportion = 0.6f
        val secondComponent = if (currentComponent is Splitter) {
            proportion = currentComponent.proportion
            currentComponent
        } else {
            masterWindow.tabbedPane.component
        }

        val splitter: Splitter = OnePixelSplitter(false, proportion, 0.1f, 0.9f)

        panel.removeAll()
        panel.add(splitter)
        splitter.firstComponent = component
        splitter.secondComponent = secondComponent
        component.requestFocusInWindow()
        updateStackDimensions(secondComponent)
    }

    private fun updateStackDimensions(component: JComponent, depth: Int = 0): Int {
        if (component is Splitter) {
            component.orientation = true
            val totalDepth = updateStackDimensions(component.secondComponent, depth + 1)
            val partitions = totalDepth - depth
            if (partitions > 0) {
                component.proportion = 1 / partitions.toFloat()
            }
            return totalDepth
        }
        return depth + 1
    }
}
