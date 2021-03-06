package com.ianhanniballake.contractiontimer.notification;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ianhanniballake.contractiontimer.BuildConfig;
import com.ianhanniballake.contractiontimer.R;
import com.ianhanniballake.contractiontimer.provider.ContractionContract;

/**
 * Google Now's 'Note to self' only calls startActivity so we redirect to our service to run the update in the
 * background
 */
public class NoteNoDisplayActivity extends Activity {
    private final static String TAG = NoteNoDisplayActivity.class.getSimpleName();

    /**
     * Ensures that the NoteTransparentActivity is only enabled when there is at least one contraction
     *
     * @param context Context to be used to query the ContentProvider and enable/disable this activity
     */
    public static void checkServiceState(final Context context) {
        new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onQueryComplete(final int token, final Object cookie, final Cursor cursor) {
                boolean hasContractions = cursor != null && cursor.moveToFirst();
                if (BuildConfig.DEBUG)
                    Log.d(TAG, (hasContractions ? "Has" : "No") + " contractions");
                int state = hasContractions ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                if (cursor != null) {
                    cursor.close();
                }
                PackageManager packageManager = context.getPackageManager();
                ComponentName componentName = new ComponentName(context, NoteNoDisplayActivity.class);
                packageManager.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP);
            }
        }.startQuery(0, null, ContractionContract.Contractions.CONTENT_URI, null, null, null, null);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(getIntent());
        String note = remoteInput != null ? remoteInput.getCharSequence(Intent.EXTRA_TEXT).toString() :
                getIntent().getStringExtra(Intent.EXTRA_TEXT);
        Intent serviceIntent = new Intent(this, NoteIntentService.class);
        serviceIntent.setAction(getIntent().getAction());
        serviceIntent.putExtra(Intent.EXTRA_TEXT, note);
        startService(serviceIntent);
        if (!TextUtils.isEmpty(note)) {
            Toast.makeText(this, R.string.saving_note, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
