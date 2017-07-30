package com.vg.momento.dao.interfaces;

import com.vg.momento.model.Moment;
import java.util.List;
import java.util.UUID;

public interface MomentDao {

    void addMoment(Moment moment);

    void deleteMoment(UUID id);

    void updateMoment(UUID id, Moment updatedMoment);

    Moment getMoment(UUID id);

    List<Moment> getAllMoments();

    void deleteAllMoments();
}
