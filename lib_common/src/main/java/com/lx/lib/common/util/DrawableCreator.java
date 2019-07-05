package com.lx.lib.common.util;

import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * Created by glennli on 2018/12/5.<br/>
 */
public final class DrawableCreator {
    private float[] radii;
    private float borderWidth, dashWidth, dashGap;
    private int fillColor, borderColor;
    private int[] gradientColors;
    private float[] gradientSegments;
    private GradientDrawable.Orientation gradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT;

    private DrawableCreator() {}

    public static DrawableCreator newCreator() {
        return new DrawableCreator();
    }

    public DrawableCreator radius(float r) {
        if (radii == null) {
            radii = new float[8];
        }
        for (int i = radii.length - 1; i >= 0; i--) {
            radii[i] = r;
        }
        return this;
    }

    public DrawableCreator radii(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        if (radii == null) {
            radii = new float[8];
        }
        radii[0] = topLeft;
        radii[1] = topLeft;
        radii[2] = topRight;
        radii[3] = topRight;
        radii[4] = bottomRight;
        radii[5] = bottomRight;
        radii[6] = bottomLeft;
        radii[7] = bottomLeft;
        return this;
    }

    public DrawableCreator fillColor(int fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public DrawableCreator border(int borderColor, float borderWidth, float dashWidth, float dashGap) {
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.dashWidth = dashWidth;
        this.dashGap = dashGap;
        return this;
    }

    public DrawableCreator gradientColors(int... gradientColors) {
        this.gradientColors = gradientColors;
        return this;
    }

    public DrawableCreator gradientSegments(float... gradientSegments) {
        this.gradientSegments = gradientSegments;
        return this;
    }

    public DrawableCreator gradientOrientation(GradientDrawable.Orientation orientation) {
        gradientOrientation = orientation;
        return this;
    }

    private Drawable create_2() {
        Drawable result = null;
        if (gradientColors != null && gradientOrientation != null) {
            result = createGradientDrawable();
        } else {
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
            Paint paint = shapeDrawable.getPaint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(fillColor);
            result = shapeDrawable;
        }
        result = addBorderIfNeed(result);
        return result;
    }

    public Drawable create() {
        if (gradientSegments != null && gradientSegments.length > 0) {
            ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
                float[] positions = new float[4];

                @Override
                public Shader resize(int width, int height) {
                    convertGradientPosition(width, height, positions);
                    LinearGradient lg = new LinearGradient(positions[0], positions[1], positions[2], positions[3],
                            gradientColors,
                            gradientSegments,
                            Shader.TileMode.CLAMP);
                    return lg;
                }
            };
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
            shapeDrawable.setShaderFactory(shaderFactory);
            return addBorderIfNeed(shapeDrawable);
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadii(radii);
        if (gradientColors == null) {
            gradientDrawable.setColor(fillColor);
        } else {
            gradientDrawable.setColors(gradientColors);
            gradientDrawable.setOrientation(gradientOrientation);
        }
        if (borderWidth > 0 && borderColor != 0) {
            gradientDrawable.setStroke((int) borderWidth, borderColor, dashWidth, dashGap);
        }
        return gradientDrawable;
    }

    private Drawable addBorderIfNeed(Drawable drawable) {
        if (borderWidth == 0 || borderColor == 0) {
            return drawable;
        }
        // first layer
        Drawable[] layers = new Drawable[2];
        layers[0] = drawable;
        // border layer
        ShapeDrawable borderLayer = new ShapeDrawable(new RoundRectShape(radii, null, null));
        Paint borderPaint = borderLayer.getPaint();
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
        if (dashWidth > 0 && dashGap > 0) {
            PathEffect effect = new DashPathEffect(new float[]{dashWidth, dashGap}, 0);
            borderPaint.setPathEffect(effect);
        }
        layers[1] = borderLayer;
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        int dis = (int) Math.ceil(borderWidth / 2);
        // 使border的外边界与first layer的外边界对齐
        layerDrawable.setLayerInset(1, dis, dis, dis, dis);
        return layerDrawable;
    }

    private Drawable createGradientDrawable() {
        if (gradientSegments == null || gradientSegments.length <= 0) {
            GradientDrawable gradientDrawable = new GradientDrawable(gradientOrientation, gradientColors);
            gradientDrawable.setCornerRadii(radii);
            return gradientDrawable;
        }
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            float[] positions = new float[4];

            @Override
            public Shader resize(int width, int height) {
                convertGradientPosition(width, height, positions);
                LinearGradient lg = new LinearGradient(positions[0], positions[1], positions[2], positions[3],
                        gradientColors,
                        gradientSegments,
                        Shader.TileMode.CLAMP);
                return lg;
            }
        };
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
        shapeDrawable.setShaderFactory(shaderFactory);
        return shapeDrawable;
    }

    private void convertGradientPosition(int width, int height, float[] position) {
        float startX = 0, startY = 0, endX = width, endY = 0;
        switch (gradientOrientation) {
            case RIGHT_LEFT:
                startX = width;
                startY = endX = endY = 0;
                break;
            case TL_BR:
                startX = startY = 0;
                endX = width;
                endY = height;
                break;
            case BL_TR:
                startX = 0;
                startY = height;
                endX = width;
                endY = 0;
                break;
            case BR_TL:
                startX = width;
                startY = height;
                endX = 0;
                endY = 0;
                break;
            case TR_BL:
                startX = width;
                startY = 0;
                endX = 0;
                endY = height;
                break;
            case TOP_BOTTOM:
                startX = 0;
                startY = 0;
                endX = 0;
                endY = height;
                break;
            case BOTTOM_TOP:
                startX = 0;
                startY = height;
                endX = 0;
                endY = 0;
                break;
            case LEFT_RIGHT:
            default:
                break;
        }
        position[0] = startX;
        position[1] = startY;
        position[2] = endX;
        position[3] = endY;
    }

    public static StateListDrawable createStateListDrawable(Drawable normal, Drawable pressed, Drawable disabled) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        // -android.R.attr.state_enabled表示enable为false的状态
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, disabled);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        stateListDrawable.addState(new int[0], normal);
        return stateListDrawable;
    }
}
