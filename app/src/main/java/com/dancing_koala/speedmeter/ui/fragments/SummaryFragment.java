package com.dancing_koala.speedmeter.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.database.access.TrackingSessionAccess;
import com.dancing_koala.speedmeter.helpers.Formatter;
import com.dancing_koala.speedmeter.models.TrackingSession;
import com.dancing_koala.speedmeter.ui.activities.SummaryActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Tracking session summary fragment
 */
public class SummaryFragment extends Fragment {

    private View mRootView;

    public SummaryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_summary, container, false);
        init();
        return mRootView;
    }

    private void init() {

        TrackingSession session;
        TrackingSessionAccess access = new TrackingSessionAccess(getActivity());
        access.openToRead();

        if (getActivity().getIntent().hasExtra(SummaryActivity.EXTRA_SESSION_ID)) {
            session = access.getTrackingSessionById(getActivity().getIntent().getStringExtra(SummaryActivity.EXTRA_SESSION_ID));
        } else {
            session = access.getLastTrackingSession();
        }

        access.close();

        if (session == null) {
            getActivity().finish();
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.FRANCE);

        ((TextView) mRootView.findViewById(R.id.txtv_session_start)).setText(dateFormat.format(new Date(session.getStartTime())));
        ((TextView) mRootView.findViewById(R.id.txtv_session_end)).setText(dateFormat.format(new Date(session.getEndTime())));
        ((TextView) mRootView.findViewById(R.id.txtv_session_duration)).setText(Formatter.getFormattedTime(session.getEndTime() - session.getStartTime()));
        ((TextView) mRootView.findViewById(R.id.txtv_session_distance)).setText(Formatter.getFormattedDistance(session.getDistance()));
        ((TextView) mRootView.findViewById(R.id.txtv_session_speed)).setText(Formatter.getKilometersPerHour(session.getAverageSpeed()));

    }
}
