package org.jitu.wagtail;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class FileSaver extends FileChooser implements DialogInterface.OnClickListener {
    private static final String SAVED_PATH = "SAVED_PATH";

    private String savedPath = null;

    protected void setupContentView() {
        setContentView(R.layout.file_saver);
        setFileEdit();
    }

    protected void setupRoot() {
        File tmp = getArgFile().getParentFile();
        if (tmp.exists() && tmp.isDirectory()) {
            root = currentDir = tmp;
        } else {
            root = currentDir = Environment.getExternalStorageDirectory();
        }
    }

    private void setFileEdit() {
        EditText et = (EditText) findViewById(R.id.file_edit);
        String filename = getArgFile().getName();
        et.setText(filename);
        et.setSelection(et.getText().length());
    }

    protected ListView findFileList() {
        return (ListView) findViewById(R.id.file_list);
    }

    protected FileArrayAdapter newFileArrayAdapter() {
        return FileArrayAdapter.newInstance(FileSaver.this, R.layout.file_chooser_list,
                currentDir);
    }

    protected void onFileClick(File file) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
             return cancelToHome();
        default:
            return true;
        }
    }

    private boolean cancelToHome() {
        backToParent(-2, "");
        return true;
    }

    public void onOk(View view) {
        String path = getSavedPath();
        File file = new File(path);
        if (file.exists()) {
            savedPath = path;
            showReplaceDialog();
            return;
        }
        backToParent(0, path);
    }

    private void showReplaceDialog() {
        getReplaceDialog().show();
    }

    public void onCancel(View view) {
        cancel();
    }

    private String getSavedPath() {
        EditText et = (EditText) findViewById(R.id.file_edit);
        String text = et.getText().toString();
        return currentDir + File.separator + text;
    }

    private AlertDialog getReplaceDialog() {
        return new AlertDialog.Builder(this)
            .setMessage(R.string.replace_message)
            .setPositiveButton(R.string.yes, this)
            .setNeutralButton(R.string.no, this)
            .setNegativeButton(R.string.cancel, this)
            .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_NEGATIVE:
            onCancelReplace();
            break;
        case DialogInterface.BUTTON_NEUTRAL:
            onNoReplace();
            break;
        case DialogInterface.BUTTON_POSITIVE:
            onYesReplace();
            break;
        default:
            Log.i("FileSaver", "unknown choice: " + which);
            break;
        }
    }

    private void onCancelReplace() {
        cancel();
    }

    private void onNoReplace() { }

    private void onYesReplace() {
        String tmp = savedPath;
        savedPath = null;
        backToParent(0, tmp);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedPath != null) {
            outState.putString(SAVED_PATH, savedPath);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedPath = savedInstanceState.getString(SAVED_PATH);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
