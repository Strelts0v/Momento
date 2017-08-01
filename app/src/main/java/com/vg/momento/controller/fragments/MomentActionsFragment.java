package com.vg.momento.controller.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.vg.momento.R;
import com.vg.momento.controller.activities.MomentPagerActivity;
import com.vg.momento.dao.implementations.EmbeddedMomentDao;
import com.vg.momento.dao.interfaces.MomentDao;
import java.util.UUID;

public class MomentActionsFragment extends DialogFragment{

    private UUID mMomentId;

    private MomentDao mMomentDao;

    private ListView mActionsListView;

    private static final String ARG_MOMENT_ID = "moment_id";

    private static final int DELETE_ACTION_POSITION = 0;

    private static final int UPDATE_ACTION_POSITION = 1;

    public static MomentActionsFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOMENT_ID, id);
        MomentActionsFragment fragment = new MomentActionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMomentId = (UUID) getArguments().getSerializable(ARG_MOMENT_ID);

        // init of MomentDao
        mMomentDao = EmbeddedMomentDao.getInstance();

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_moment_actions, null);

        mActionsListView = (ListView) view.findViewById(R.id.moment_actions_list_view);

        // create Adapter for ListView
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.moment_action_list, android.R.layout.simple_list_item_1);

        mActionsListView.setAdapter(adapter);

        // handle clicking on ListView item
        mActionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position){
                    case DELETE_ACTION_POSITION:
                        deleteMoment();
                        break;
                    case UPDATE_ACTION_POSITION:
                        updateMoment();
                        break;
                    default:
                        break;
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.moment_actions_title)
                .create();
    }

    private void deleteMoment(){
        mMomentDao.deleteMoment(mMomentId);
        sendResult(Activity.RESULT_OK);
        dismiss();
    }

    private void updateMoment(){
        Intent intent = MomentPagerActivity.newIntent(getActivity(), mMomentId);
        startActivity(intent);
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
