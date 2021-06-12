package com.daimajia.slider.library.Animations;

import android.view.View;

import com.daimajia.slider.library.R;

/**
 * A demo class to show how to use {@link BaseAnimationInterface}
 * to make  your custom animation in {@link com.daimajia.slider.library.Tricks.ViewPagerEx.PageTransformer} action.
 */
public class NoDescriptionAnimation implements BaseAnimationInterface {

    @Override
    public void onPrepareCurrentItemLeaveScreen(View current) {
        View descriptionLayout = current.findViewById(R.id.description_layout);
        if (descriptionLayout != null) {
            descriptionLayout.setVisibility(View.GONE);
        }
    }

    /**
     * When next item is coming to show, let's hide the description layout.
     *
     * @param next
     */
    @Override
    public void onPrepareNextItemShowInScreen(View next) {
        View descriptionLayout = next.findViewById(R.id.description_layout);
        if (descriptionLayout != null) {
            descriptionLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onCurrentItemDisappear(View view) {

    }

    /**
     * When next item show in ViewPagerEx, let's make an animation to show the
     * description layout.
     *
     * @param view
     */
    @Override
    public void onNextItemAppear(View view) {

        View descriptionLayout = view.findViewById(R.id.description_layout);
        if (descriptionLayout != null) {
            descriptionLayout.setVisibility(View.GONE);
        }
    }
}
