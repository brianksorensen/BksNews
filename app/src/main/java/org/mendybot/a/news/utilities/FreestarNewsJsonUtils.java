/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mendybot.a.news.utilities;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mendybot.a.news.data.NewsContract;
import org.mendybot.a.news.data.NewsPreferences;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class FreestarNewsJsonUtils {

    /* Weather information. Each day's forecast info is an element of the "list" array */
    private static final String OWM_LIST = "list";

    private static final String OWM_TITLE = "title";
    private static final String OWM_SCORE = "score";
    private static final String OWM_TEXT = "text";

    /* Max temperature for the day */
    private static final String OWM_NEWS = "news";
    private static final String OWM_ID = "id";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getNewsIdsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        JSONArray jsonNewsArray = new JSONArray(forecastJsonStr);
        ContentValues[] newsContentValues = new ContentValues[jsonNewsArray.length()];
        for (int i = 0; i < jsonNewsArray.length(); i++) {
            ContentValues newsValues = new ContentValues();
            newsValues.put(NewsContract.NewsEntry._ID, jsonNewsArray.getInt(i));
            newsContentValues[i] = newsValues;
        }
        return newsContentValues;
    }

    public static ContentValues getNewsItemsFromJson(Context context, String newsJsonStr)
            throws JSONException {

        JSONObject newsItem = new JSONObject(newsJsonStr);

        /*
         * OWM returns daily forecasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our weather.
         */
//        long now = System.currentTimeMillis();
//        long normalizedUtcStartDay = SunshineDateUtils.normalizeDate(now);

        long normalizedUtcStartDay = FreestarDateUtils.getNormalizedUtcDateForToday();


            int id;
            long dateTimeMillis;
            String title;
            int score;
            String text;

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
            dateTimeMillis = normalizedUtcStartDay + FreestarDateUtils.DAY_IN_MILLIS;

            title = newsItem.getString(OWM_TITLE);
            score = newsItem.getInt(OWM_SCORE);
            if (newsItem.has(OWM_TEXT)) {
                text = newsItem.getString(OWM_TEXT);
            } else {
                text = "";
            }

            /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
            id = newsItem.getInt(OWM_ID);

            /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary variable, temporary folder, temporary employee, or many
             * others, and is just a bad variable name.
             */

            ContentValues newsValues = new ContentValues();
            newsValues.put(NewsContract.NewsEntry.COLUMN_TIME, dateTimeMillis);
            newsValues.put(NewsContract.NewsEntry._ID, id);
            newsValues.put(NewsContract.NewsEntry.COLUMN_TITLE, title);
            newsValues.put(NewsContract.NewsEntry.COLUMN_SCORE, score);
            newsValues.put(NewsContract.NewsEntry.COLUMN_TEXT, text);


        return newsValues;
    }
}