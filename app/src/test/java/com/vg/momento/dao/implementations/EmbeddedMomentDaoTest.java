package com.vg.momento.dao.implementations;

import com.vg.momento.dao.interfaces.MomentDao;
import com.vg.momento.model.Moment;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmbeddedMomentDaoTest {

    private MomentDao mMomentDao = EmbeddedMomentDao.getInstance();

    @Test
    public void getInstanceTest() throws Exception {
        assertNotNull(mMomentDao);
    }

    @Test
    public void addMomentTest() throws Exception {
        Moment moment = new Moment();
        mMomentDao.addMoment(moment);
        String errorMessage = "Added moment object is not correspond to retrieved moment object";
        assertEquals(errorMessage, moment, mMomentDao.getMoment(moment.getId()));
    }

    @Test
    public void deleteMomentTest() throws Exception {
        Moment moment = new Moment();
        mMomentDao.addMoment(moment);
        mMomentDao.deleteMoment(moment.getId());
        String errorMessage = "After deleting moment object however exists in storage";
        assertNull(errorMessage, mMomentDao.getMoment(moment.getId()));
    }

    @Test
    public void updateMomentTest() throws Exception {
        Moment moment = new Moment();
        mMomentDao.addMoment(moment);
        moment.setTitle("Test title");
        mMomentDao.updateMoment(moment.getId(), moment);
        String errorMessage = "After updating moment object however" +
                " doesn't correspond to updated moment object";
        assertEquals(errorMessage, moment, mMomentDao.getMoment(moment.getId()));
    }

    @Test
    public void getNotExistedMomentTest() throws Exception {
        Moment moment = new Moment();
        String errorMessage = "Expected null reference but has been retrieved object" +
                " after query for not existing object";
        assertNull(errorMessage, mMomentDao.getMoment(moment.getId()));
    }

    @After
    public void clearStorage(){
        mMomentDao.deleteAllMoments();
    }
}