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
package org.mendybot.a.news.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import org.mendybot.a.news.data.NewsContract;
import org.mendybot.a.news.data.NewsPreferences;
import org.mendybot.a.news.utilities.FreestarNewsJsonUtils;
import org.mendybot.a.news.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class FreestarSyncTask {

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncNews(Context context) {

        try {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            URL newsListUrl = NetworkUtils.getListUrl(context);

            /* Use the URL to retrieve the JSON */
            String jsonNewsListResponse = NetworkUtils.getResponseFromHttpUrl(newsListUrl);

            /* Parse the JSON into a list of weather values */
            ContentValues[] idValues = FreestarNewsJsonUtils.getNewsIdsFromJson(context, jsonNewsListResponse);
            if (idValues != null && idValues.length > 0) {
                ContentResolver newsContentResolver = context.getContentResolver();

                ArrayList<ContentValues> newsValues = new ArrayList<ContentValues>();
                for (ContentValues value : idValues) {

                    URL newsItemUrl = NetworkUtils.getItemUrl(context, value.getAsInteger(NewsContract.NewsEntry._ID));
                    String jsonNewsItemResponse = NetworkUtils.getResponseFromHttpUrl(newsItemUrl);

                    ContentValues fullValue = FreestarNewsJsonUtils.getNewsItemsFromJson(context, jsonNewsItemResponse);
                    newsValues.add(fullValue);
                }

                ContentValues[] nValues = newsValues.toArray(new ContentValues[0]);
                newsContentResolver.bulkInsert(
                        NewsContract.NewsEntry.CONTENT_URI,
                        nValues);
            }

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }
}