package org.jitu.wagtail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.CharArrayBuffer;

import android.os.Environment;

public class FileControl {
    private Exception error;
    private File currentDirectory = new File(getHomePath());
    private File currentFile;

    public static String getHomePath() {
        File storage = Environment.getExternalStorageDirectory();
        return storage.getAbsolutePath();
    }
    
    public void setCurrentDirectory(File dir) {
        currentDirectory = dir;
    }

    public void setCurrentFile(File file) {
        this.currentFile = file;
    }

    public String getCurrentFileName() {
        if (currentFile == null) {
            return "";
        }
        return currentFile.getName();
    }

    public String read() {
        if (currentFile == null) {
            return "";
        }
        try {
            URL url = currentFile.toURI().toURL();
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            int nread;
            char[] chunk = new char[8192];
            CharArrayBuffer buf = new CharArrayBuffer(8192);
            while ((nread = br.read(chunk, 0, chunk.length)) != -1) {
                buf.append(chunk, 0, nread);
            }
            br.close();
            error = null;
            return buf.toString();
        } catch (IOException e) {
            error = e;
            return null;
        }
    }

    public boolean saveAs(String filename, String text) {
        File newFile = new File(currentDirectory, filename);
        return saveFile(newFile, text);
    }
    
    public boolean save(String text) {
        if (currentFile == null) {
            return true;
        }
        return saveFile(currentFile, text);
    }

    public boolean saveFile(File file, String text) {
        try {
            FileWriter wf = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(wf);
            bw.write(text, 0, text.length());
            bw.close();
            currentFile = file;
            return true;
        } catch (IOException e) {
            error = e;
            return false;
        }
    }

    public String getErrorMessage() {
        if (error == null) {
            return "";
        }
        return error.getMessage();
    }
}
