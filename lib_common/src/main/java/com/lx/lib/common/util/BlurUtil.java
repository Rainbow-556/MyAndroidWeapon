package com.lx.lib.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;

/**
 * Created by glennli on 2019/7/5.<br/>
 */
public final class BlurUtil {
    /**
     * 高斯模糊Bitmap
     *
     * @param context
     * @param originalBitmap
     * @param scale originalBitmap的缩放比例
     * @param radius 模糊半径，0 < radius <= 25
     * @return
     */
    @Nullable
    public static Bitmap blur(Context context, Bitmap originalBitmap, float scale, float radius) {
        if (originalBitmap == null || originalBitmap.getWidth() <= 0 || originalBitmap.getHeight() <= 0) {
            return null;
        }
        if (scale != 1) {
            int bmpWidth = (int) (originalBitmap.getWidth() * scale);
            int bmpHeight = (int) (originalBitmap.getHeight() * scale);
            if (bmpWidth <= 0 || bmpHeight <= 0) {
                return null;
            }
            // bmpWidth、bmpHeight必须大于0，否则在某些版本上Bitmap.createScaledBitmap()会崩溃
            Bitmap newBitmap = Bitmap.createScaledBitmap(originalBitmap, bmpWidth, bmpHeight, true);
            if (newBitmap == null) {
                return null;
            }
            if (originalBitmap != newBitmap) {
                originalBitmap.recycle();
            }
            originalBitmap = newBitmap;
        }
        // Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context.getApplicationContext());
        // Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, originalBitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        // Set the radius of the blur: 0 < radius <= 25
        if (radius < 0) {
            radius = 1;
        } else if (radius > 25) {
            radius = 25;
        }
        blurScript.setRadius(radius);
        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        // recycle the original bitmap
        originalBitmap.recycle();
        // After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;
    }
}
