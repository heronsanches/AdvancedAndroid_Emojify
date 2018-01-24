package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by Heron Sanches on 2018-01-23.
 */

public final class Emojifier {

    private static FaceDetector detector;


    public static void detectFaces(Context context, Bitmap bm){

        if(Emojifier.detector == null)
            Emojifier.detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        if(!detector.isOperational()){

            Log.d("log_me", "!detector.isOperational()");
            return;

        }

        Frame frame = new Frame.Builder().setBitmap(bm).build();
        SparseArray<Face> faces = detector.detect(frame);
        Log.d("log_me", "Number of faces detected: "+faces.size());

        /*for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }
        }*/


    }


    public static void releaseFaceDetector(){

        if(Emojifier.detector != null)
            Emojifier.detector.release();

    }


}
