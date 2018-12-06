package com.example.namrathasubramanya.nanotale;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class NanotaleWidget extends AppWidgetProvider {
    public static final String _TAG = "nanotale_widget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, NanotaleRemoteViewService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.nanotale_viewer);

            rv.setRemoteAdapter(R.id.widget_stack_view, intent);

            rv.setEmptyView(R.id.widget_stack_view, R.id.widget_empty_stack);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_stack_view);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NanotaleWidget.class));
        if (appWidgetIds.length > 0) {
            for(int appWidgetId : appWidgetIds) {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_stack_view);
            }
        }
    }
}
