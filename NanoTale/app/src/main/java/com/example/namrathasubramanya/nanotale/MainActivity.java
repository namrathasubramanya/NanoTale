package com.example.namrathasubramanya.nanotale;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NanotaleAdapter.DisplayUpdateNtList {
    private static final String _TAG = "main_activity";
    private static final int CURSOR_LOADER_TAG = 9;

    public static final String NEW_NT_SAVED_TAG = "new_nanotale";

    public static final String BUILD_NEW_NANOTALE = "build_new_nt";

    private NanotaleAdapter mAdapter;
    private NTCursorLoaderCallback mCallback;

    @BindView(R.id.saved_nt_list)
    public RecyclerView savedNTList;

    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mAnalytics;

    public class NTCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private Context mContext;

        public NTCursorLoaderCallback(Context context) {
            mContext = context;
        }

        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    ModelNanotale.buildQueryAllUri(),
                    null,
                    null,
                    null,
                    NanotaleBase.NanoTale._ID + " DESC");
            return cursorLoader;
        }


        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            TextView noSavedNt = (TextView) findViewById(R.id.no_saved_nt);
            if (data == null || data.getCount() == 0) {
                noSavedNt.setVisibility(View.VISIBLE);
            } else {
                noSavedNt.setVisibility(View.GONE);
                FirebaseLogger.logNumberOfNanoTales(mAnalytics, data.getCount());
            }
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
            mAdapter.notifyDataSetChanged();
            getSupportLoaderManager().initLoader(CURSOR_LOADER_TAG, null, this);
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAnalytics = FirebaseAnalytics.getInstance(this);

        mCallback = new NTCursorLoaderCallback(this);
        initRecyclerView();
        Intent intent = getIntent();
        if (intent.getBooleanExtra(NEW_NT_SAVED_TAG, false)) {
            getSupportLoaderManager().restartLoader(CURSOR_LOADER_TAG, null, mCallback);
        } else {
            getSupportLoaderManager().initLoader(CURSOR_LOADER_TAG, null, mCallback);
        }
        savedNTList.setAdapter(mAdapter);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager;
        if (isTablet(this) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }
        savedNTList.setLayoutManager(layoutManager);

        mAdapter = new NanotaleAdapter(this, this);
        // savedNTList.setLayoutParams(params);
    }

    public void writeNanoTale(View v) {
        if(mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            buildNanoTale();
        }
    }

    @Override
    public void onNtListUpdate() {
        getSupportLoaderManager().restartLoader(CURSOR_LOADER_TAG, null, mCallback);
    }

    private void buildNanoTale() {
        Intent intent = new Intent(MainActivity.this, BuildNanotale.class);
        intent.setAction(BUILD_NEW_NANOTALE);
        startActivity(intent);
    }


}

