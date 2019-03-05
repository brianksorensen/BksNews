package org.mendybot.a.news;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsAdapterViewHolder> {
    private final Context mContext;
    private final NewsAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;

    public NewsAdapter(@NonNull Context context, NewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public interface NewsAdapterOnClickHandler {
        void onClick(long date);
    }

    @Override
    public NewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int layoutId = R.layout.activity_articles;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new NewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapterViewHolder newsAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        int id = mCursor.getInt(MainActivity.INDEX_NEWS_ID);
//        int id = mCursor.getInt(MainActivity.INDEX)
        int viewType = getItemViewType(position);
//        int imageId =
//        newsAdapterViewHolder.iconView.setImageResource()
        newsAdapterViewHolder.itemView.setId(id);

    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class NewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView titleView;

        public NewsAdapterViewHolder(@NonNull View view) {
            super(view);
            titleView = view.findViewById(R.id.news_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_NEWS_ID);
            mClickHandler.onClick(dateInMillis);

        }
    }
}
