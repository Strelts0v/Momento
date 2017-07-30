package com.vg.momento.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.vg.momento.controller.fragments.MomentFragment;

import java.util.UUID;

public class MomentActivity extends SingleMomentActivity {

    public static final String EXTRA_MOMENT_ID = "com.vg.android.momento.moment_id";

    public static Intent newIntent(Context packageContext, UUID momentId) {
        Intent intent = new Intent(packageContext, MomentActivity.class);
        intent.putExtra(EXTRA_MOMENT_ID, momentId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID id = (UUID) getIntent().getSerializableExtra(EXTRA_MOMENT_ID);
        return MomentFragment.newInstance(id);
    }
}
