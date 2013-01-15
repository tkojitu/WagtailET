package org.jitu.wagtail;

import android.app.Fragment;
import android.widget.EditText;

public class ControlBoard {
    private final String tag = getClass().getName() + "." + System.currentTimeMillis();
    private TabPaneListener tabPaneListener;
    private EditControl editControl = new EditControl();

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
}
