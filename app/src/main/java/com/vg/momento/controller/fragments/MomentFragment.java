package com.vg.momento.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vg.momento.R;
import com.vg.momento.dao.implementations.FileSystemPhotoMomentDao;
import com.vg.momento.dao.implementations.SQLiteMomentDao;
import com.vg.momento.dao.interfaces.MomentDao;
import com.vg.momento.dao.interfaces.PhotoMomentDao;
import com.vg.momento.dao.utils.PictureUtils;
import com.vg.momento.model.Moment;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class MomentFragment extends Fragment{

    private Moment mMoment;

    private File mPhotoFile;

    private EditText mTitleEditText;

    private Button mDateDisplayButton;

    private Button mDateButton;

    private Button mTimeButton;

    private CheckBox mIsFavoriteCheckBox;

    private Button mSaveButton;

    private Button mReportButton;

    private Button mPeopleButton;

    private ImageButton mPhotoButton;

    private ImageView mPhotoView;

    private MomentDao mMomentDao;

    private PhotoMomentDao mPhotoMomentDao;

    private static final String ARG_MOMENT_ID = "moment_id";

    private static final String DIALOG_DATE = "DialogDate";

    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_CODE_DATE = 0;

    private static final int REQUEST_CODE_CONTACT = 1;

    private static final int REQUEST_CODE_PHOTO = 2;

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
        mPhotoMomentDao = FileSystemPhotoMomentDao.getInstance(getActivity());
        mPhotoFile = mPhotoMomentDao.getPhotoFile(mMoment);
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

        mReportButton = (Button) v.findViewById(R.id.moment_send_report_button);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getMomentReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.moment_report_subject));

                // creating of list with available apps in the app
                i = Intent.createChooser(i, getString(R.string.send_report));

                startActivity(i);
            }
        });

        mPeopleButton = (Button) v.findViewById(R.id.moment_choose_people_button);

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mPeopleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CODE_CONTACT);
            }
        });

        if (!mMoment.getPeople().equals("")) {
            mPeopleButton.setText(mMoment.getPeople());
        }

        // Make sure that code below will work
        // pickContact.addCategory(Intent.CATEGORY_HOME);

        // if on the device there is any contact app - disable correspond button
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mPeopleButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.moment_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.moment_photo);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_CODE_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.moment_photo);
        updatePhotoView();

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
        } else if (requestCode == REQUEST_CODE_CONTACT && data != null) {
            Uri contactUri = data.getData();

            // Define fields, which must be the result of request.
            String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
            };

            // Request executing - contactUri perform function of WHERE clause
            Cursor c = getActivity().getContentResolver()
                  .query(contactUri, queryFields, null, null, null);
            try {
                 // Check receiving of results
                if (c.getCount() == 0) {
                   return;
                }
                // Extract first column - name of people
                c.moveToFirst();
                String suspect = c.getString(0);
                mMoment.setPeople(suspect);
                mPeopleButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_CODE_PHOTO) {
            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateDisplayButton.setText(mMoment.getDate().toLocaleString());
    }

    private String getMomentReport() {
        String favoriteString = null;
        if (mMoment.isFavorite()) {
            favoriteString = getString(R.string.moment_report_favorite);
        } else {
            favoriteString = getString(R.string.moment_report_not_favorite);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                mMoment.getDate()).toString();
        String people = mMoment.getPeople();
        if (people == null) {
            people = getString(R.string.moment_report_no_people);
        } else {
            people = getString(R.string.moment_report_people, people);
        }
        String report = getString(R.string.moment_report,
                mMoment.getTitle(), dateString, favoriteString, people);
        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}

