package com.vg.momento.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.vg.momento.R;
import com.vg.momento.controller.activities.MomentPagerActivity;
import com.vg.momento.dao.implementations.EmbeddedMomentDao;
import com.vg.momento.dao.interfaces.MomentDao;
import com.vg.momento.model.Moment;
import java.util.List;

public class MomentListFragment extends Fragment {

    private RecyclerView mMomentRecycleView;

    private MomentAdapter mAdapter;

    private MomentDao mMomentDao;

    private boolean mSubtitleVisible;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private static final int REQUEST_CODE_MOMENT_ACTIONS = 2;

    private static final String DIALOG_MOMENT_ACTIONS = "MomentActions";

    public static MomentListFragment newInstance() {
        // Initializing of bundle for fragment
        Bundle args = new Bundle();

        MomentListFragment fragment = new MomentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moment_list, container, false);

        // Tell FragmentManager to call method onCreateOptionsMenu() to init menu
        setHasOptionsMenu(true);

        mMomentRecycleView = (RecyclerView) view.findViewById(R.id.moment_recycler_view);

        // RecycleView delegates LayoutManager management of widgets
        // LinearLayoutManager dispose elements using vertical list
        mMomentRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize MomentDao object
        mMomentDao = EmbeddedMomentDao.getInstance();

        // save state of subtitle visibility for cases, when user rotate phone
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean
                    (SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    private void updateUI() {
        List<Moment> moments = mMomentDao.getAllMoments();

        if (mAdapter == null) {
            mAdapter = new MomentAdapter(moments);
            mMomentRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.mMoments = moments;
            mAdapter.notifyDataSetChanged();
        }

        // to update subtitle after clicking Back button
        updateSubtitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_moment_menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_moment:
                Moment moment = new Moment();
                mMomentDao.addMoment(moment);
                Intent intent = MomentPagerActivity
                        .newIntent(getActivity(), moment.getId());
                startActivity(intent);
                return true;

            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * sets count of moments in subtitle of toolbar
     */
    public void updateSubtitle(){
        int momentCount = mMomentDao.getAllMoments().size();
        String subtitle = getString(R.string.subtitle_format, momentCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_MOMENT_ACTIONS) {
            updateUI();
        }
    }

    private class MomentHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private Moment mMoment;

        private TextView mTitleTextView;

        private TextView mDateTextView;

        private CheckBox mIsFavoriteCheckBox;

        public MomentHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView.findViewById(
                    R.id.list_item_moment_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(
                    R.id.list_item_moment_date_text_view);
            mIsFavoriteCheckBox = (CheckBox) itemView.findViewById(
                    R.id.list_item_moment_is_favorite_check_box);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindMoment(Moment moment){
            mMoment = moment;
            mTitleTextView.setText(mMoment.getTitle());
            mDateTextView.setText(mMoment.getDate().toLocaleString());
            mIsFavoriteCheckBox.setChecked(mMoment.isFavorite());
        }

        @Override
        public void onClick(View v) {
            Intent intent = MomentPagerActivity.newIntent(getActivity(), mMoment.getId());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            FragmentManager manager = getFragmentManager();
            MomentActionsFragment dialog = MomentActionsFragment.newInstance(mMoment.getId());
            dialog.setTargetFragment(MomentListFragment.this, REQUEST_CODE_MOMENT_ACTIONS);
            dialog.show(manager, DIALOG_MOMENT_ACTIONS);
            return true;
        }
    }

    private class MomentAdapter extends RecyclerView.Adapter<MomentHolder> {

        private List<Moment> mMoments;

        public MomentAdapter(List<Moment> moments) {
            mMoments = moments;
        }

        @Override
        public MomentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_moment, parent, false);
            return new MomentHolder(view);
        }

        @Override
        public void onBindViewHolder(MomentHolder holder, int position) {
            Moment moment = mMoments.get(position);
            holder.bindMoment(moment);
        }

        @Override
        public int getItemCount() {
            return mMoments.size();
        }
    }
}
