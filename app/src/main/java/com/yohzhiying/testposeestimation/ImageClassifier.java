package com.yohzhiying.testposeestimation;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ImageClassifier {

    private final int DIM_BATCH_SIZE = 1;
    private final int DIM_PIXEL_SIZE = 3;
    private final int numBytesPerChannel = 4;

    protected final int imageSizeX = 192;
    protected final int imageSizeY = 192;
    private final int outputW = 96;
    private final int outputH = 96;
    private final int LABEL_LENGTH = 14;
    private final String modelPath = "model_pose.tflite";

    protected Interpreter tflite = null;
    protected ByteBuffer imgData = null;
    private Mat mMat = null;
    public float[][] mPrintPointArray = null;
    private int[] intValues = new int[imageSizeX*imageSizeY];
    private float[][][][] heatmapArray = new float[1][outputW][outputH][14];

    private static final String TAG = "TfLiteCameraDemo";

    public ImageClassifier(Activity activity) throws IOException {
        this.tflite = new Interpreter(loadModelFile(activity));
        this.imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_PIXEL_SIZE * imageSizeX * imageSizeY * numBytesPerChannel);
        this.imgData.order(ByteOrder.nativeOrder());
        Log.d("TfLiteCameraDemo", "Created a Tensorflow Lite Image Classifier.");
    }

    public void classifyFrame(Bitmap bitmap) {
        if (this.tflite == null) {
            Log.e("TfLiteCameraDemo", "Tflite model is null");
            return;
        }

        convertBitmapToByteBuffer(bitmap);
        runInference();
        Log.d("TfLiteCameraDemo", "Frame classified");
    }

    public final void close() {
        tflite.close();
        this.tflite = null;
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d(TAG, " Model file loaded");
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) { return; }

        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for(int i = 0;i < imageSizeX; i++) {
            for(int j = 0; j < imageSizeY; j++) {
                int value = this.intValues[pixel++];
                imgData.putFloat((value) & 0xFF); //B
                imgData.putFloat((value >> 8)  & 0xFF); //G;
                imgData.putFloat((value >> 16) & 0xFF);
            }
        }
        Log.d("TfLiteCameraDemo", "Bitmap stored into a bytebuffer!");
    }

    private void runInference() {
        tflite.run(imgData, heatmapArray);

        if (mPrintPointArray == null) {
            mPrintPointArray = new float[2][14];
        }
        if (mMat == null) {
            mMat = new Mat(outputW, outputH, CvType.CV_32F);
        }

        float[] tempArray = new float[outputW*outputH];
        float[] outTempArray = new float[outputW*outputH];

        for (int i=0; i < LABEL_LENGTH; i++) {
            int index = 0;
            for (int x=0; x < outputW; x++) {
                for (int y=0; y < outputH; y++) {
                    tempArray[index] = heatmapArray[0][y][x][i];
                    index++;
                }
            }

            mMat.put(0, 0, tempArray);
            Imgproc.GaussianBlur(mMat, mMat, new Size(5,5), 0, 0);
            mMat.get(0,0,outTempArray);

            float xMax = 0f;
            float yMax = 0f;
            float vMax = 0f;

            for(int x = 0; x < outputW; x++) {
                for(int y = 0; y < outputH; y++) {
                    float value = get(x, y, outTempArray);
                    if(value > vMax) {
                        vMax = value;
                        xMax = x;
                        yMax = y;
                    }
                }
            }

            mPrintPointArray[0][i] = xMax;
            mPrintPointArray[1][i] = yMax;
        }
    }

    private float get(int x, int y, float[] arr) {
        if(x < 0 || y < 0 || x >= outputW || y >= outputH) {
            return -1f;
        } else {
            return arr[x*outputW+y];
        }
    }
}
