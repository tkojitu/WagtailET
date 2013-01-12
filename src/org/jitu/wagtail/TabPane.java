package org.jitu.wagtail;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class TabPane extends Fragment {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        if (mgr.findFragmentByTag("edit_pane") == null) {
            Fragment top = Fragment.instantiate(getActivity(), EditPane.class.getName());
            tx.add(R.id.tab_layout, top, "edit_pane");
        }
        if (mgr.findFragmentByTag("find_pane") == null) {
            Fragment bottom = Fragment.instantiate(getActivity(), FindPane.class.getName());
            tx.add(R.id.tab_layout, bottom, "find_pane");
        }
        tx.commit();
    }

    public void showHideFindPane() {
        Fragment bottom = getChildFragmentManager().findFragmentByTag("find_pane");
        if (bottom == null) {
            return;
        }
        if (bottom.isHidden()) {
            getChildFragmentManager().beginTransaction().show(bottom).commit();
        } else {
            getChildFragmentManager().beginTransaction().hide(bottom).commit();
        }
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tab_pane, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
            ((WagtailET) getActivity()).addTab();
            return true;
        case R.id.menu_remove:
            ((WagtailET) getActivity()).removeTab();
            return true;
        case R.id.menu_show_hide:
            showHideFindPane();
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
