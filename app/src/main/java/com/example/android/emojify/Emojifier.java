package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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


    public static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap bm){

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
            return null;

        }

        Frame frame = new Frame.Builder().setBitmap(bm).build();
        SparseArray<Face> faces = Emojifier.detector.detect(frame);

        if(faces.size() == 0) {

            Log.d("log_me", "No faces detected");
            return bm;

        }

        Bitmap bmOverlay = null;

        for (int i = 0; i < faces.size(); ++i) {

            bmOverlay = Emojifier.addBitmapToFace( //TODO transform all faces into a unique bitmap
                bm,
                Bitmap.createBitmap( BitmapFactory.decodeResource(Emojifier.context.getResources(), Emojifier.wichEmoji(faces.get(i))) ),
                faces.get(i)
            );

            /*for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }*/

        }

        return bmOverlay;

    }


    public static int wichEmoji(Face face){

        if(face == null)
            return 0;

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

        return imgSourceId;
    }


    public static void releaseFaceDetector(){

        if(Emojifier.detector != null) {

            Emojifier.detector.release();
            Emojifier.detector = null;

        }

    }



    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = 0.7f;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() * newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);

        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }

}
