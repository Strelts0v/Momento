package com.vg.momento.dao.implementations;

import android.content.Context;
import android.os.Environment;

import com.vg.momento.dao.interfaces.PhotoMomentDao;
import com.vg.momento.model.Moment;

import java.io.File;

public class FileSystemPhotoMomentDao implements PhotoMomentDao {

    private static FileSystemPhotoMomentDao INSTANCE;

    private Context mContext;

    public static FileSystemPhotoMomentDao getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new FileSystemPhotoMomentDao(context);
        }
        return INSTANCE;
    }

    private FileSystemPhotoMomentDao(Context context){
        mContext = context;
    }

    @Override
    public File getPhotoFile(Moment moment) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, moment.getPhotoFilename());
    }
}
