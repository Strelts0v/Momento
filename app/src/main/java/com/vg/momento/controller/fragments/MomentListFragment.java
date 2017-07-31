package com.vg.momento.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

        mMomentRecycleView = (RecyclerView) view.findViewById(R.id.moment_recycler_view);

        // RecycleView delegates LayoutManager management of widgets
        // LinearLayoutManager dispose elements using vertical list
        mMomentRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize MomentDao object
        mMomentDao = EmbeddedMomentDao.getInstance();

        updateUI();

        return view;
    }

    private void updateUI() {
        List<Moment> moments = mMomentDao.getAllMoments();

        if (mAdapter == null) {
            mAdapter = new MomentAdapter(moments);
            mMomentRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class MomentHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

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
