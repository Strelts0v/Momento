package com.vg.momento.dao.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vg.momento.dao.entries.MomentEntry;
import com.vg.momento.model.Moment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MomentDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;

    public static final String DATABASE_NAME = "momento.db";

    private static MomentDbHelper INSTANCE;

    private SQLiteDatabase mDatabase;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MomentEntry.TABLE_NAME + " (" +
                    MomentEntry._ID + " INTEGER PRIMARY KEY, " +
                    MomentEntry.COLUMN_NAME_ENTRY_ID + " TEXT, " +
                    MomentEntry.COLUMN_NAME_TITLE + " TEXT, " +
                    MomentEntry.COLUMN_NAME_DATE + " BIGINT, " +
                    MomentEntry.COLUMN_NAME_IS_FAVORITE + " INTEGER, " +
                    MomentEntry.COLUMN_NAME_PEOPLE + " TEXT " +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MomentEntry.TABLE_NAME;

    public static MomentDbHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new MomentDbHelper(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private MomentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = this.getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertMoment(Moment moment){
        ContentValues values = getContentValues(moment);
        long id = mDatabase.insert(MomentEntry.TABLE_NAME, null, values);
        return id;
    }

    public void deleteMoment(UUID id){
        // Define 'where' part of query.
        String selection = MomentEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id.toString()) };
        // Issue SQL statement.
        mDatabase.delete(MomentEntry.TABLE_NAME, selection, selectionArgs);

    }

    public void deleteAllMoments(){
        // Issue SQL statement.
        mDatabase.delete(MomentEntry.TABLE_NAME, null, null);

    }

    public int updateMoment(UUID id, Moment moment){
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MomentEntry.COLUMN_NAME_TITLE, moment.getTitle());
        values.put(MomentEntry.COLUMN_NAME_DATE, moment.getDate().getTime());
        values.put(MomentEntry.COLUMN_NAME_IS_FAVORITE, moment.isFavorite() ? 1 : 0);
        values.put(MomentEntry.COLUMN_NAME_PEOPLE, moment.getPeople());

        // Which row to update, based on the ID
        String selection = MomentEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = mDatabase.update(
                MomentEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return count;
    }

    public Moment getMoment(UUID id){
        String whereClause = MomentEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String whereArgs[] = { id.toString() };
        MomentCursorWrapper cursor = queryMoments(whereClause, whereArgs);
        List<Moment> moments = parseMoments(cursor);
        if(moments.size() == 0){
            return new Moment();
        }
        return moments.get(0);
    }

    public List<Moment> getMoments() {
        MomentCursorWrapper cursor = queryMoments(null, null);
        return parseMoments(cursor);
    }

    private MomentCursorWrapper queryMoments(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MomentEntry.TABLE_NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new MomentCursorWrapper(cursor);
    }

    private ContentValues getContentValues(Moment moment){
        ContentValues values = new ContentValues();
        values.put(MomentEntry.COLUMN_NAME_ENTRY_ID, moment.getId().toString());
        values.put(MomentEntry.COLUMN_NAME_TITLE , moment.getTitle());
        values.put(MomentEntry.COLUMN_NAME_DATE, moment.getDate().getTime());
        values.put(MomentEntry.COLUMN_NAME_IS_FAVORITE, moment.isFavorite() ? 1 : 0);
        values.put(MomentEntry.COLUMN_NAME_PEOPLE, moment.getPeople());

        return values;
    }

    private List<Moment> parseMoments(MomentCursorWrapper cursor){
        List<Moment> moments = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                moments.add(cursor.getMoment());
                cursor.moveToNext();
            }
        } finally {
            cursor.close(); // never forger to close cursor
        }
        return moments;
    }
}
