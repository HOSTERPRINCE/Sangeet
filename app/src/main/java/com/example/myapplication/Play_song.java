package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class Play_song extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Thread updateseekbar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (updateseekbar != null) {
            updateseekbar.interrupt();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        SeekBar seekbar = findViewById(R.id.seekBar2);
        ImageView play = findViewById(R.id.play);
        ImageView next = findViewById(R.id.next);
        ImageView previous = findViewById(R.id.previous);
        TextView view = findViewById(R.id.textView);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<File> songs = (ArrayList) bundle.getParcelableArrayList("songList");

        final int[] position = {intent.getIntExtra("position", 0)};
        Uri uri = Uri.parse(songs.get(position[0]).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();

        view.setSelected(true);
        view.setText(songs.get(position[0]).getName());
        seekbar.setMax(mediaPlayer.getDuration());

        // Update SeekBar in a separate thread
        updateseekbar = new Thread() {
            public void run() {
                int currentposition;
                try {
                    while ((currentposition = mediaPlayer.getCurrentPosition()) < mediaPlayer.getDuration()) {
                        seekbar.setProgress(currentposition);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateseekbar.start();


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Play/Pause Button Click Listener
        play.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                play.setImageResource(R.drawable.play);
                mediaPlayer.pause();
            } else {
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
        });

        // Previous Button Click Listener
        previous.setOnClickListener(v -> {
            changeSong(songs, position, -1, seekbar, view, play);
        });

        // Next Button Click Listener
        next.setOnClickListener(v -> {
            changeSong(songs, position, 1, seekbar, view, play);
        });

        // Set OnCompletionListener to automatically go to the next song
        mediaPlayer.setOnCompletionListener(mp -> {
            changeSong(songs, position, 1, seekbar, view, play);
        });
    }

    // Helper function to change song
    private void changeSong(ArrayList<File> songs, int[] position, int change, SeekBar seekbar, TextView view, ImageView play) {
        mediaPlayer.stop();
        mediaPlayer.release();

        position[0] = (position[0] + change + songs.size()) % songs.size();
        Uri uri = Uri.parse(songs.get(position[0]).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        play.setImageResource(R.drawable.pause);
        seekbar.setMax(mediaPlayer.getDuration());
        view.setText(songs.get(position[0]).getName());

        // Restart the SeekBar updater thread
        updateseekbar = new Thread() {
            public void run() {
                int currentposition;
                try {
                    while ((currentposition = mediaPlayer.getCurrentPosition()) < mediaPlayer.getDuration()) {
                        seekbar.setProgress(currentposition);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateseekbar.start();

        // Set the listener again to move to the next song when the current song finishes
        mediaPlayer.setOnCompletionListener(mp -> {
            changeSong(songs, position, 1, seekbar, view, play);
        });
    }
}