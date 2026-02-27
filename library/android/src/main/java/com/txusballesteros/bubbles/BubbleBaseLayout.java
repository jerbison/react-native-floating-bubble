package com.txusballesteros.bubbles;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class BubbleBaseLayout extends FrameLayout {
    private WindowManager.LayoutParams viewParams;
    private WindowManager windowManager;
    private BubblesLayoutCoordinator layoutCoordinator;

    protected void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    protected WindowManager getWindowManager() {
        return windowManager;
    }

    protected void setViewParams(WindowManager.LayoutParams viewParams) {
        this.viewParams = viewParams;
    }

    protected WindowManager.LayoutParams getViewParams() {
        return viewParams;
    }

    protected void setLayoutCoordinator(BubblesLayoutCoordinator layoutCoordinator) {
        this.layoutCoordinator = layoutCoordinator;
    }

    protected BubblesLayoutCoordinator getLayoutCoordinator() {
        return layoutCoordinator;
    }

    public BubbleBaseLayout(Context context) {
        super(context);
    }

    public BubbleBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleBaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
