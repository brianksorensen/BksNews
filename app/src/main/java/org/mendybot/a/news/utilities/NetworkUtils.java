package org.mendybot.a.news.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the news servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String NEWS_LIST_BASE_URL =
            "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";

    private static final String NEWS_ITEM_KEY = "{I}";
    private static final String NEWS_ITEM_BASE_URL = "https://hacker-news.firebaseio.com/v0/item/"+NEWS_ITEM_KEY+".json?print=pretty";

    /* The format we want our API to return */
    private static final String FORMAT= "json";
    /* The units we want our API to return */
    /* The format parameter allows us to designate whether we want JSON or XML from our API */
    private static final String FORMAT_PARAM = "mode";

    public static URL getListUrl(Context context) {
        return buildUrl(NEWS_LIST_BASE_URL);
    }

    public static URL getItemUrl(Context context, int id) {
        return buildUrl(NEWS_ITEM_BASE_URL.replace(NEWS_ITEM_KEY, Integer.toString(id)));
    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @return The Url to use to query the weather server.
     */
    private static URL buildUrl(String base) {
        Uri weatherQueryUri = Uri.parse(base).buildUpon()
                .appendQueryParameter(FORMAT_PARAM, FORMAT)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}