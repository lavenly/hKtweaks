/*
 * Copyright (C) 2015-2026 hKtweaks contributors
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.hades.hKtweaks.utils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.view.View;
import android.view.animation.PathInterpolator;

import androidx.annotation.AttrRes;
import androidx.annotation.StyleRes;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.google.android.material.motion.MotionUtils;

/**
 * Resolves custom motion from the active Material 3 Expressive theme.
 */
public final class ExpressiveMotion {

    private static final TimeInterpolator STANDARD_FALLBACK =
            new PathInterpolator(0.2f, 0f, 0f, 1f);
    private static final TimeInterpolator EMPHASIZED_DECELERATE_FALLBACK =
            new PathInterpolator(0.05f, 0.7f, 0.1f, 1f);

    private ExpressiveMotion() {
    }

    public static void applyEmphasized(Animator animator, Context context,
                                       @AttrRes int durationAttribute, int fallbackDuration) {
        apply(animator, context, durationAttribute, fallbackDuration,
                com.google.android.material.R.attr.motionEasingEmphasizedInterpolator,
                STANDARD_FALLBACK);
    }

    public static void applyEmphasizedDecelerate(Animator animator, Context context,
                                                 @AttrRes int durationAttribute,
                                                 int fallbackDuration) {
        apply(animator, context, durationAttribute, fallbackDuration,
                com.google.android.material.R.attr.motionEasingEmphasizedDecelerateInterpolator,
                EMPHASIZED_DECELERATE_FALLBACK);
    }

    public static int resolveDuration(Context context, @AttrRes int durationAttribute,
                                      int fallbackDuration) {
        return MotionUtils.resolveThemeDuration(context, durationAttribute, fallbackDuration);
    }

    public static SpringAnimation spring(View target,
                                         DynamicAnimation.ViewProperty property,
                                         float finalPosition,
                                         @AttrRes int springAttribute,
                                         @StyleRes int fallbackStyle) {
        SpringForce force = MotionUtils.resolveThemeSpringForce(
                target.getContext(), springAttribute, fallbackStyle);
        force.setFinalPosition(finalPosition);
        return new SpringAnimation(target, property).setSpring(force);
    }

    private static void apply(Animator animator, Context context,
                              @AttrRes int durationAttribute, int fallbackDuration,
                              @AttrRes int easingAttribute,
                              TimeInterpolator fallbackInterpolator) {
        animator.setDuration(resolveDuration(context, durationAttribute, fallbackDuration));
        animator.setInterpolator(MotionUtils.resolveThemeInterpolator(
                context, easingAttribute, fallbackInterpolator));
    }
}
