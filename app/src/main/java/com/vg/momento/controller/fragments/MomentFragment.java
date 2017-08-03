package com.vg.momento.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.vg.momento.dao.implementations.SQLiteMomentDao;
import com.vg.momento.dao.interfaces.MomentDao;
import com.vg.momento.model.Moment;
import java.util.Date;
import java.util.UUID;

public class MomentFragment extends Fragment{

    private Moment mMoment;

    private EditText mTitleEditText;

    private Button mDateDisplayButton;

    private Button mDateButton;

    private Button mTimeButton;

    private CheckBox mIsFavoriteCheckBox;

    private Button mSaveButton;

    private MomentDao mMomentDao;

    private static final String ARG_MOMENT_ID = "moment_id";

    private static final String DIALOG_DATE = "DialogDate";

    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_CODE_DATE = 0;

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
        mMoment = SQLiteMomentDao.getInstance(getActivity()).getMoment(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);

        mTitleEditText = (EditText) v.findViewById(R.id.moment_title);
        mTitleEditText.setText(mMoment.getTitle());
        mTitleEditText.addTextChangedListener(new TextWatcher() {
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

        mDateDisplayButton = (Button) v.findViewById(R.id.moment_display_date);
        mDateDisplayButton.setEnabled(false);
        updateDate();

        mDateButton = (Button) v.findViewById(R.id.moment_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mMoment.getDate());
                dialog.setTargetFragment(MomentFragment.this, REQUEST_CODE_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.moment_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mMoment.getDate());
                dialog.setTargetFragment(MomentFragment.this, REQUEST_CODE_DATE);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mIsFavoriteCheckBox = (CheckBox) v.findViewById(R.id.moment_is_favorite);
        mIsFavoriteCheckBox.setChecked(mMoment.isFavorite());
        mIsFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMoment.setFavorite(isChecked);
            }
        });

        mMomentDao = SQLiteMomentDao.getInstance(getActivity());

        mSaveButton = (Button) v.findViewById(R.id.save_moment_button);
        mSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mMomentDao.updateMoment(mMoment.getId(), mMoment);
                getActivity().onBackPressed();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mMoment.setDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        mDateDisplayButton.setText(mMoment.getDate().toLocaleString());
    }
}

