package com.feca.mface.core.facemakeup;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.widget.RecyclerView;

import com.feca.mface.core.facedetection.DetectedFaces;
import com.feca.mface.core.imaging.ColorDetector;
import com.feca.mface.core.imaging.Paths;

/**
 * Created by Bob on 2018/4/17.
 */

public class Eyestick implements FaceMakeup {
    private static final float A = 0.5f;
    private static final int LIPSTICK_DETECTING_THRESHOLD = 0x13;
    private int mUpColor,mLowColor,mCornerColor;
    private int left_radius;

    public Eyestick(int upcolor,int lowColor) {
        int newUpcolor = 0X20000000 + upcolor;
        int newLowcolor = 0X15000000 + lowColor;
        mUpColor = newUpcolor;
        mLowColor = newLowcolor;
    }


    // @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void makeup(Bitmap image, DetectedFaces.FaceShapeItem face) {
        //Path mouthBounds = Paths.toPolygon(face.mouth);
        //int lipColor = extractLipAverageColor(image, mouthBounds);
        //applyLipstick(image, mouthBounds, lipColor, mColor);

        Canvas canvas = new Canvas(image);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        p.setColor(mUpColor);
        p.setStrokeJoin(Paint.Join.ROUND);
        left_radius = (face.getLeftUpperEye()[5].y - face.getLeftUpperEye()[3].y)/2;
        if (left_radius != 0){
            BlurMaskFilter bmf = new BlurMaskFilter(left_radius, BlurMaskFilter.Blur.NORMAL);
            p.setMaskFilter(bmf);
        }
        canvas.drawPath(Paths.toCatmullRomCurve(face.getLeftUpperEye()), p);
        canvas.drawPath(Paths.toCatmullRomCurve(face.getRightUpperEye()), p);


        p.setColor(mLowColor);
        p.setStrokeJoin(Paint.Join.ROUND);
        left_radius = (face.getLeftUpperEye()[7].y - face.getLeftUpperEye()[1].y)/2;
        if (left_radius != 0){
            BlurMaskFilter bmf = new BlurMaskFilter(left_radius, BlurMaskFilter.Blur.NORMAL);
            p.setMaskFilter(bmf);
        }
        canvas.drawPath(Paths.toCatmullRomCurve(face.getLeftLowerEye()), p);
        canvas.drawPath(Paths.toCatmullRomCurve(face.getRightLowerEye()), p);

/*        p.setColor(mCornerColor);
        p.setStrokeJoin(Paint.Join.ROUND);
        left_radius = (face.getLeftUpperEye()[7].y - face.getLeftUpperEye()[1].y)/2;
        *//*bmf = new BlurMaskFilter(left_radius, BlurMaskFilter.Blur.NORMAL);
        p.setMaskFilter(bmf);*//*
        canvas.drawPath(Paths.toCatmullRomCurve(face.getLeftCorner()), p);
        canvas.drawPath(Paths.toCatmullRomCurve(face.getRightCorner()), p);*/

    }

    private int extractLipAverageColor(Bitmap image, Path mouthBounds) {
        //for test
        return 0xe09184;
    }

    private void applyLipstick(Bitmap image, Path mouthBounds, int lipAverageColor, int color) {
        Rect bounds = computeMouthBoundsInRect(mouthBounds);
        ColorDetector lipDetector = new ColorDetector.WeightedRGBDistanceDetector(lipAverageColor, LIPSTICK_DETECTING_THRESHOLD);
        Canvas canvas = new Canvas(image);
        Paint p = new Paint();
        //p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        p.setColor(mUpColor);

        for (int y = bounds.top; y < bounds.bottom; y++) {
            for (int x = bounds.left; x < bounds.right; x++) {
                int c = image.getPixel(x, y);
                boolean isLip = lipDetector.detectsColor(Color.red(c), Color.green(c), Color.blue(c));
                if (!isLip) {
                    continue;
                }
                //c = applyLipstick(c, lipAverageColor, color);
                canvas.drawPoint(x, y, p);
                //image.setPixel(x, y, c);
            }
        }

    }

    private int applyLipstick(int c, int lipAverageColor, int lipstickColor) {
        return (int) (lipstickColor * A + c * (1 - A));
    }

    private Rect computeMouthBoundsInRect(Path mouthBounds) {
        RectF extraBounds = new RectF();
        mouthBounds.computeBounds(extraBounds, true);
        Rect bounds = new Rect();
        extraBounds.roundOut(bounds);
        return bounds;
    }
}
