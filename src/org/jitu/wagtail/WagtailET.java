package org.jitu.wagtail;

import java.io.File;
import java.util.HashMap;

import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;

public class WagtailET extends Activity {
    public static final String OI_EXTRA_BUTTON_TEXT = "org.openintents.extra.BUTTON_TEXT";
    public static final String OI_EXTRA_TITLE = "org.openintents.extra.TITLE";
    public static final String OI_ACTION_PICK_DIRECTORY = "org.openintents.action.PICK_DIRECTORY";
    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";

    private static final int REQUEST_OI_ACTION_PICK_FILE = 11;
    private static final int REQUEST_OI_ACTION_PICK_DIRECTORY = 12;

    private HashMap<String, ControlBoard> boards = new HashMap<String, ControlBoard>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_pane_demo);
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        addTab(null);
    }

    private void addTab(File file) {
        ControlBoard board = new ControlBoard();
        board.getFileControl().setCurrentFile(file);
        Fragment tabPane = newTabPane(board.getTag());
        TabPaneListener listener = board.newTabPaneListener(tabPane);
        boards.put(board.getTag(), board);
        ActionBar bar = getActionBar();
        Tab tab = bar.newTab();
        tab.setTabListener(listener);
        String title = board.getFileControl().getCurrentFileName();
        if (title.isEmpty()) {
           title = getString(R.string.untitled);
        }
        tab.setText(title);
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
            addTab(null);
        } else if (r.getString(R.string.menu_item_open).equals(item)) {
            onOpen();
        } else if (r.getString(R.string.menu_item_close).equals(item)) {
            removeTab();
        } else if (r.getString(R.string.menu_item_save).equals(item)) {
            onSave();
        } else if (r.getString(R.string.menu_item_save_as).equals(item)) {
            onSaveAs();
        }
    }

    private void onOpen() {
        String home = FileControl.getHomePath();
        Intent intent = new Intent(OI_ACTION_PICK_FILE);    
        intent.setData(Uri.parse("file://" + home));
        intent.putExtra(OI_EXTRA_TITLE, getString(R.string.oi_open_title));
        intent.putExtra(OI_EXTRA_BUTTON_TEXT, getString(R.string.oi_open_button));
        try {
            startActivityForResult(intent, REQUEST_OI_ACTION_PICK_FILE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.oi_no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private void onSave() {
        Pair<EditText, ControlBoard> pair = getSelectedEditAndBoard();
        if (pair == null) {
            return;
        }
        if (pair.second.getFileControl().getCurrentFileName().isEmpty()) {
            onSaveAs();
            return;
        }
        if (!pair.second.getFileControl().save(pair.first.getText().toString())) {
            String message = pair.second.getFileControl().getErrorMessage();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void onSaveAs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.filename);
                String filename = edit.getText().toString();
                onFileSaveDialogOk(filename);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.file_save_dialog, null));
        builder.create().show();
    }

    private void onFileSaveDialogOk(String filename) {
        Pair<EditText, ControlBoard> pair = getSelectedEditAndBoard();
        if (pair == null) {
            return;
        }
        if (!pair.second.getFileControl().saveAs(filename, pair.first.getText().toString())) {
            String message = pair.second.getFileControl().getErrorMessage();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public void onClickOpenFileManager(View view) {
        String home = FileControl.getHomePath();
        Intent intent = new Intent(OI_ACTION_PICK_DIRECTORY);
        intent.setData(Uri.parse("file://" + home));
        intent.putExtra(OI_EXTRA_TITLE, getString(R.string.oi_pick_directory_title));
        intent.putExtra(OI_EXTRA_BUTTON_TEXT, getString(R.string.oi_pick_directory_button));
        try {
            startActivityForResult(intent, REQUEST_OI_ACTION_PICK_DIRECTORY);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.oi_no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
        case REQUEST_OI_ACTION_PICK_FILE:
            onOiActionPickFile(data);
            break;
        case REQUEST_OI_ACTION_PICK_DIRECTORY:
            onOiActionPickDirectory(data);
            break;
        }
    }

    private void onOiActionPickFile(Intent data) {
        String path = data.getDataString();
        if (path == null || path.isEmpty()) {
            return;
        }
        if (path.startsWith("file://")) {
            path = path.substring(7);
        }
        if (path.isEmpty()) {
            return;
        }
        addTab(new File(path));
    }

    private void onOiActionPickDirectory(Intent data) {
        String dir = data.getDataString();
        if (dir == null || dir.isEmpty()) {
            return;
        }
        if (dir.startsWith("file://")) {
            dir = dir.substring(7);
        }
        ControlBoard board = getSelectedBoard();
        if (board == null) {
            return;
        }
        board.getFileControl().setCurrentDirectory(new File(dir));
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

    private ControlBoard getSelectedBoard() {
        Pair<Fragment, ControlBoard> pair = getSelectedFragAndBoard();
        if (pair == null) {
            return null;
        }
        return pair.second;
    }

    public ControlBoard getControlBoard(String tag) {
        return boards.get(tag);
    }
}
