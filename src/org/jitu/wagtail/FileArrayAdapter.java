package org.jitu.wagtail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<File> {
    private Context context;
    private int id;
    private List<File> files;

    public static FileArrayAdapter newInstance(Context context, int textViewResourceId, File dir) {
        List<File> files = getFileList(dir);
        files.add(0, dir.getParentFile());
        return new FileArrayAdapter(context, textViewResourceId, files);
    }

    private static List<File> getFileList(File dir) {
        List<File> files = new ArrayList<File>();
        File[] array = dir.listFiles();
        for (File file: array) {
            files.add(file);
        }
        Collections.sort(files);
        Collections.reverse(files);
        return files;
    }

    public FileArrayAdapter(Context context, int textViewResourceId,
                            List<File> files) {
        super(context, textViewResourceId, files);
        this.context = context;
        id = textViewResourceId;
        this.files = files;
    }

    public File getFile(int i) {
        return files.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cv = convertView;
        if (cv == null) {
            LayoutInflater li =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cv = li.inflate(id, null);
        }
        TextView tv = (TextView) cv.findViewById(R.id.filename);
        if (tv == null) {
            return cv;
        }
        if (position == 0) {
            tv.setText("..");
            return cv;
        }
        final File file = files.get(position);
        if (file == null) {
            return cv;
        }
        String name = file.getName();
        if (file.isDirectory()) {
            name += "/";
        }
        tv.setText(name);
        return cv;
    }
}
