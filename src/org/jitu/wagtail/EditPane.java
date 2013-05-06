package org.jitu.wagtail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class EditPane extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.z_edit_pane, container, false);
        addTextWatcher(view);
        EditText edit = (EditText) view.findViewById(R.id.edit);
        if (edit == null) {
            Toast.makeText(getActivity(), "TabPane#onCreateView: invalid view.", Toast.LENGTH_LONG)
                .show();
            return view;
        }
        if (edit.getText().length() == 0) {
            String tag = getParentFragment().getTag();
            ControlBoard board = ((WagtailET) getActivity()).getControlBoard(tag);
            String text = board.getFileControl().read();
            if (text == null) {
                String msg = "TabPane#onCreateView: " + board.getFileControl().getErrorMessage();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return view;
            }
            edit.setText(text);
        }
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
