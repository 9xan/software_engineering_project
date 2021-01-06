package com.example.provaapp.useful_classes;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class UiSettings {
    // WARNING: USES DEPRECATED CONSTANTS
    public static void hideSystemUI(AppCompatActivity context) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        context.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        /*| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION*/
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public static void showSystemUI(AppCompatActivity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setupFullscreenRotationHandling(AppCompatActivity context) {
        // activity view, it is manipulated to enable immersive mode in portrait and landscape device rotations
        View decorView = context.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                // TODO: The system bars are visible. Make any desired change
                // adjustments to your UI, such as showing the action bar or
                // other navigational controls.
                hideSystemUI(context);
            } else {
                // TODO: The system bars are NOT visible. Make any desired change
                // adjustments to your UI, such as hiding the action bar or
                // other navigational controls.
            }
        });
    }

}
