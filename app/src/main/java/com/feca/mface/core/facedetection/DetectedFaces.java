package com.feca.mface.core.facedetection;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Stardust on 2017/9/7.
 */

public class DetectedFaces {

    public FaceShapeItem[] face_shape; // 人脸轮廓结构体，包含所有人脸的轮廓点
    public int image_width; // 请求图片的宽度
    public int image_height; //	请求图片的高度
    public int errorcode; //返回状态值
    public String errormsg; //返回错误消息

    public static class FaceShapeItem {
        public Point[] face_profile; //	描述脸型轮廓的21点
        public Point[] left_eye; //描述左眼轮廓的8点
        public Point[] right_eye;    //	描述右眼轮廓的8点
        public Point[] left_eyebrow; //	描述左眉轮廓的8点
        public Point[] right_eyebrow; //	描述右眉轮廓的8点
        public Point[] mouth; //描述嘴巴轮廓的22点
        public Point[] nose; //描述鼻子轮廓的13点

        public Point[] getUpperLip() {
            // 0, 21, 20, 19, 18, 17, 6, 7, 8, 9, 10, 11
            int[] indices = {0, 21, 20, 19, 18, 17, 6, 7, 8, 9, 10, 11};
            Point[] upperLip = new Point[indices.length];
            for(int i = 0; i < indices.length; i++){
                upperLip[i] = mouth[indices[i]];
            }
            return upperLip;
        }


        public Point[] getLowerLip() {
            // 0, 1, 2, 3, 4, 5, 6, 17, 16, 15, 14, 12
            int[] indices = {0, 1, 2, 3, 4, 5, 6, 16, 15, 14, 13, 12};
            Point[] lowerLip = new Point[indices.length];
            for(int i = 0; i < indices.length; i++){
                lowerLip[i] = mouth[indices[i]];
            }
            return lowerLip;
        }

        public Point[] getLeftUpperEye(){
            // 0, 1, 2, 3, 4, 5, 6, 17, 16, 15, 14, 12
            int[] indices = {0, 1, 2, 3, 4, 5, 6, 7};
            Point[] leftEye = new Point[indices.length+3];
            for(int i = 0; i < indices.length; i++){
                leftEye[i] = left_eye[indices[i]];
            }
            Point left_zero = new Point(left_eye[0].x - (left_eye[1].x - left_eye[0].x)*2,left_eye[0].y + (left_eye[2].y-left_eye[0].y)/3);
            Point left_nine = new Point(left_eye[0].x,left_eye[0].y - (left_eye[2].y-left_eye[6].y)/2);
            Point left_one = new Point(left_eye[7].x,left_eye[7].y - (left_eye[2].y-left_eye[6].y)/2);
            Point left_two = new Point(left_eye[6].x,left_eye[6].y - (left_eye[2].y-left_eye[6].y)/2);
            Point left_three = new Point(left_eye[5].x,left_eye[5].y - (left_eye[2].y-left_eye[6].y)/2);
            Point left_ten = new Point(left_eye[5].x + (left_eye[4].x-left_eye[5].x),left_eye[4].y - (left_eye[1].y-left_eye[7].y));
            leftEye[9] = left_eye[0];
            leftEye[0] = left_zero;
            leftEye[1] = left_one;
            leftEye[2] = left_two;
            leftEye[3] = left_three;
            leftEye[8] = left_nine;
            leftEye[10] = left_ten;
            return leftEye;
        }

