package com.example.lee.footprints.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lee.footprints.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {


    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_timeline, container, false);
    }

    public static TimelineFragment newInstance() {

        Bundle args = new Bundle();

        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
