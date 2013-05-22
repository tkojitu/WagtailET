package org.jitu.wagtail.et;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.widget.EditText;

public class EditControl {
    private EditHistorian historian = new EditHistorian();
    private ClipboardControl clipper = new ClipboardControl();

    public void copy(Context context, EditText edit) {
        clipper.copy(context, edit);
    }

    public void cut(Context context, EditText edit) {
        clipper.cut(context, edit);
    }

    public void paste(Context context, EditText edit) {
        clipper.paste(context, edit);
    }

    public void setupEditHistorian(EditText edit) {
        edit.addTextChangedListener(historian);
    }

    public void undo(EditText edit) {
        historian.undo(edit.getText());
    }

    public void redo(EditText edit) {
        historian.redo(edit.getText());
    }

    public void clear(EditText edit) {
        edit.setText("");
    }

    public void setText(EditText edit, String text) {
        edit.setText(text);
        edit.setSelection(text.length());
    }

    public String getText(EditText edit) {
        return edit.getText().toString();
    }

    public boolean find(EditText edit, String needle, boolean doesFindDown,
                        boolean doesIgnoreCase) {
        if (doesIgnoreCase) {
            return doesFindDown ? findDownIgnoreCase(edit, needle) : findUpIgnoreCase(edit, needle);
        } else {
            return doesFindDown ? findDown(edit, needle) : findUp(edit, needle);
        }
    }

    private boolean findDownIgnoreCase(EditText edit, String needle) {
        if (needle.isEmpty()) {
            return false;
        }
        int index = edit.getSelectionEnd();
        String haystack = edit.getText().toString();
        Pattern pat = Pattern.compile(Pattern.quote(needle), Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(haystack);
        if (!mat.find(index)) {
            return false;
        }
        int st = mat.start();
        int ed = mat.end();
        edit.setSelection(st, ed);
        return true;
    }

    private boolean findUpIgnoreCase(EditText edit, String needle) {
        if (needle.isEmpty()) {
            return false;
        }
        int start = edit.getSelectionStart();
        String haystack = edit.getText().toString();
        Pattern pat = Pattern.compile(Pattern.quote(needle), Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(haystack);
        int st = -1;
        int ed = 0;
        while (true) {
            if (!mat.find(ed)) {
                break;
            }
            if (mat.start() >= start) {
                break;
            }
            st = mat.start();
            ed = mat.end();
        }
        if (st < 0) {
            return false;
        }
        edit.setSelection(st, ed);
        return true;
    }

    private boolean findDown(EditText edit, String needle) { 
        if (needle.isEmpty()) {
            return false;
        }
        int index = edit.getSelectionEnd();
        String haystack = edit.getText().toString();
        int ret = haystack.indexOf(needle, index);
        if (ret < 0) {
            return false;
        }
        edit.setSelection(ret, ret + needle.length());
        return true;
    }

    private boolean findUp(EditText edit, String needle) { 
        if (needle.isEmpty()) {
            return false;
        }
        int index = edit.getSelectionStart() - 1;
        if (index < 0) {
            return false;
        }
        String haystack = edit.getText().toString();
        int ret = haystack.lastIndexOf(needle, index);
        if (ret < 0) {
            return false;
        }
        edit.setSelection(ret, ret + needle.length());
        return true;
    }

    public boolean replaceFind(EditText edit, String needle, String replacement,
                               boolean doesFindDown, boolean doesIgnoreCase) {
        if (needle.isEmpty()) {
            return false;
        }
        clipper.delete(edit);
        int index = edit.getSelectionStart();
        edit.getEditableText().insert(index, replacement);
        return find(edit, needle, doesFindDown, doesIgnoreCase);
    }

    public boolean replaceAll(EditText edit, String needle, String replacement,
                              boolean doesFindDown, boolean doesIgnoreCase) {
        boolean found = find(edit, needle, doesFindDown, doesIgnoreCase);
        if (!found) {
            return false;
        }
        while (found) {
            found = replaceFind(edit, needle, replacement, doesFindDown, doesIgnoreCase);
        }
        return true;
    }
}
