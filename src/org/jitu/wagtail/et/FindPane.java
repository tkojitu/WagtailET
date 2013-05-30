package org.jitu.wagtail.et;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class FindPane extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_pane, container, false);
        return view;
    }

    public void requestFocusEditFind() {
        View editFind = getView().findViewById(R.id.edit_find);
        if (editFind != null) {
            editFind.requestFocus();
        }
    }

    public String getEditFindString() {
        EditText edit = (EditText) getView().findViewById(R.id.edit_find);
        return edit.getText().toString();
    }

    public String getEditReplaceString() {
        EditText edit = (EditText) getView().findViewById(R.id.edit_replace);
        return edit.getText().toString();
    }
}
