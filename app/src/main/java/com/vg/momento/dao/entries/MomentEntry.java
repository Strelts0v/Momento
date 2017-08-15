package com.vg.momento.dao.entries;

import android.provider.BaseColumns;

/* Defines the table contents */
public abstract class MomentEntry implements BaseColumns {

    public static final String TABLE_NAME = "moments";

    public static final String COLUMN_NAME_ENTRY_ID = "moment_id";

    public static final String COLUMN_NAME_TITLE = "title";

    public static final String COLUMN_NAME_DATE = "date";

    public static final String COLUMN_NAME_IS_FAVORITE = "is_favorite";

    public static final String COLUMN_NAME_PEOPLE = "people";
}