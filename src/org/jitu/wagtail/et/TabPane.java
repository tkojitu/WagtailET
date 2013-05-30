package org.jitu.wagtail.et;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class TabPane extends Fragment {
    private boolean findPaneVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_pane, container, false);
        addFrags();
        return view;
    }

    private void addFrags() {
        FragmentManager mgr = getChildFragmentManager();
        FragmentTransaction tx = mgr.beginTransaction();
        if (getEditPane() == null) {
            Fragment editPane = Fragment.instantiate(getActivity(), EditPane.class.getName());
            tx.add(R.id.tab_layout, editPane, "edit_pane");
        }
        FindPane findPane = getFindPane();
        if (findPane == null) {
            findPane = (FindPane) Fragment.instantiate(getActivity(), FindPane.class.getName());
            tx.add(R.id.tab_layout, findPane, "find_pane");
        }
        if (findPaneVisible) {
            tx.show(findPane);
        } else {
            tx.hide(findPane);
        }
        tx.commit();
    }

    public void showHideFindPane() {
        FindPane findPane = getFindPane();
        if (findPane == null) {
            return;
        }
        findPaneVisible = !findPaneVisible;
        if (findPaneVisible) {
            getChildFragmentManager().beginTransaction().show(findPane).commit();
            requestFocusFindPane();
        } else {
            getChildFragmentManager().beginTransaction().hide(findPane).commit();
        }
    }

    private void requestFocusFindPane() {
        FindPane findPane = getFindPane();
        if (findPane == null || !isFindPaneVisible()) {
            return;
        }
        findPane.requestFocusEditFind();
    }

    public boolean isFindPaneVisible() {
        return findPaneVisible;
    }

    public EditText getEdit() {
        return getEditPane().getEdit();
    }

    public String getNeedleText() {
        if (!findPaneVisible) {
            return "";
        }
        FindPane findPane = getFindPane();
        if (findPane == null) {
            return "";
        }
        return findPane.getEditFindString();
    }

    public String getReplacementText() {
        if (!findPaneVisible) {
            return "";
        }
        FindPane findPane = getFindPane();
        if (findPane == null) {
            return "";
        }
        return findPane.getEditReplaceString();
    }

    private EditPane getEditPane() {
        return (EditPane) getChildFragmentManager().findFragmentByTag("edit_pane");
    }

    private FindPane getFindPane() {
        return (FindPane) getChildFragmentManager().findFragmentByTag("find_pane");
    }

    public void requestFocusEdit() {
        getEditPane().requestFocusEdit();
    }
}
