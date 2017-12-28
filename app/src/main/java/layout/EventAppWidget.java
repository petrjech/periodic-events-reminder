package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;

import com.jp.apps.weeklyreminder.ActivityMain;
import com.jp.apps.weeklyreminder.Event;
import com.jp.apps.weeklyreminder.EventDao;
import com.jp.apps.weeklyreminder.EventDaoImpl;
import com.jp.apps.weeklyreminder.Parameters;
import com.jp.apps.weeklyreminder.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class EventAppWidget extends AppWidgetProvider {

    public static String UPDATE_WIDGET_ACTION = "UPDATE_EVENT_LIST_WIDGET";

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Spannable widgetText = getWidgetContent(context);

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
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
        if(UPDATE_WIDGET_ACTION.equals(intent.getAction())){
            Bundle extras = intent.getExtras();
            if(extras!=null) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisAppWidget = new ComponentName(context.getPackageName(), EventAppWidget.class.getName());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

                onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }
    }

    private Spannable getWidgetContent(Context context) {
        String resultText = "";
        @SuppressWarnings("deprecation") ForegroundColorSpan red = new ForegroundColorSpan(context.getResources().getColor(R.color.colorWidgetThreshold3));
        @SuppressWarnings("deprecation") ForegroundColorSpan yellow = new ForegroundColorSpan(context.getResources().getColor(R.color.colorWidgetThreshold2));
        @SuppressWarnings("deprecation") ForegroundColorSpan green = new ForegroundColorSpan(context.getResources().getColor(R.color.colorWidgetThreshold1));
        List<Event> eventsToShow = getEventsToShow(context);
        int redStart = 0;
        int redEnd = 0;
        int yellowEnd;
        boolean isRed = true;
        boolean first = true;
        for (Event event : eventsToShow) {
            if (isRed && event.getEventApproach() < Parameters.EVENT_APPROACHING_THRESHOLD2) {
                isRed = false;
                redEnd = resultText.length();
            }
            if (first) {
                first = false;
            } else {
                resultText += "\n";
            }
            resultText += trimToMaxLength(event.getName());
        }
        yellowEnd = resultText.length();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        dateFormat.setLenient(false);
        resultText += "\n" +  dateFormat.format(new Date());


        SpannableString result = new SpannableString(resultText);
        result.setSpan(red, redStart, redEnd, SPAN_INCLUSIVE_INCLUSIVE);
        result.setSpan(yellow, redEnd, yellowEnd, SPAN_EXCLUSIVE_INCLUSIVE);
        result.setSpan(green, yellowEnd, result.length(), SPAN_EXCLUSIVE_INCLUSIVE);
        return result;
    }

    private List<Event> getEventsToShow(Context context) {
        List<Event> eventsToShow = new ArrayList<>();
        EventDao eventDao = new EventDaoImpl(context);
        List<Event> eventList = eventDao.getAllSortedEvents();
        int counter = 1;
        for (Event event: eventList) {
            if (Parameters.EVENT_APPROACHING_THRESHOLD1 > event.getEventApproach()) {
                break;
            }
            if (event.isVisibleOnWidget()) {
                eventsToShow.add(event);
                counter += 1;
            }
            if (counter > Parameters.WIDGET_MAX_LINES) {
                break;
            }
        }
        return eventsToShow;
    }

    private String trimToMaxLength(String line) {
        return line.length() > Parameters.WIDGET_LINE_MAX_LENGTH ? line.substring(0, Parameters.WIDGET_LINE_MAX_LENGTH + 1) : line;
    }
}

