package org.jitu.wagtail;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class TabPaneListener implements TabListener {
    private Fragment fragment;

    public TabPaneListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.attach(fragment);
        tab.setText(fragment.getTag());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.detach(fragment);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
}
