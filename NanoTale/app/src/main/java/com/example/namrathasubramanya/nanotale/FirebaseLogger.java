package com.example.namrathasubramanya.nanotale;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class FirebaseLogger {
    public static final String NUM_NT_EVENT = "num_nt_event";
    public static final String MODIFY_NT_EVENT = "modify_nt_event";
    public static final String DEL_NT_EVENT = "del_nt_event";
    public static final String NEW_NT_EVENT = "new_nt_event";

    public static final String NUM_NT_PARAM = "num_nt_param";

    public static void logNumberOfNanoTales(FirebaseAnalytics analytics, int num) {
        Bundle bundle = new Bundle();
        bundle.putInt(NUM_NT_PARAM, num);
        analytics.logEvent(NUM_NT_EVENT, bundle);
    }

    public static void logModifyEvent(FirebaseAnalytics analytics) {
        Bundle bundle = new Bundle();
        analytics.logEvent(MODIFY_NT_EVENT, bundle);
    }

    public static void logNewNanoTale(FirebaseAnalytics analytics) {
        Bundle bundle = new Bundle();
        analytics.logEvent(NEW_NT_EVENT, bundle);
    }

    public static void logDeletedNanoTale(FirebaseAnalytics analytics) {
        Bundle bundle = new Bundle();
        analytics.logEvent(DEL_NT_EVENT, bundle);
    }
}
