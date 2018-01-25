package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by Heron Sanches on 2018-01-23.
 */

public final class Emojifier {

    private static FaceDetector detector;
    private static final double EYES_OPEN_THRESHOLD = 0.71; //TODO search the appropriate
    private static final double SMILE = 0.59; //TODO search the appropriate

    private static boolean isSmile;
    private static boolean isEyeRightOpen;
    private static boolean isEyeLeftOpen;
    private static Context context;


    public static void detectFaces(Context context, Bitmap bm){

        if(Emojifier.detector == null) {

            Emojifier.context = context;

            Emojifier.detector = new FaceDetector.Builder(Emojifier.context)
                    .setTrackingEnabled(false)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .build();

        }

        if(!Emojifier.detector.isOperational()){

            Log.d("log_me", "!detector.isOperational()");
            return;

        }

        Frame frame = new Frame.Builder().setBitmap(bm).build();
        SparseArray<Face> faces = Emojifier.detector.detect(frame);
        Log.d("log_me", "Number of faces detected: "+faces.size());

        if(faces.size() == 0)
            Log.d("log_me", "No faces detected");

        for (int i = 0; i < faces.size(); ++i) {

            Emojifier.wichEmoji(faces.valueAt(i));

            /*for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }*/

        }


    }


    public static void wichEmoji(Face face){

        Log.d("log_me", "face.getPosition().toString(): "+face.getPosition().toString());
        Log.d("log_me", "face.getIsLeftEyeOpenProbability(): "+face.getIsLeftEyeOpenProbability());
        Log.d("log_me", "face.getIsRightEyeOpenProbability(): "+face.getIsRightEyeOpenProbability());
        Log.d("log_me", "face.getIsSmilingProbability(): "+face.getIsSmilingProbability());

        Emojifier.isEyeLeftOpen = face.getIsLeftEyeOpenProbability() >= Emojifier.EYES_OPEN_THRESHOLD ? true : false;
        Emojifier.isEyeRightOpen = face.getIsRightEyeOpenProbability() >= Emojifier.EYES_OPEN_THRESHOLD ? true : false;
        Emojifier.isSmile = face.getIsSmilingProbability() >= Emojifier.SMILE ? true : false;

        int imgSourceId = 0;

        if(Emojifier.isEyeLeftOpen == true && Emojifier.isEyeRightOpen == true && Emojifier.isSmile == true)
            imgSourceId = R.drawable.smile;
        else if(Emojifier.isEyeLeftOpen == true && Emojifier.isEyeRightOpen == false && Emojifier.isSmile == false)
            imgSourceId = R.drawable.rightwinkfrown;
        else if(Emojifier.isEyeLeftOpen == false && Emojifier.isEyeRightOpen == false && Emojifier.isSmile == false)
            imgSourceId = R.drawable.closed_frown;
        else if(Emojifier.isEyeLeftOpen == false && Emojifier.isEyeRightOpen == false && Emojifier.isSmile == true)
            imgSourceId = R.drawable.closed_smile;
        else if(Emojifier.isEyeLeftOpen == true && Emojifier.isEyeRightOpen == true && Emojifier.isSmile == false)
            imgSourceId = R.drawable.frown;
        else if(Emojifier.isEyeLeftOpen == false && Emojifier.isEyeRightOpen == true && Emojifier.isSmile == true)
            imgSourceId = R.drawable.leftwink;
        else if(Emojifier.isEyeLeftOpen == false && Emojifier.isEyeRightOpen == true && Emojifier.isSmile == false)
            imgSourceId = R.drawable.leftwinkfrown;
        else if(Emojifier.isEyeLeftOpen == true && Emojifier.isEyeRightOpen == false && Emojifier.isSmile == true)
            imgSourceId = R.drawable.rightwink;

        Log.d("log_me", "emoji: "+Emojifier.context.getResources().getResourceName(imgSourceId));

    }


    public static void releaseFaceDetector(){

        if(Emojifier.detector != null) {

            Emojifier.detector.release();
            Emojifier.detector = null;

        }

    }


}
