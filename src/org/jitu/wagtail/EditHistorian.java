package org.jitu.wagtail;

import java.util.LinkedList;

import android.text.Editable;
import android.text.TextWatcher;

public class EditHistorian implements TextWatcher {
    boolean undoing = false;
    private LinkedList<EditEvent> undos = new LinkedList<EditEvent>();
    private LinkedList<EditEvent> redos = new LinkedList<EditEvent>();

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (undoing) {
            return;
        }
        EditEvent event = new EditEvent(start, s.subSequence(start, start + count));
        addUndo(event);
        clearRedos();
    }

    private void addUndo(EditEvent event) {
        undos.addLast(event);
        if (undos.size() > 100) {
            undos.removeFirst();
        }
    }

    private void clearRedos() {
        while (!redos.isEmpty()) {
            redos.removeFirst();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (undoing) {
            return;
        }
        EditEvent event = undos.getLast();
        event.setAfter(start, s.subSequence(start, start + count));
    }

    @Override
    public void afterTextChanged(Editable s) { }

    public void undo(Editable editable) {
        if (undos.isEmpty()) {
            return;
        }
        EditEvent event = undos.removeLast();
        redos.addLast(event);
        undoing = true;
        try {
            event.undo(editable);
        } finally {
            undoing = false;
        }
    }

    public void redo(Editable editable) {
        if (redos.isEmpty()) {
            return;
        }
        EditEvent event = redos.removeLast();
        undos.addLast(event);
        undoing = true;
        try {
            event.redo(editable);
        } finally {
            undoing = false;
        }
    }
}

class EditEvent {
    private int beforePosition;
    private CharSequence before;
    private int afterPosition;
    private CharSequence after;

    EditEvent(int beforePosition, CharSequence before) {
        this.beforePosition = beforePosition;
        this.before = before;
    }

    void setAfter(int afterPosition, CharSequence after) {
        this.afterPosition = afterPosition;
        this.after = after;
    }

    void undo(Editable editable) {
        editable.replace(afterPosition, afterPosition + after.length(), before);
    }

    void redo(Editable editable) {
        editable.replace(beforePosition, beforePosition + before.length(), after);
    }
}
