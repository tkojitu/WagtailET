package org.jitu.wagtail;

import android.app.Fragment;
import android.widget.EditText;

public class ControlBoard {
    private final String tag = getClass().getName() + "." + System.currentTimeMillis();

    private EditControl editControl = new EditControl();
    private FileControl fileControl = new FileControl();
    private TabPaneListener tabPaneListener;

    public String getTag() {
        return tag;
    }

    public TabPaneListener newTabPaneListener(Fragment fragment) {
        tabPaneListener = new TabPaneListener(fragment);
        return tabPaneListener;
    }

    public EditControl getEditControl() {
        return editControl;
    }

    public void addTextWatcher(EditText edit) {
        editControl.addTextWatcher(edit);
    }

    public FileControl getFileControl() {
        return fileControl;
    }
}
