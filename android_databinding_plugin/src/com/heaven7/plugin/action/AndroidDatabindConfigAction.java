package com.heaven7.plugin.action;

import com.heaven7.plugin.ui.AndroidDatabindingDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by heaven7 on 2015/12/24.
 */
public class AndroidDatabindConfigAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        VirtualFile dir = e.getData(LangDataKeys.VIRTUAL_FILE);
        if (dir != null && dir.isDirectory()) {
            String text = dir.getName();
            e.getPresentation().setVisible("res".equals(text));
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile dir = e.getData(LangDataKeys.VIRTUAL_FILE);
        if (dir == null) {
            return;
        }

        Project project = e.getProject();
        AndroidDatabindingDialog dialog = new AndroidDatabindingDialog(project, dir);
        dialog.show();

    }
}
