package com.vg.momento.controller.activities;

import android.support.v4.app.Fragment;
import com.vg.momento.controller.fragments.MomentListFragment;

public class MomentListActivity extends SingleMomentActivity {

    @Override
    protected Fragment createFragment() {
        return MomentListFragment.newInstance();
    }
}
