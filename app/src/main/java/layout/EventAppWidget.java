package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.jp.apps.weeklyreminder.ActivityMain;
import com.jp.apps.weeklyreminder.Event;
import com.jp.apps.weeklyreminder.EventDao;
import com.jp.apps.weeklyreminder.EventDaoImpl;
import com.jp.apps.weeklyreminder.Parameters;
import com.jp.apps.weeklyreminder.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        dateFormat.setLenient(false);

        String widgetText = getWidgetEvents(context);
        widgetText += "\n" +  dateFormat.format(new Date());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.event_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent = new Intent(context, ActivityMain.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener to the button
        views.setOnClickPendingIntent(R.id.appwidget_view, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static String getWidgetEvents(Context context) {
        String result = "";
        int counter = 0;
        EventDao eventDao = new EventDaoImpl(context);
        List<Event> eventList = eventDao.getAllSortedEvents();
        for (Event event: eventList) {
            if (event.isVisibleOnWidget()) {
                if (counter > 0) {
                    result += "\n";
                }
                result += event.getName();
                counter++;
            }
            if (counter >= Parameters.MAX_EVENTS_ON_APP_WIDGET) {
                break;
            }
        }
        return result;
    }
}

