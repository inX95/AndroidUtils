package com.sensetime.game.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import com.cv.faceapi.CvFace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.TimingLogger;

public class BitmapUtils {
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		try {
			Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(circleBitmap);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
			float roundPx = 0.0f;
			// 以较短的边为标准
			if (bitmap.getWidth() > bitmap.getHeight()) {
				roundPx = bitmap.getHeight() / 2.0f;
			} else {
				roundPx = bitmap.getWidth() / 2.0f;
			}
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.WHITE);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			canvas.drawBitmap(bitmap, src, rect, paint);
			return circleBitmap;
		} catch (Exception e) {
			return bitmap;
		}
	}
	public static Bitmap getCropBitmap(Bitmap sourceBitmap, Rect orginRect) {
		return getCropBitmap(sourceBitmap, orginRect, 2f, 2f);
	}

	public static Bitmap getCropBitmap(Bitmap sourceBitmap, Rect orginRect,
			float scaleX, float scaleY) {
		if (sourceBitmap == null || sourceBitmap.isRecycled()) {
			return null;
		}
		Rect rect = getScaleRect(orginRect, scaleX, scaleY,
				sourceBitmap.getWidth(), sourceBitmap.getHeight());
		return sourceBitmap.createBitmap(sourceBitmap, rect.left, rect.top,
				rect.width(), rect.height());
	}
	public static Bitmap getCropBitmap(Bitmap bitmap, Rect rect, boolean frontcamera) {
		Bitmap cropBitmap = BitmapUtils.getCropBitmap(bitmap, rect);
		if (frontcamera) {
			cropBitmap = BitmapUtils.rotateFrontCameraBitmap(cropBitmap, false);
		}
		return cropBitmap;
	}
	public static Bitmap rotateFrontCameraBitmap(Bitmap bitmap, boolean recyle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		matrix.postScale(1f, -1f);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (recyle) {
			recycleImage(bitmap);
		}
		return resizedBitmap;
	}
	public static boolean recycleImage(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
			return true;
		}
		return false;
	}
	public static Rect getScaleRect(Rect rect, float scaleX, float scaleY,
			int maxW, int maxH) {
		Rect resultRect = new Rect();
		int left = (int) (rect.left - rect.width() * (scaleX - 1) / 2);
		int right = (int) (rect.right + rect.width() * (scaleX - 1) / 2);
		int bottom = (int) (rect.bottom + rect.height() * (scaleY - 1) / 2);
		int top = (int) (rect.top - rect.height() * (scaleY - 1) / 2);
		

		top -= scaleY * rect.height() / 6;
		bottom -= scaleY * rect.height() / 6;
		
		resultRect.left = left > 0 ? left : 0;
		resultRect.right = right > maxW ? maxW : right;
		resultRect.bottom = bottom > maxH ? maxH : bottom;
		resultRect.top = top > 0 ? top : 0;
		return resultRect;
	}
	public static Bitmap rotateBitmap(CvFace face, int[] intBuffer, byte[] byteBuffer, int width, int height) {
		Bitmap bitmap = null;
		if (intBuffer != null) {
			bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			bitmap.copyPixelsFromBuffer(IntBuffer.wrap(intBuffer));
		} else {
//			bitmap = BitmapUtils.nv21ToBitmapRs(getApplicationContext(), byteBuffer, mCameralayout.getPreviewWidth(), mCameralayout.getPreviewHeight());
			bitmap = BitmapUtils.yuvToBitmap(byteBuffer, width, height, ImageFormat.NV21);
			bitmap = BitmapUtils.rotateFrontCameraBitmap(bitmap, true);
		}
		return bitmap;
	}
	public static Bitmap catchCirclePhotoBitmap(Bitmap fullBitmap, Rect rect) {
		Bitmap bitmap = BitmapUtils.getCropBitmap(fullBitmap, rect);
		return BitmapUtils.getCircleBitmap(bitmap);
	}

 	public static Bitmap yuvToBitmap(byte[]nv21 ,int width, int height, int yuvType) {
		YuvImage yuvImage = new YuvImage(nv21, yuvType, width, height, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, baos);
        byte[] cur = baos.toByteArray();
        return BitmapFactory.decodeByteArray(cur, 0, cur.length);
	}

	public static Bitmap createImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false;
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public static void saveBitmap(String dirRoot, String photoName,
			Bitmap photoBitmap, int quality) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = new File(dirRoot);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File photoFile = new File(dir, photoName); // 在指定路径下创建文件
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, quality,
							fileOutputStream)) {
						fileOutputStream.flush();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					if (fileOutputStream != null) {
						
						fileOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
