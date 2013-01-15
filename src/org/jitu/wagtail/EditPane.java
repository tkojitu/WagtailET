package org.jitu.wagtail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EditPane extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.z_edit_pane, container, false);
        addTextWatcher(view);
        return view;
    }

    private void addTextWatcher(View view) {
        EditText edit = (EditText) view.findViewById(R.id.edit);
        WagtailET app = (WagtailET) getActivity();
        String tag = getParentTag();
        ControlBoard board = app.getControlBoard(tag);
        board.addTextWatcher(edit);
    }

    private String getParentTag() {
        return getParentFragment().getTag();
    }
}
