package org.jitu.wagtail;

import java.util.LinkedList;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;

public class WagtailET extends Activity {
    private LinkedList<String> tags = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_pane_demo);
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        addTab();
    }

    public void addTab() {
        ActionBar bar = getActionBar();
        Tab tab = bar.newTab();
        TabPaneControl control = newTabControl();
        tab.setTabListener(control);
        bar.addTab(tab);
        bar.selectTab(tab);
    }

    private TabPaneControl newTabControl() {
        Fragment frag = Fragment.instantiate(this, TabPane.class.getName());
        getFragmentManager().beginTransaction().add(android.R.id.content, frag, newTag()).commit();
        return new TabPaneControl(frag);
    }

    private String newTag() {
        String tag = TabPaneControl.class.getName() + "." + System.currentTimeMillis();
        tags.add(tag);
        return tag;
    }

    public void removeTab() {
        ActionBar bar = getActionBar();
        if (bar.getTabCount() == 0) {
            return;
        }
        Tab tab = bar.getSelectedTab();
        if (tab == null) {
            return;
        }
        try {
            String tag = findSelectedFragmentTag();
            if (tag == null) {
                return;
            }
            tags.remove(tag);
            Fragment frag = getFragmentManager().findFragmentByTag(tag);
            getFragmentManager().beginTransaction().remove(frag).commit();            
        } finally {
            bar.removeTab(tab);
        }
    }

    private String findSelectedFragmentTag() {
        for (String tag : tags) {
            Fragment frag = getFragmentManager().findFragmentByTag(tag);
            if (!frag.isDetached()) {
                return tag;
            }
        }
        return null;
    }
}
