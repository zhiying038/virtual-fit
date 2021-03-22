package com.yohzhiying.testposeestimation;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ProcessImage {
    public ProcessImage() {}

    public Bitmap getOutfitArea(Bitmap bitmap, int sensitivity, int[] color) {
        // Convert bitmap to matrix
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        // Perform Gaussian blur
        Imgproc.GaussianBlur(mat, mat, new Size(5,5), 0);
        Mat hsv = new Mat();
        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);

        if (color.length != 3) {
            return bitmap;
        } else {
            int c1 = color[0];
            int c2 = color[1];
            int c3 = color[2];

            Scalar lower = new Scalar(0, 0, 255 - sensitivity);
            Scalar upper = new Scalar(255, sensitivity, 255);
            Mat mask = new Mat();
            Core.inRange(hsv, lower, upper, mask);
            Mat res = new Mat();
            Core.bitwise_and(mat, mat, res, mask);
            Mat mask2 = new Mat();
            Core.bitwise_not(mask, mask2);
            Mat result = new Mat();
            Core.bitwise_and(mat, mat, result, mask2);

            result = cropROI(result);
            Bitmap resultBitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, resultBitmap);
            return resultBitmap;
        }
    }

    private Mat cropROI(Mat mat) {
        double largestArea = 0;
        Rect boundingShape = new Rect();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat threshold = new Mat();

        Imgproc.cvtColor(mat, threshold, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(threshold, threshold, 125, 155, Imgproc.THRESH_BINARY);

        Imgproc.findContours(threshold, contours, threshold, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int contourIndex = 0; contourIndex < contours.size(); contourIndex++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIndex));
            if (contourArea > largestArea) {
                largestArea = contourArea;
                boundingShape = Imgproc.boundingRect(contours.get(contourIndex));
            }
        }

        return mat.submat(boundingShape);
    }
}
