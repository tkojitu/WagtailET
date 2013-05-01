package org.jitu.wagtail;

import java.io.File;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FileChooser extends Activity implements OnItemClickListener {
    public static final String ARG_PATH = "ARG_PATH";
    public static final String RESULT_PATH = "path";

    protected File root;
    protected File currentDir;
    protected FileArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 10) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setupRoot();
        setTitleDir();
        setupContentView();
        setupList();
    }

    protected void setupRoot() {
        root = currentDir = getArgFile();
    }

    protected void setupContentView() {
        setContentView(R.layout.file_chooser);
    }

    protected File getArgFile() {
        String path = getIntent().getStringExtra(ARG_PATH);
        return new File(path);
    }

    private void setupList() {
        ListView lv = findFileList();
        lv.setOnItemClickListener(this);
        adapter = newFileArrayAdapter();
        lv.setAdapter(adapter);
    }

    protected ListView findFileList() {
        return (ListView) findViewById(R.id.file_chooser_list);
    }

    protected FileArrayAdapter newFileArrayAdapter() {
        return FileArrayAdapter.newInstance(FileChooser.this, R.layout.file_chooser_list,
                currentDir);
    }

    protected void setTitleDir() {
        setTitle(currentDir.getName());
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File save = currentDir;
        try {
            File file = adapter.getFile(position);
            if (file.isDirectory()) {
                currentDir = file.getAbsoluteFile();
                setupList();
            } else {
                onFileClick(file);
            }
        } catch (Exception e) {
            currentDir = save;
        }
        setTitleDir();
    }

    protected void onFileClick(File file) {
        backToParent(0, file.getAbsolutePath());
    }

    protected void backToParent(int resultCode, String path) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_PATH, path);
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
             return cancel();
        default:
            return true;
        }
    }

    protected boolean cancel() {
        backToParent(-1, "");
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentDir.equals(root)) {
            cancel();
            return;
        }
        moveUp();
    }

    private void moveUp() {
        currentDir = currentDir.getParentFile();
        setupList();
        setTitleDir();
    }
}
