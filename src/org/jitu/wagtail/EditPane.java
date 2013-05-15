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
        EditText edit = getEdit(view);
        if (edit == null) {
            String msg = "TabPane#onCreateView: invalid view.";
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            return view;
        }
        ControlBoard board = getBoard();
        board.setupEditHistorian(edit);
        board.setupIndentMan(edit);
        if (edit.getText().length() == 0) {
            String text = board.fileRead();
            if (text == null) {
                String msg = "TabPane#onCreateView: " + board.getFileErrorMessage();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return view;
            }
            edit.setText(text);
        }
        return view;
    }

    private EditText getEdit(View view) {
        return (EditText) view.findViewById(R.id.edit);
    }

    private ControlBoard getBoard() {
        String tag = getParentTag();
        return ((WagtailET) getActivity()).getControlBoard(tag);
    }

    private String getParentTag() {
        return getParentFragment().getTag();
    }
}
