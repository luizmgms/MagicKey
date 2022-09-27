package com.luiz.mg.magickey.db;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entries";

        public static final String COLUMN_NAME_KEY = "name_key";

        public static final String COLUMN_NAME_MAT_TAKE = "mat_take";
        public static final String COLUMN_NAME_NAME_TAKE = "name_take";
        public static final String COLUMN_NAME_DATE_TAKE = "date_take";
        public static final String COLUMN_NAME_TIME_TAKE = "time_take";

        public static final String COLUMN_NAME_MAT_BACK = "mat_back";
        public static final String COLUMN_NAME_NAME_BACK = "name_back";
        public static final String COLUMN_NAME_DATE_BACK = "date_back";
        public static final String COLUMN_NAME_TIME_BACK = "time_back";

    }

    public static class Feedkeys implements BaseColumns {
        public static final String TABLE_NAME = "keys";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DEPT = "dept";
        public static final String COLUMN_NAME_BORROWED = "borrowed";
    }

    public static class FeedUsers implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_MAT = "mat";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DEPT = "dept";
    }

}