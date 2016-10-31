package com.dancing_koala.speedmeter.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.database.access.TrackingSessionAccess;
import com.dancing_koala.speedmeter.models.TrackingSession;
import com.dancing_koala.speedmeter.ui.activities.SummaryActivity;

/**
 * Tracking session summary fragment
 */
public class SummaryFragment extends Fragment {

    private View rootView;

    public SummaryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        init();
        return rootView;
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


        ((TextView) rootView.findViewById(R.id.txtv_session_start)).setText("" + session.getStartTime());
        ((TextView) rootView.findViewById(R.id.txtv_session_end)).setText("" + session.getEndTime());
        ((TextView) rootView.findViewById(R.id.txtv_session_duration)).setText("" + (session.getEndTime() - session.getStartTime()));
        ((TextView) rootView.findViewById(R.id.txtv_session_distance)).setText("" + session.getDistance());
        ((TextView) rootView.findViewById(R.id.txtv_session_speed)).setText("" + session.getAverageSpeed());

    }
}
