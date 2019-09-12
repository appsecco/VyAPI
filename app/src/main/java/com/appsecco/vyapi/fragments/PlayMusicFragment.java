package com.appsecco.vyapi.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appsecco.vyapi.R;
import com.appsecco.vyapi.service.PlayMusicService;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayMusicFragment extends Fragment {

    private Button btn_play_music;
    private Button btn_stop_music;
    private Intent service;

    public PlayMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_music, container, false);

        btn_play_music = (Button) view.findViewById(R.id.button_play_music);
        btn_stop_music = (Button) view.findViewById(R.id.button_stop_music);

        btn_play_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service = new Intent(getActivity(), PlayMusicService.class);
                getContext().startService(service);
            }
        });

        btn_stop_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent service = new Intent(getActivity(), PlayMusicService.class);
                service = new Intent(getActivity(), PlayMusicService.class);
                getContext().startService(service);
                getContext().stopService(service);
            }
        });

        return view;
    }

}
