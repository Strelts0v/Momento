package com.vg.momento.dao.implementations;

import android.content.Context;

import com.vg.momento.dao.interfaces.MomentDao;
import com.vg.momento.dao.utils.MomentDbHelper;
import com.vg.momento.model.Moment;
import java.util.List;
import java.util.UUID;

public class SQLiteMomentDao implements MomentDao {

    private static SQLiteMomentDao INSTANCE;

    private MomentDbHelper mMomentDbHelper;

    public static SQLiteMomentDao getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new SQLiteMomentDao(context);
        }
        return INSTANCE;
    }

    private SQLiteMomentDao(Context context){
        mMomentDbHelper = MomentDbHelper.getInstance(context);
    }

    @Override
    public void addMoment(Moment moment) {
        mMomentDbHelper.insertMoment(moment);
    }

    @Override
    public void deleteMoment(UUID id) {
        mMomentDbHelper.deleteMoment(id);
    }

    @Override
    public void updateMoment(UUID id, Moment updatedMoment) {
        mMomentDbHelper.updateMoment(id, updatedMoment);
    }

    @Override
    public Moment getMoment(UUID id) {
        return mMomentDbHelper.getMoment(id);
    }

    @Override
    public List<Moment> getAllMoments() {
        return mMomentDbHelper.getMoments();
    }

    @Override
    public void deleteAllMoments() {
        mMomentDbHelper.deleteAllMoments();
    }
}
