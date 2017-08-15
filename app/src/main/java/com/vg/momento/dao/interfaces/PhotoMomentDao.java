package com.vg.momento.dao.interfaces;

import com.vg.momento.model.Moment;

import java.io.File;

public interface PhotoMomentDao {

    File getPhotoFile(Moment moment);
}
