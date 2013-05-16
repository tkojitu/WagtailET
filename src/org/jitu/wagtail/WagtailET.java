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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
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
    private boolean findOtionDown = true;
    private boolean findOptionIgnoreCase = true;

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
        board.setCurrentFile(file);
        Fragment tabPane = newTabPane(board.getTag());
        TabPaneListener listener = board.newTabPaneListener(tabPane);
        boards.put(board.getTag(), board);
        ActionBar bar = getActionBar();
        Tab tab = bar.newTab();
        tab.setTabListener(listener);
        setTabTitle(board, tab);
        bar.addTab(tab);
        bar.selectTab(tab);
    }

    private TabPane newTabPane(String tag) {
        TabPane tabPane = (TabPane) Fragment.instantiate(this, TabPane.class.getName());
        getFragmentManager().beginTransaction().add(android.R.id.content, tabPane, tag).commit();
        return tabPane;
    }

    private void setTabTitle(ControlBoard board, Tab tab) {
        String title = board.getCurrentFileName();
        if (title.isEmpty()) {
           title = getString(R.string.untitled);
        }
        tab.setText(title);
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
            Fragment frag = findSelectedTabPane();
            String tag = frag.getTag();
            getFragmentManager().beginTransaction().remove(frag).commit();
            boards.remove(tag);
        } finally {
            bar.removeTab(tab);
        }
    }

    private TabPane findSelectedTabPane() {
        for (String tag : boards.keySet()) {
            Fragment frag = getFragmentManager().findFragmentByTag(tag);
            if (!frag.isDetached()) {
                return (TabPane) frag;
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
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        if (pair.second.getCurrentFileName().isEmpty()) {
            onSaveAs();
            return;
        }
        if (!pair.second.fileSave(pair.first)) {
            String message = pair.second.getFileErrorMessage();
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
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        if (!pair.second.fileSaveAs(pair.first, filename)) {
            String message = pair.second.getFileErrorMessage();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return;
        }
        Tab tab = getActionBar().getSelectedTab();
        if (tab == null) {
            return;
        }
        setTabTitle(pair.second, tab);
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
        board.setCurrentDirectory(new File(dir));
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
        } else if (r.getString(R.string.menu_item_find_replace).equals(item)) {
            onFindReplace();
        } else if (r.getString(R.string.menu_item_find_option).equals(item)) {
            onFindOption();
        }
    }

    private void onPaste() {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.editPaste(pair.first);
    }

    private void onUndo() {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.editUndo(pair.first);
    }

    private void onRedo() {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.editRedo(pair.first);
    }

    private void onFindReplace() {
        TabPane tabPane = findSelectedTabPane();
        tabPane.showHideFindPane();
    }

    private void onFindOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AlertDialog ad = (AlertDialog) dialog;
                RadioButton radio = (RadioButton) ad.findViewById(R.id.find_option_radio_up);
                findOtionDown = !radio.isChecked();
                CheckBox check = (CheckBox) ad.findViewById(R.id.find_option_check_ignore_case);
                findOptionIgnoreCase = check.isChecked();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        LayoutInflater inflater = getLayoutInflater();
        View container = inflater.inflate(R.layout.find_option_dialog, null);
        builder.setView(container);
        AlertDialog dialog = builder.create();
        setupFindOptionDialog(container);
        dialog.show();
    }

    private void setupFindOptionDialog(View container) {
        int radio_id = findOtionDown ? R.id.find_option_radio_down : R.id.find_option_radio_up;
        RadioButton radio = (RadioButton) container.findViewById(radio_id);
        radio.setChecked(true);
        CheckBox check = (CheckBox) container.findViewById(R.id.find_option_check_ignore_case);
        check.setChecked(findOptionIgnoreCase);
    }

    private Pair<TabPane, ControlBoard> getSelectedTabPaneAndBoard() {
        TabPane tabPane = findSelectedTabPane();
        if (tabPane == null) {
            return null;
        }
        ControlBoard board = getControlBoard(tabPane.getTag());
        return new Pair<TabPane, ControlBoard>(tabPane, board);
    }

    private ControlBoard getSelectedBoard() {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return null;
        }
        return pair.second;
    }

    public ControlBoard getControlBoard(String tag) {
        return boards.get(tag);
    }

    @Override
    public void onBackPressed() {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (!pair.first.isFindPaneVisible()) {
            super.onBackPressed();
            return;
        }
        pair.first.showHideFindPane();
    }

    public void onClickReplaceAll(View view) {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.editReplaceAll(pair.first, findOtionDown, findOptionIgnoreCase);
    }

    public void onClickReplaceFind(View view) {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.editReplaceFind(pair.first, findOtionDown, findOptionIgnoreCase);
    }

    public void onClickFind(View view) {
        Pair<TabPane, ControlBoard> pair = getSelectedTabPaneAndBoard();
        if (pair == null) {
            return;
        }
        pair.second.editFind(pair.first, findOtionDown, findOptionIgnoreCase);
    }

    public void onClickFindOptionDown(View view) {
        findOtionDown = true;
    }

    public void onClickFindOptionUp(View view) {
        findOtionDown = false;
    }

    public void onClickFindOptionIgnoreCase(View view) {
        findOptionIgnoreCase = !findOptionIgnoreCase;
    }
}
