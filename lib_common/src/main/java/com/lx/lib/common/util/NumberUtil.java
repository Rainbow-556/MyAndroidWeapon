package com.lx.lib.common.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Created by glennli on 2019/1/4.<br/>
 */
public final class NumberUtil {
    /**
     * 格式化数字，格式化小数位时，是四舍五入
     *
     * @param num
     * @param minFractionCount 至少保留的小数位
     * @param maxFractionCount 最多保留的小数位
     * @return
     */
    @NonNull
    public static String format(double num, int minFractionCount, int maxFractionCount) {
        try {
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(minFractionCount);
            numberFormat.setMaximumFractionDigits(maxFractionCount);
            numberFormat.setRoundingMode(RoundingMode.HALF_UP);
            return numberFormat.format(num);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 格式化金额，例：入参(999999, 2, 2)，输出9999.99
     *
     * @param num 单位为分
     * @param minFractionCount
     * @param maxFractionCount
     * @return
     */
    public static String formatAmount(double num, int minFractionCount, int maxFractionCount) {
        String result = format(num / 100, minFractionCount, maxFractionCount);
        if (TextUtils.isEmpty(result)) {
            return result;
        }
        return result.replaceAll(",", "");
    }

    public static int parseInt(String num, int defaultValue) {
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double parseDouble(String num, double defaultValue) {
        try {
            return Double.parseDouble(num);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float parseFloat(String num, float defaultValue) {
        try {
            return Float.parseFloat(num);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
