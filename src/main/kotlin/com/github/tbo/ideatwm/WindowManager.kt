package com.github.tbo.ideatwm

import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.tabs.impl.SingleHeightTabs
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import java.awt.Component
import javax.swing.FocusManager
import javax.swing.JComponent
import javax.swing.SwingUtilities

class WindowManager(project: Project) {
    private val fileEditorManager = FileEditorManagerImpl.getInstance(project) as FileEditorManagerImpl
    private var previousFocusedWindow: Component? = null

    fun addWindow(component: JComponent) {
        if (fileEditorManager.windows.isEmpty()) {
            return
        }

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

    fun focusCurrentWindow() {
        val currentWindow = getWindows().findLast { hasFocus(it) }
        if (currentWindow == getWindows().first() && previousFocusedWindow != null) {
            focusWindow(previousFocusedWindow!!)
        } else {
            currentWindow?.let { focusWindow(it) }
        }
    }

    fun focusEditorWindow() {
        getEditorWindow()?.let { focusWindow(it) }
    }

    private fun focusWindow(window: Component) {
        val mainComponent = getMainComponent()
        val focusedSplitter = getSplitters().findLast { it.firstComponent == window || it.secondComponent == window }
        if (mainComponent is Splitter) {
            if (focusedSplitter != null && window != getWindows().first()) {
                previousFocusedWindow = mainComponent.firstComponent
                if (mainComponent == focusedSplitter) {
                    mainComponent.swapComponents()
                } else {
                    val temp = mainComponent.firstComponent
                    if (focusedSplitter.firstComponent == window) {
                        mainComponent.firstComponent = focusedSplitter.firstComponent
                        focusedSplitter.firstComponent = temp
                    } else {
                        mainComponent.firstComponent = focusedSplitter.secondComponent
                        focusedSplitter.secondComponent = temp
                    }
                }
            }
            focus(mainComponent.firstComponent)
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

    private fun getEditorWindow(): Component? {
        return getWindows().find { it !is ShellTerminalWidget }
    }

    private fun focus(component: Component) {
        if (component is ShellTerminalWidget) {
            component.requestFocusInWindow()
        } else {
            fileEditorManager.windows[0].tabbedPane.selectedIndex = 0
        }
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

    fun deleteWindow() {
        val windows = getWindows()
        val focusedWindow = windows.findLast { hasFocus(it) }
        if (focusedWindow !is ShellTerminalWidget) {
            fileEditorManager.windows[0].tabbedPane.removeTabAt(0, 0)
            return
        }
        val splitters = getSplitters()
        val availableSplitters = splitters.filter { it.firstComponent != focusedWindow && it.secondComponent != focusedWindow }
        val focusedSplitter = splitters.findLast { hasFocus(it) }
        if (availableSplitters.isEmpty()) {
            val masterWindow = fileEditorManager.windows[0]
            val panel = masterWindow.owner
            panel.removeAll()
            val component = if (focusedWindow == windows.first()) focusedSplitter!!.secondComponent else focusedSplitter!!.firstComponent
            panel.add(component)
            component.requestFocusInWindow()
        } else {
            if (focusedSplitter == splitters.last()) {
                val secondToLastSplitter = splitters[splitters.size - 2]
                secondToLastSplitter.secondComponent = if (focusedWindow == focusedSplitter.firstComponent) focusedSplitter.secondComponent else focusedSplitter.firstComponent
                focus(secondToLastSplitter.secondComponent)
            } else {
                availableSplitters.reduce { a, b ->
                    a.secondComponent = b
                    b
                }
                focus(splitters[splitters.indexOf(focusedSplitter) + 1].firstComponent)
            }
        }
        setMainComponent(availableSplitters.first())
        focusedWindow.close()
        focusedSplitter?.dispose()
        updateDimensions()
    }

    fun growMasterWindow() {
        val mainComponent = getMainComponent()
        if (mainComponent is Splitter) {
            mainComponent.proportion += 0.05f
        }
    }

    fun shrinkMasterWindow() {
        val mainComponent = getMainComponent()
        if (mainComponent is Splitter) {
            mainComponent.proportion -= 0.05f
        }
    }

    private fun updateDimensions() {
        val mainComponent = getMainComponent()
        if (mainComponent is Splitter) {
            mainComponent.proportion = 0.6f
            mainComponent.orientation = false
            updateStackDimensions(mainComponent.secondComponent)
        }
    }

    private fun getSplitters(): List<Splitter> {
        var component = getMainComponent()
        val splitters = mutableListOf<Splitter>()
        while (component is Splitter) {
            splitters.add(component)
            component = component.secondComponent
        }
        return splitters.toList()
    }

    private fun getMainComponent(): Component {
        return fileEditorManager.windows[0].owner.getComponent(0)
    }

    private fun setMainComponent(component: Component) {
        val owner = fileEditorManager.windows[0].owner
        if (owner.components.first() != component) {
            owner.removeAll()
            owner.add(component)
        }
    }

    private fun hasFocus(component: Component): Boolean {
        return SwingUtilities.isDescendingFrom(FocusManager.getCurrentManager().focusOwner, component)
    }
}
