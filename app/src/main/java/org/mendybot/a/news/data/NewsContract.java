package org.mendybot.a.news.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class NewsContract {
    public static final String CONTENT_AUTHORITY = "org.mendybot.a.news";
    public static final String PATH_NEWS = "news";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class NewsEntry implements BaseColumns {
        public static final String COLUMN_BY="by";
        public static final String COLUMN_DESCENDANTS="descendants";
        public static final String COLUMN_KIDS="kids";
        public static final String COLUMN_SCORE="score";
        public static final String COLUMN_TIME="time";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_TYPE="type";
        public static final String COLUMN_URL="url";
        public static final String COLUMN_DELETED="deleted";
        public static final String COLUMN_DEAD="dead";
        public static final String COLUMN_TEXT="text";
        public static final String COLUMN_PARENT="parent";
        public static final String COLUMN_POLL="poll";
        public static final String COLUMN_PARTS="parts";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NEWS)
                .build();
        public static final String TABLE_NAME = "freestar_news";

        public static Uri buildNewsUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static String getSqlSelectForNews() {
//            long normalizedUtcNow = FreestarDateUtils.normalizeDate(System.currentTimeMillis());
//            return NewsContract.NewsEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
            return NewsContract.NewsEntry._ID + " > 0";
        }

        public static String getSqlSortOrderTop() {
            return NewsContract.NewsEntry.COLUMN_SCORE + " ASC";
        }

        public static String getSqlSortOrderNewest() {
            return NewsContract.NewsEntry.COLUMN_TIME + " DESC";
        }
    }
}
