package com.vg.momento.dao.implementations;

import com.vg.momento.dao.interfaces.MomentDao;
import com.vg.momento.model.Moment;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmbeddedMomentDao implements MomentDao {

    private static MomentDao sMomentDao;

    private Map<UUID, Moment> mMomentsMap;

    private final static int INITIAL_SIZE = 30;

    private EmbeddedMomentDao(){
        mMomentsMap = new LinkedHashMap<>(INITIAL_SIZE);
        Moment moment;
        for(int i = 0; i < INITIAL_SIZE; i++){
            moment = new Moment();
            moment.setTitle("Moment #" + (i + 1));
            moment.setFavorite(i % 2 == 0);
            mMomentsMap.put(moment.getId(), moment);
        }
    }

    public static MomentDao getInstance(){
        if(sMomentDao == null){
            sMomentDao = new EmbeddedMomentDao();
        }
        return sMomentDao;
    }

    @Override
    public void addMoment(Moment moment) {
        mMomentsMap.put(moment.getId(), moment);
    }

    @Override
    public void deleteMoment(UUID id) {
        mMomentsMap.remove(id);
    }

    @Override
    public void updateMoment(UUID id, Moment updatedMoment) {
        Moment oldMoment = mMomentsMap.get(id);
        oldMoment.setTitle(updatedMoment.getTitle());
        oldMoment.setDate(updatedMoment.getDate());
        oldMoment.setFavorite(updatedMoment.isFavorite());
    }

    @Override
    public Moment getMoment(UUID id) {
        return mMomentsMap.get(id);
    }

    @Override
    public List<Moment> getAllMoments() {
        return new ArrayList<>(mMomentsMap.values());
    }

    @Override
    public void deleteAllMoments(){
        mMomentsMap.clear();
    }
}
