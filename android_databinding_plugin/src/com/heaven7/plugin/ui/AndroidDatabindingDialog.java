package com.heaven7.plugin.ui;

import com.heaven7.plugin.util.Util;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by heaven7 on 2015/12/24.
 */
public class AndroidDatabindingDialog extends DialogWrapper {

    private static final String INDENT_SPACE = "{http://xml.apache.org/xslt}indent-amount";
    private static final String sXmlns = "http://schemas.android.com/heaven7/android-databinding/1";
    private static final String sXmlInstance = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String sVersion = "1.0";

    private JPanel panel1;
    private JTextField db_TextField;

    private final VirtualFile mDir;
    private final Project mProject;


    public AndroidDatabindingDialog(@Nullable Project project, VirtualFile dir) {
        super(project);
        this.mDir = dir;
        this.mProject = project;

        setTitle("Android-databinding config");
        setResizable(false);
        init();
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel1;
    }

    @Override
    protected void doOKAction() {
        Application app = ApplicationManager.getApplication();
        app.runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    createDatabindingFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        super.doOKAction();
    }

    /** return true when error */
    private boolean createDatabindingFile() {
        try{
            String f = db_TextField.getText();
            final String filename = (f.endsWith(".xml") ? f : f + ".xml").trim();
            VirtualFile child = mDir.findChild("raw");
            if (child == null) {
                 child = mDir.createChildDirectory(null, "raw");
            }
            VirtualFile newXmlFile = child.findChild(filename);
            if (newXmlFile != null && newXmlFile.exists()) {
                showMessageDialog("create failed!", "caused by the file is existed!");
                return true;
            }
            newXmlFile = child.createChildData(null, filename);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();
            Element root = doc.createElement("DataBinding");
            root.setAttribute("xmlns",sXmlns);
           // root.setAttribute("xmlns:xsi",sXmlInstance);
            root.setAttribute("version",sVersion);
            doc.appendChild(root);

            Element dataEle = doc.createElement("data");
            //<variable name="user" classname="com.heaven7.databinding.demo.bean.User$df" type="bean" />
            Element variable = doc.createElement("variable");
            variable.setAttribute("name","user");
            variable.setAttribute("classname","com.heaven7.databinding.demo.xxx$User");
            variable.setAttribute("type","bean");

           // <import classname="com.heaven7.databinding" alias="ddd"/>
            Element importEle = doc.createElement("import");
            importEle.setAttribute("classname","android.view.View");
            importEle.setAttribute("alias","View");

            dataEle.appendChild(variable);
            dataEle.appendChild(importEle);
            root.appendChild(dataEle);

            PrintWriter out = new PrintWriter(newXmlFile.getOutputStream(null));

            StringWriter writer = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(INDENT_SPACE, "4");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            out.println(writer.getBuffer().toString());
            out.close();
        }catch (Exception e){
            showMessageDialog("Exception occoured", "detail : " + Util.toString(e));
            return true;
        }
        return false;
    }

    private void showMessageDialog(String title, String message) {
        Messages.showMessageDialog(
                mProject, message, title, Messages.getErrorIcon());
    }
}
