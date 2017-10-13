package com.example.xw.cameratest.preview;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by xw on 2017/9/19.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;

    public CameraSurfaceView(Context context) {
        this(context, null, 0);
    }
    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        mSurfaceHolder=getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        destroyCamera();
        initCamera();
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);//设置surfaceHolder与SurfaceView绑定
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();//开始预览
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        destroyCamera();
    }

    @Override
    public void run() {

    }

    @Override
    public

    private void initCamera(){
        mCamera=Camera.open();
//        try {
//            mCamera.setPreviewDisplay(mSurfaceHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Camera.Parameters parameters=mCamera.getParameters();
        int sWidth=((Activity)mContext).getWindowManager().getDefaultDisplay().getWidth();
        int sHeight=((Activity)mContext).getWindowManager().getDefaultDisplay().getHeight();
        Camera.Size optimumSize=getOptimunPreviewSize(parameters.getSupportedPreviewSizes(),sWidth,sHeight);
        Camera.Size pictureSize=getOptimunPreviewSize(parameters.getSupportedPictureSizes(),sWidth,sHeight);
        parameters.setPreviewSize(optimumSize.width,optimumSize.height);//设置预览尺寸
        parameters.setPictureSize(pictureSize.width,pictureSize.height);//设置预览尺寸
        parameters.setPreviewFormat(ImageFormat.NV21);//视频源数据格式

        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);//设置预览方向，Camera默认预览方向是横屏
    }

    private void destroyCamera(){
        if(mCamera==null){
            return;
        }
        mCamera.setPreviewCallback(null);//调用顺序很重要，这个必须在前
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }


    /**
     * Camera并不能支持所有的尺寸，在显示预览画面时，Camera会将预览画面调整成Surface大小，造成
     * 画面失真。因此，要保证预览画面长宽比和SurfaceView的长宽比一致，当没有完全相等的尺寸时，选取
     * 与SurfaceView尺寸偏差最小的尺寸作为预览尺寸。
     * @param supportSizeList
     * @param width 对应SurfaceView的宽度，这里默认为屏幕宽度
     * @param height 对应SurfaceView的高度，这里默认为屏幕高度
     */
    private Camera.Size getOptimunPreviewSize(List<Camera.Size> supportSizeList, int width, int height){
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) width / height;
        Log.d("Camera", "targetRatio " +targetRatio);
        if (supportSizeList == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        //长宽比最接近
        //注意：camera由于方向问题，宽高是倒过来的，所以计算比例的时候要反过来
        //比如竖屏下屏幕宽高是720*1280，Camera的宽高则是1280*720
        for (Camera.Size size : supportSizeList) {
            double ratio = (double) size.height / size.width;
            Log.d("Camera", "Checking size " + size.width + "w " + size.height
                    + "h "+ratio+" Ratio");
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        //最接近长度
//        minDiff = Double.MAX_VALUE;
//        for (Camera.Size size : supportSizeList) {
//            if (Math.abs(size.height - targetHeight) < minDiff) {
//                optimalSize = size;
//                minDiff = Math.abs(size.height - targetHeight);
//            }
//        }

        //最大尺寸
//            minDiff = 0;
//            for (Camera.Size size : supportSizeList) {
//                if (  size.height * size.width > minDiff ) {
//                    optimalSize = size;
//                    minDiff = size.height * size.width ;
//                }
//            }
        return optimalSize;
    }
}
