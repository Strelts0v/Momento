package com.vg.momento.dao.utils;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.vg.momento.dao.entries.MomentEntry;
import com.vg.momento.model.Moment;

import java.util.Date;
import java.util.UUID;

public class MomentCursorWrapper extends CursorWrapper {

    public MomentCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Moment getMoment(){
        String uuidStr = getString(getColumnIndex(MomentEntry.COLUMN_NAME_ENTRY_ID));
        String title = getString(getColumnIndex(MomentEntry.COLUMN_NAME_TITLE));
        long date = getLong(getColumnIndex(MomentEntry.COLUMN_NAME_DATE));
        boolean isFavorite = getInt(getColumnIndex(MomentEntry.COLUMN_NAME_IS_FAVORITE)) == 1;
        String people = getString(getColumnIndex(MomentEntry.COLUMN_NAME_PEOPLE));

        Moment moment = new Moment(UUID.fromString(uuidStr));
        moment.setTitle(title);
        moment.setDate(new Date(date));
        moment.setFavorite(isFavorite);
        moment.setPeople(people);

        return moment;
    }
}
