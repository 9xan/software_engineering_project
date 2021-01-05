package com.example.videoplayer;

import android.app.Activity;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MediaHandler {

    public static List<VideoView> createVideoViews(@NonNull Activity act, @NonNull List<Integer> ids, @NonNull List<String> videoPaths, @Nullable List<MediaController> viewControllers) {
        List<VideoView> destVideoViews = new ArrayList<>();
        int index = 0;
        for (Integer id : ids) {
            String myVideoPath = videoPaths.get(index);
            MediaController myController = null;
            if (viewControllers != null) {
                myController = viewControllers.get(index);
            }
            destVideoViews.add(createVideoView(act, id, myVideoPath, myController));
            index++;
        }
        return destVideoViews;
    }

    public static VideoView createVideoView(@NonNull Activity act, int id, @NonNull String videoPath, @Nullable MediaController controller) {
        VideoView myVideoView = act.findViewById(id);
        if (controller != null) {
            myVideoView.setMediaController(controller);
        }
        myVideoView.setVideoPath(videoPath);
        return myVideoView;
    }

    public static List<String> getFilesPathFromDirPath(@NonNull String dirPath) {
        File myDir = new File(dirPath);
        List<String> myPaths = new ArrayList<>();
        File[] files = myDir.listFiles();
        if (files != null) {
            for (File f : files) {
                myPaths.add(f.getPath());
            }
        }
        return myPaths;
    }

    public static List<String> getFilesNameFromDirPath(@NonNull String dirPath) {
        File myDir = new File(dirPath);
        List<String> myFilesName = new ArrayList<>();
        File[] files = myDir.listFiles();
        if (files != null) {
            for (File f : files) {
                myFilesName.add(f.getName());
            }
        }
        return myFilesName;
    }

    // TODO add flow control for different video duration


    public static void startVideoView(VideoView v) {
        v.start();
    }

    public static void startVideoViews(@NonNull List<VideoView> videoViews) {
        for (VideoView view : videoViews) {
            startVideoView(view);
        }
    }

    public static void stopVideoView(@NonNull VideoView v) {
        if (v.canPause()) {
            v.pause();
        }
    }

    public static void stopVideoViews(@NonNull List<VideoView> videoViews) {
        for (VideoView view : videoViews) {
            stopVideoView(view);
        }
    }

    public static void restartVideoView(@NonNull VideoView v) {
        if (v.canSeekBackward()) {
            v.seekTo(0);
        }
    }

    public static void restartVideoViews(@NonNull List<VideoView> videoViews) {
        for (VideoView view : videoViews) {
            restartVideoView(view);
        }
    }

    public static void seekVideoViewBackward(@NonNull VideoView v, int msTime) {
        if (v.canSeekBackward()) {
            v.seekTo(v.getCurrentPosition() - msTime);
        }
    }

    public static void seekVideoViewsBackward(@NonNull List<VideoView> v, int msTime) {
        for (VideoView view : v) {
            seekVideoViewBackward(view, msTime);
        }
    }

    public static void seekVideoViewForward(@NonNull VideoView v, int msTime) {
        if (v.canSeekForward()) {
            v.seekTo(v.getCurrentPosition() + msTime);
        }
    }

    public static void seekVideoViewsForward(@NonNull List<VideoView> v, int msTime) {
        for (VideoView view : v) {
            seekVideoViewForward(view, msTime);
        }
    }

    public static int getVideoViewCursorPosition(@NonNull VideoView v) {
        return v.getCurrentPosition();
    }

    public static List<Button> createButtons(@NonNull Activity act, @NonNull List<Integer> ids) {
        List<Button> elems = new ArrayList<>();
        for (int id : ids) {
            elems.add((Button) act.findViewById(id));
        }
        return elems;
    }

    public static <T> boolean addOrRemoveElement(@NonNull List<T> list, @NonNull T elem) {
        if (!list.contains(elem)) {
            list.add(elem);
            return true;
        } else {
            list.remove(elem);
            return false;
        }
    }

    public static boolean isInFormat(String filePath, String format) {
        String[] arr = filePath.split("\\.");
        return (arr[arr.length - 1].compareTo(format) == 0);
    }

    public static boolean areVideosPlaying(List<VideoView> videos) {
        boolean arePlaying = true;
        for (VideoView v : videos) {
            arePlaying &= v.isPlaying();
        }
        return arePlaying;
    }

}
