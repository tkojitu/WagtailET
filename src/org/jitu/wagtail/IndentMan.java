package org.jitu.wagtail;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.View;

public class IndentMan extends TextKeyListener implements TextWatcher {
    private boolean enterDown = false;
    private int index = -1;

    public IndentMan() {
        super(TextKeyListener.Capitalize.NONE, false);
    }

    public boolean onKeyDown(View view, Editable content, int keyCode, KeyEvent event) {
        enterDown = (keyCode == KeyEvent.KEYCODE_ENTER);
        return super.onKeyDown(view, content, keyCode, event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        index = start + count;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (enterDown) {
            enterDown = false;
            String leadingWhites = getLeadingWhites(s);
            s.insert(index, leadingWhites);
        }
        enterDown = false;
    }

    private String getLeadingWhites(Editable s) {
        if (index > s.length()) {
            return "";
        }
        if (s.charAt(index - 1) != '\n') {
            return "";
        }
        if (index - 2 < 0) {
            return "";
        }
        int n = s.toString().lastIndexOf("\n", index - 2);
        n = (n < 0) ? 0 : n + 1;
        StringBuilder buf = new StringBuilder();
        while (n < s.length() && s.charAt(n) == ' ') {
            buf.append(' ');
            ++n;
        }
        return buf.toString();
    }
}
