package org.mendybot.a.news;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.mendybot.a.news.data.NewsContract;
import org.mendybot.a.news.sync.FreestarSyncUtils;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
                   NewsAdapter.NewsAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();
    private static final int ID_NEWS_LOADER = 2166;

    public static final String[] MAIN_FORECAST_PROJECTION = {
            NewsContract.NewsEntry._ID,
            NewsContract.NewsEntry.COLUMN_TIME,
            NewsContract.NewsEntry.COLUMN_TITLE,
            NewsContract.NewsEntry.COLUMN_SCORE,
            NewsContract.NewsEntry.COLUMN_TEXT,
    };

    // indices of values
    public static final int INDEX_NEWS_ID = 0;
    public static final int INDEX_NEWS_TIME = 1;
    public static final int INDEX_NEWS_TITLE = 2;
    public static final int INDEX_NEWS_SCORE = 3;
    public static final int INDEX_NEWS_TEXT = 4;

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private NewsAdapter mNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        getSupportActionBar().setElevation(0f);

        mRecyclerView = findViewById(R.id.recyclerview_articles);
        mLoadingIndicator = findViewById(R.id.news_loading_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mNewsAdapter = new NewsAdapter(this, this);
        mRecyclerView.setAdapter(mNewsAdapter);

        showLoading();
        getSupportLoaderManager().initLoader(ID_NEWS_LOADER, null, this);
        FreestarSyncUtils.initialize(this);

        String[] dummyWeatherData1 = {
                "The next version of Safari will let users blo...",
                "Google Launches Sidewalk Labs",
                "Frag: A Quake3 engine in Haskell",
                "Kaspersky Lab cybersecurity firm is hacked",
                "Unreal Engine 4.8 Released",
                "How We Designed for Performance and Sc...",
                "Generating Magic cards using deep, recurs...",
                "Show HN: Clojure by Example",
                "Duolingo Raises $45M, Now Valued at $47...",
                "Moog Music Gives Employees More Control",
        };

    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();
        switch(selected) {
            case R.id.top_news:
                Toast.makeText((Context)this, "top", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.newest_news:
                Toast.makeText((Context)this, "newest", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.ask:
                Toast.makeText((Context)this, "ask", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(long id) {
        Intent newsDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = NewsContract.NewsEntry.buildNewsUriWithId(id);
        newsDetailIntent.setData(uriForDateClicked);
        startActivity(newsDetailIntent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        switch (loaderId) {

            case ID_NEWS_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri forecastQueryUri = NewsContract.NewsEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = NewsContract.NewsEntry.getSqlSortOrderTop();
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                String selection = NewsContract.NewsEntry.getSqlSelectForNews();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
