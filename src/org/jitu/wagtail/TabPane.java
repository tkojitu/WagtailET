package org.jitu.wagtail;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.Resources;
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
        case R.id.menu_file:
            showFileMenu();
            return true;
        case R.id.menu_edit:
            showEditMenu();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showFileMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.file_menu_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickFile(dialog, which);
                    }
                });
        builder.create().show();
    }

    private void showEditMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.edit_menu_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickEdit(dialog, which);
                    }
                });
        builder.create().show();
    }

    public void onClickFile(DialogInterface dialog, int which) {
        Resources r = getResources();
        String[] items = r.getStringArray(R.array.file_menu_items);
        String item = items[which];
        if (r.getString(R.string.menu_item_new).equals(item)) {
            // onNew();
        } else if (r.getString(R.string.menu_item_open).equals(item)) {
            // onOpen();
        } else if (r.getString(R.string.menu_item_close).equals(item)) {
            // onClose();
        } else if (r.getString(R.string.menu_item_save).equals(item)) {
            // onSave();
        } else if (r.getString(R.string.menu_item_save_as).equals(item)) {
            // onSaveAs();
        }
    }

    public void onClickEdit(DialogInterface dialog, int which) {
        Resources r = getResources();
        String[] items = r.getStringArray(R.array.edit_menu_items);
        String item = items[which];
        if (r.getString(R.string.menu_item_cut).equals(item)) {
            // tabControl.cut(this);
        } else if (r.getString(R.string.menu_item_copy).equals(item)) {
            // tabControl.copy(this);
        } else if (r.getString(R.string.menu_item_paste).equals(item)) {
            // tabControl.paste(this);
        } else if (r.getString(R.string.menu_item_undo).equals(item)) {
            // tabControl.undo();
        } else if (r.getString(R.string.menu_item_redo).equals(item)) {
            // tabControl.redo();
        }
    }
}
