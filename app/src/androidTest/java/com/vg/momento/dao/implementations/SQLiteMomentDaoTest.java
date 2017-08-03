package com.vg.momento.dao.implementations;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.vg.momento.dao.interfaces.MomentDao;
import com.vg.momento.model.Moment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SQLiteMomentDaoTest {

    private Context appContext;

    private MomentDao mMomentDao;

    public SQLiteMomentDaoTest(){
        appContext = InstrumentationRegistry.getTargetContext();
        mMomentDao = SQLiteMomentDao.getInstance(appContext);
    }

    @After
    @Before
    public void deleteAllMomentsTest() throws Exception {
        String errorMessage = "Expected size of result list equal to 0";
        mMomentDao.deleteAllMoments();
        assertEquals(errorMessage, 0, mMomentDao.getAllMoments().size());
    }

    @Test
    public void verifyAppContextTest() throws Exception {
        String errorMessage = "Android app context is not initialized";
        assertNotNull(errorMessage , appContext);
        errorMessage = "Android app context's package name is not correspond";
        assertEquals(errorMessage, "com.vg.momento", appContext.getPackageName());
    }

    @Test
    public void getInstanceTest() throws Exception {
        String errorMessage = "SQLiteMomentDao is not initialized";
        assertNotNull(errorMessage, mMomentDao);
    }

    @Test
    public void getAllMomentsTest(){
        Moment moment = new Moment();
        mMomentDao.addMoment(moment);
        moment = new Moment();
        mMomentDao.addMoment(moment);
        assertEquals(2, mMomentDao.getAllMoments().size());
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
        moment.setTitle("Test title");
        mMomentDao.addMoment(moment);
        mMomentDao.deleteMoment(moment.getId());
        String errorMessage = "After deleting moment object however exists in storage";
        Moment resultMoment = mMomentDao.getMoment(moment.getId());
        assertEquals(errorMessage, resultMoment.getTitle(), "");
        assertEquals(errorMessage, resultMoment.isFavorite(), false);
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
        String errorMessage = "Expected empty object but has been retrieved not actual object" +
                " after query for not existing object";
        Moment resultMoment = mMomentDao.getMoment(moment.getId());
        assertEquals(errorMessage, resultMoment.getTitle(), moment.getTitle());
        assertEquals(errorMessage, resultMoment.isFavorite(), moment.isFavorite());
    }
}