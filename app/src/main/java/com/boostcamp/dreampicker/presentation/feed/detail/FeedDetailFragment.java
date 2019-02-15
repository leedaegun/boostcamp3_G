package com.boostcamp.dreampicker.presentation.feed.detail;

import android.os.Bundle;
import android.view.View;

import com.boostcamp.dreampicker.R;
import com.boostcamp.dreampicker.databinding.FragmentFeedDetailImageBinding;
import com.boostcamp.dreampicker.presentation.BaseFragment;
import com.boostcamp.dreampicker.utils.GlideApp;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FeedDetailFragment extends BaseFragment<FragmentFeedDetailImageBinding> {

    private static final String ARGUMENT_IMAGEURL = "ARGUMENT_IMAGEURL";
    private String imageUrl;

    public FeedDetailFragment() {

    }

    static Fragment newInstance(@NonNull String imageUrl) {
        FeedDetailFragment fragment = new FeedDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_IMAGEURL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            imageUrl = savedInstanceState.getString(ARGUMENT_IMAGEURL);
        } else {
            final Bundle args = getArguments();
            if (args != null) {
                imageUrl = args.getString(ARGUMENT_IMAGEURL);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            GlideApp.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo)
                    .error(R.drawable.ic_broken_image_black)
                    .transform(new RoundedCorners(20))
                    .into(binding.ivFeedDetailImage);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_feed_detail_image;
    }
}
