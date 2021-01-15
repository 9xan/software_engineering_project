package com.example.provaapp.useful_classes;


import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class Permissions {

    public static class PermissionDeniedException extends Exception {
        public PermissionDeniedException(String message) {
            super(message);
        }
    }

    ;

    // Ask permissions then run a lambda if allowed
    public static void runIfAllowed(AppCompatActivity context, String[] permissions, int requestCode, Runnable fun) throws PermissionDeniedException {
        // all the permissions in the array are checked, if at least one fails then fun.run() is not called
        Permissions.requestPermissions(context, permissions, requestCode);
        if (!areGranted(context, Arrays.asList(permissions))) {
            throw new PermissionDeniedException(Permissions.buildPermissionString(permissions));
        } else {
            fun.run();
        }
    }

    public static void requestPermissions(AppCompatActivity context, String[] permissions, int requestCode) {
        // smart requests: if already allowed don't ask for permissions again!
        if (!areGranted(context, Arrays.asList(permissions)))
            ActivityCompat.requestPermissions(context, permissions, requestCode);
    }

    public static boolean isGranted(AppCompatActivity context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean areGranted(AppCompatActivity context, List<String> permissions) {
        for (String permission : permissions) {
            if (!isGranted(context, permission))
                return false;
        }
        return true;
    }

    private static String buildPermissionString(String[] permissions) {
        String result = "";
        for (String permission : permissions) {
            result = result + "[ " + permission + " ] ";
        }
        return result;
    }
}