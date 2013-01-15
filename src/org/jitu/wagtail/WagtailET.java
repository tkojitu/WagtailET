package org.jitu.wagtail;

import java.util.HashMap;

import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;

public class WagtailET extends Activity {
    private HashMap<String, ControlBoard> boards = new HashMap<String, ControlBoard>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_pane_demo);
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        addTab();
    }

    private void addTab() {
        ControlBoard board = new ControlBoard();
        Fragment tabPane = newTabPane(board.getTag());
        TabPaneListener listener = board.newTabPaneListener(tabPane);
        boards.put(board.getTag(), board);
        ActionBar bar = getActionBar();
        Tab tab = bar.newTab();
        tab.setTabListener(listener);
        bar.addTab(tab);
        bar.selectTab(tab);
    }

    private Fragment newTabPane(String tag) {
        Fragment tabPane = Fragment.instantiate(this, TabPane.class.getName());
        getFragmentManager().beginTransaction().add(android.R.id.content, tabPane, tag).commit();
        return tabPane;
    }

    private void removeTab() {
        ActionBar bar = getActionBar();
        if (bar.getTabCount() == 0) {
            return;
        }
        Tab tab = bar.getSelectedTab();
        if (tab == null) {
            return;
        }
        try {
            Fragment frag = findSelectedFragment();
            String tag = frag.getTag();
            getFragmentManager().beginTransaction().remove(frag).commit();
            boards.remove(tag);
        } finally {
            bar.removeTab(tab);
        }
    }

    private Fragment findSelectedFragment() {
        for (String tag : boards.keySet()) {
            Fragment frag = getFragmentManager().findFragmentByTag(tag);
            if (!frag.isDetached()) {
                return frag;
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
        case R.id.menu_find_replace:
            onFindReplace();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showFileMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.edit_menu_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickEdit(dialog, which);
                    }
                });
        builder.create().show();
    }

    private void onClickFile(DialogInterface dialog, int which) {
        Resources r = getResources();
        String[] items = r.getStringArray(R.array.file_menu_items);
        String item = items[which];
        if (r.getString(R.string.menu_item_new).equals(item)) {
            addTab();
        } else if (r.getString(R.string.menu_item_open).equals(item)) {
            // onOpen();
        } else if (r.getString(R.string.menu_item_close).equals(item)) {
            removeTab();
        } else if (r.getString(R.string.menu_item_save).equals(item)) {
            // onSave();
        } else if (r.getString(R.string.menu_item_save_as).equals(item)) {
            // onSaveAs();
        }
    }

    private void onClickEdit(DialogInterface dialog, int which) {
        Resources r = getResources();
        String[] items = r.getStringArray(R.array.edit_menu_items);
        String item = items[which];
        if (r.getString(R.string.menu_item_paste).equals(item)) {
            onPaste();
        } else if (r.getString(R.string.menu_item_undo).equals(item)) {
            onUndo();
        } else if (r.getString(R.string.menu_item_redo).equals(item)) {
            onRedo();
        }
    }

    private void onPaste() {
        Pair<EditText, ControlBoard> pair = getSelectedEditAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.getEditControl().paste(this, pair.first);
    }

    private void onUndo() {
        Pair<EditText, ControlBoard> pair = getSelectedEditAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.getEditControl().undo(pair.first);
    }

    private void onRedo() {
        Pair<EditText, ControlBoard> pair = getSelectedEditAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.getEditControl().redo(pair.first);
    }

    private void onFindReplace() {
        TabPane tabPane = (TabPane) findSelectedFragment();
        tabPane.showHideFindPane();
    }

    private Pair<Fragment, ControlBoard> getSelectedFragAndBoard() {
        Fragment frag = findSelectedFragment();
        if (frag == null) {
            return null;
        }
        ControlBoard board = getControlBoard(frag.getTag());
        return new Pair<Fragment, ControlBoard>(frag, board);
    }

    private Pair<EditText, ControlBoard> getSelectedEditAndBoard() {
        Pair<Fragment, ControlBoard> pair = getSelectedFragAndBoard();
        if (pair == null) {
            return null;
        }
        EditText edit = getEditText(pair.first);
        return new Pair<EditText, ControlBoard>(edit, pair.second);
    }

    private EditText getEditText(Fragment frag) {
        return (EditText) frag.getView().findViewById(R.id.edit);
    }

    public ControlBoard getControlBoard(String tag) {
        return boards.get(tag);
    }
}
