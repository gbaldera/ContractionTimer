package com.ianhanniballake.contractiontimer;

import android.app.Application;
import android.preference.PreferenceManager;

import com.ianhanniballake.contractiontimer.strictmode.StrictModeController;
import com.ianhanniballake.contractiontimer.tagmanager.GtmManager;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpPostSender;
import org.acra.sender.ReportSender;

/**
 * Creates the Contraction Timer application, setting strict mode in debug mode
 */
@ReportsCrashes(formKey = "dFdXWHJ6SDRJREh2M0FRMFFqdFk2R1E6MQ")
public class ContractionTimerApplication extends Application {
    /**
     * Sets strict mode if we are in debug mode, init ACRA if we are not.
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        GtmManager.getInstance(this).init();
        if (BuildConfig.DEBUG) {
            StrictModeController.createInstance().setStrictMode();
        } else {
            ACRA.init(this);
            final ReportSender bugsenseReportSender = new HttpPostSender(
                    "http://www.bugsense.com/api/acra?api_key=6ebe60f4", null);
            ACRA.getErrorReporter().addReportSender(bugsenseReportSender);
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences_settings, false);
        super.onCreate();
    }
}