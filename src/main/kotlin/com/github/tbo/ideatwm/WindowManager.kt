package com.github.tbo.ideatwm

import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.OnePixelSplitter
import java.awt.Component
import javax.swing.FocusManager
import javax.swing.JComponent
import javax.swing.SwingUtilities

class WindowManager(project: Project) {
    private val fileEditorManager = FileEditorManagerImpl.getInstance(project) as FileEditorManagerImpl

    fun addWindow(component: JComponent) {
        val masterWindow = fileEditorManager.windows[0]
        val panel = masterWindow.owner
        val mainComponent = getMainComponent()
        var proportion = 0.6f
        val secondComponent = if (mainComponent is Splitter) {
            proportion = mainComponent.proportion
            mainComponent
        } else {
            masterWindow.tabbedPane.component
        }

        val splitter: Splitter = OnePixelSplitter(false, proportion, 0.1f, 0.9f)

        panel.removeAll()
        panel.add(splitter)
        splitter.firstComponent = component
        splitter.secondComponent = secondComponent
        focus(component)
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

    fun focusWindow() {
        val mainComponent = getMainComponent()
        val focusedSplitter = getFocusedSplitter(mainComponent)
        if (focusedSplitter != null && mainComponent is Splitter) {
            val temp = mainComponent.firstComponent
            if (hasFocus(focusedSplitter.firstComponent)) {
                mainComponent.firstComponent = focusedSplitter.firstComponent
                focusedSplitter.firstComponent = temp
            } else {
                mainComponent.firstComponent = focusedSplitter.secondComponent
                focusedSplitter.secondComponent = temp
            }
            mainComponent.firstComponent.requestFocusInWindow()
            mainComponent.firstComponent.requestFocus()
        }
    }

    private fun getWindows(): List<Component> {
        var component = getMainComponent()
        val windows = mutableListOf<Component>()
        while (component is Splitter) {
            windows.add(component.firstComponent)
            component = component.secondComponent
        }
        windows.add(component)
        return windows.toList()

    }

    private fun focus(component: Component) {
        component.requestFocusInWindow()
        component.requestFocus()
    }

    fun nextWindow() {
        val windows = getWindows()
        val currentIndex = windows.indexOfLast { hasFocus(it) }
        val nextWindow = windows.elementAtOrElse(currentIndex + 1) { windows.first() }
        focus(nextWindow)
    }

    fun previousWindow() {
        val windows = getWindows()
        val currentIndex = windows.indexOfLast { hasFocus(it) }
        val nextWindow = windows.elementAtOrElse(currentIndex - 1) { windows.last() }
        focus(nextWindow)
    }

    private fun getMainComponent(): Component {
        return fileEditorManager.windows[0].owner.getComponent(0)
    }

    fun hasFocus(component: Component): Boolean {
        return SwingUtilities.isDescendingFrom(FocusManager.getCurrentManager().focusOwner, component)
    }

    private fun getFocusedSplitter(component: Component): Splitter? {
        if (component is Splitter) {
            return if (hasFocus(component.firstComponent) || component.secondComponent !is Splitter && hasFocus(component.secondComponent)) {
                component
            } else {
                getFocusedSplitter(component.secondComponent)
            }
        }
        return null
    }
}
