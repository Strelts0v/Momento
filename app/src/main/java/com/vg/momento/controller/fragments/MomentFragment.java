package com.vg.momento.controller.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.vg.momento.R;
import com.vg.momento.dao.implementations.EmbeddedMomentDao;
import com.vg.momento.model.Moment;

import java.util.UUID;

public class MomentFragment extends Fragment{

    private Moment mMoment;

    private EditText mEditText;

    private Button mDateButton;

    private CheckBox mIsFavoriteCheckBox;

    private static final String ARG_MOMENT_ID = "moment_id";

    public static MomentFragment newInstance(UUID momentId) {
        // initialize bundle with arguments for MomentFragment
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOMENT_ID, momentId);

        MomentFragment fragment = new MomentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        UUID id = (UUID) getArguments().getSerializable(ARG_MOMENT_ID);
        mMoment = EmbeddedMomentDao.getInstance().getMoment(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);

        mEditText = (EditText) v.findViewById(R.id.moment_title);
        mEditText.setText(mMoment.getTitle());
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence c, int start,
                                      int before, int count) {
                mMoment.setTitle(c.toString());
            }

            @Override
            public void afterTextChanged(Editable c) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.moment_date);
        mDateButton.setText(mMoment.getDate().toLocaleString());
        mDateButton.setEnabled(false);

        mIsFavoriteCheckBox = (CheckBox) v.findViewById(R.id.moment_is_favorite);
        mIsFavoriteCheckBox.setChecked(mMoment.isFavorite());
        mIsFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMoment.setFavorite(isChecked);
            }
        });

        return v;
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }
}