        public Point[] getRightUpperEye(){
            // 0, 1, 2, 3, 4, 5, 6, 17, 16, 15, 14, 12
            int[] indices = {0, 1, 2, 3, 4, 5, 6, 7};
            Point[] rightEye = new Point[indices.length+3];
            for(int i = 0; i < indices.length; i++){
                rightEye[i] = right_eye[indices[i]];
            }
            Point right_zero = new Point(right_eye[0].x + (right_eye[0].x - right_eye[1].x)*2,right_eye[0].y + (right_eye[2].y-right_eye[0].y)/4);
            Point right_one = new Point(right_eye[7].x,right_eye[7].y - (right_eye[2].y-right_eye[6].y)/2);
            Point right_two = new Point(right_eye[6].x,right_eye[6].y - (right_eye[2].y-right_eye[6].y)/2);
            Point right_three = new Point(right_eye[5].x,right_eye[5].y - (right_eye[2].y-right_eye[6].y)/2);
            Point right_nine = new Point(right_eye[0].x,right_eye[0].y - (right_eye[2].y-right_eye[6].y)/2);
            Point right_ten = new Point(right_eye[4].x + (right_eye[5].x-right_eye[4].x),right_eye[4].y - (right_eye[1].y-right_eye[7].y));
            rightEye[9] = right_eye[0];
            rightEye[0] = right_zero;
            rightEye[1] = right_one;
            rightEye[2] = right_two;
            rightEye[3] = right_three;
            rightEye[8] = right_nine;
            rightEye[10] = right_ten;
            return rightEye;
        }

        public Point[] getRightLowerEye(){
            int[] indices = {0, 1, 2, 3, 4, 5, 6, 7};
            Point[] rightEye = new Point[indices.length];
            for(int i = 0; i < indices.length; i++){
                rightEye[i] = right_eye[indices[i]];
            }
            Point right_zero = new Point(right_eye[0].x + (right_eye[0].x - right_eye[1].x),right_eye[0].y);
            Point right_five = new Point(right_eye[1].x,right_eye[1].y + (right_eye[1].y - right_eye[7].y)/4);
            Point right_six = new Point(right_eye[2].x,right_eye[2].y + (right_eye[2].y - right_eye[6].y)/4);
            Point right_seven = new Point(right_eye[3].x,right_eye[3].y + (right_eye[3].y - right_eye[5].y)/4);
            rightEye[0] = right_zero;
            rightEye[5] = right_five;
            rightEye[6] = right_six;
            rightEye[7] = right_seven;

            return rightEye;
        }

        public Point[] getLeftLowerEye(){
            int[] indices = {0, 1, 2, 3, 4, 5, 6, 7};
            Point[] leftEye = new Point[indices.length];
            for(int i = 0; i < indices.length; i++){
                leftEye[i] = left_eye[indices[i]];
            }
            Point left_zero = new Point(left_eye[0].x - (left_eye[1].x - left_eye[0].x),left_eye[0].y);
            Point left_five = new Point(left_eye[1].x,left_eye[1].y + (left_eye[1].y - left_eye[7].y)/4);
            Point left_six = new Point(left_eye[2].x,left_eye[2].y + (left_eye[2].y - left_eye[6].y)/4);
            Point left_seven = new Point(left_eye[3].x,left_eye[3].y + (left_eye[3].y - left_eye[5].y)/4);
            leftEye[0] = left_zero;
            leftEye[5] = left_five;
            leftEye[6] = left_six;
            leftEye[7] = left_seven;

            return leftEye;
        }

        public Point[] getLeftCorner(){
            Point[] leftEye = new Point[3];
            Point left_one = new Point(left_eye[0].x - (left_eye[1].x - left_eye[0].x)*2,left_eye[0].y);
            Point left_two = new Point(left_eye[0].x - (left_eye[1].x - left_eye[0].x)*2,left_eye[7].y);
            leftEye[0] = left_one;
            leftEye[1] = left_two;
            leftEye[2] = left_eye[7];
            return leftEye;
        }

        public Point[] getRightCorner(){
            Point[] rightEye = new Point[3];
            Point right_one = new Point(right_eye[0].x + (right_eye[0].x - right_eye[1].x)*2,right_eye[0].y);
            Point right_two = new Point(right_eye[0].x + (right_eye[0].x - right_eye[1].x)*2,right_eye[7].y);
            rightEye[0] = right_one;
            rightEye[1] = right_two;
            rightEye[2] = right_eye[7];
            return rightEye;
        }

    }

}
