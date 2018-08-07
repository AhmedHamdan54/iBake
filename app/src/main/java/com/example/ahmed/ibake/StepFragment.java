package com.example.ahmed.ibake;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.example.ahmed.ibake.Recipes.Steps;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.example.ahmed.ibake.databinding.FragmentStepBinding;
import com.google.android.exoplayer2.util.Util;


import icepick.Icepick;
import icepick.State;

public class StepFragment extends Fragment {

    public static final String KEY_STEP_OBJECT = "stepObject";


    @State
    Steps step;

    @State
    long currentPosition = 0;

    private FragmentStepBinding binding;
    private SimpleExoPlayer simpleExoPlayer= null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Icepick.restoreInstanceState(this, savedInstanceState);

        binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()),
                R.layout.fragment_step, container, false);

        if(null != step.getVideoUrl() && !(TextUtils.isEmpty(step.getVideoUrl()))) {
            initializePlayer();
        } else {
            binding.expPlayer.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if(null != binding.tvShortDescriptionStep) {
            binding.tvShortDescriptionStep.setText(step.getShortDescription());
            binding.tvFullDescription.setText(step.getDescription());
        } else {

            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            getActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || simpleExoPlayer == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            simpleExoPlayer.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            simpleExoPlayer.release();
        }
    }
    public void onDetach() {
        super.onDetach();
        if (simpleExoPlayer != null){
            simpleExoPlayer.stop();
            simpleExoPlayer.release();


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null){
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }






    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(null != simpleExoPlayer) {
            simpleExoPlayer.setPlayWhenReady(true);
            currentPosition = simpleExoPlayer.getCurrentPosition();
        }

        Icepick.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);

    }

    public void setStep(Steps step) {
        this.step = step;
    }

    public void updateStep(Steps step) {

        if(null != binding.tvShortDescriptionStep) {
            binding.tvShortDescriptionStep.setText(step.getShortDescription());
            binding.tvFullDescription.setText(step.getDescription());
        }

        if(null == simpleExoPlayer) {
            initializePlayer();
        }
        simpleExoPlayer.setPlayWhenReady(false);


        if(null != step.getVideoUrl() && !(TextUtils.isEmpty(step.getVideoUrl()))) {

            binding.expPlayer.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(step.getVideoUrl());
            MediaSource mediaSource = buildMediaSource(uri);
            simpleExoPlayer.prepare(mediaSource, true, false);
            simpleExoPlayer.setPlayWhenReady(true);

        } else {
            binding.expPlayer.setVisibility(View.GONE);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void initializePlayer() {

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        binding.expPlayer.setPlayer(simpleExoPlayer);
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.seekTo(currentPosition);

        Uri uri = Uri.parse(step.getVideoUrl());
        MediaSource mediaSource = buildMediaSource(uri);
        simpleExoPlayer.prepare(mediaSource, true, false);
    }
}
