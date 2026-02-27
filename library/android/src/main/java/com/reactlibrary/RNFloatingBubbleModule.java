
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.reactlibrary.R;

import android.os.Bundle;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.File;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

public class RNFloatingBubbleModule extends ReactContextBaseJavaModule {

  private BubblesManager bubblesManager;
  private final ReactApplicationContext reactContext;
  private BubbleLayout bubbleView;

  public RNFloatingBubbleModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @ReactMethod
  public void reopenApp() {
    Intent launchIntent = reactContext.getPackageManager().getLaunchIntentForPackage(reactContext.getPackageName());
    if (launchIntent != null) {
      launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
      reactContext.startActivity(launchIntent);
    }
  }

  @Override
  public String getName() {
    return "RNFloatingBubble";
  }

  @ReactMethod
  public void showFloatingBubble(int x, int y, String iconPath, final Promise promise) {
    try {
      this.addNewBubble(x, y, iconPath);
      promise.resolve("");
    } catch (Exception e) {
      promise.reject(e.getMessage());
    }
  }

  @ReactMethod
  public void hideFloatingBubble(final Promise promise) {
    try {
      this.removeBubble();
      promise.resolve("");
    } catch (Exception e) {
      promise.reject(e.getMessage());
    }
  }

  @ReactMethod
  public void requestPermission(final Promise promise) {
    try {
      this.requestPermissionAction(promise);
    } catch (Exception e) {
      promise.reject(e.getMessage());
    }
  }

  @ReactMethod
  public void checkPermission(final Promise promise) {
    try {
      promise.resolve(hasPermission());
    } catch (Exception e) {
      promise.reject(e.getMessage());
    }
  }

  @ReactMethod
  public void initialize(final Promise promise) {
    if (!hasPermission()) {
      promise.reject("PERMISSION_DENIED", "Overlay permission not granted");
      return;
    }
    try {
      this.initializeBubblesManager();
      promise.resolve("");
    } catch (Exception e) {
      promise.reject(e.getMessage());
    }
  }

  private void addNewBubble(int x, int y, String iconPath) {
    this.removeBubble();
    bubbleView = (BubbleLayout) LayoutInflater.from(reactContext).inflate(R.layout.bubble_layout, null);

    // Set dynamic icon if path is provided, else use app icon
    ImageView imageView = (ImageView) bubbleView.findViewById(R.id.avatar);
    if (imageView != null) {
      boolean iconSet = false;
      if (iconPath != null && !iconPath.isEmpty()) {
        try {
          File imgFile = new File(iconPath);
          if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            iconSet = true;
          }
        } catch (Exception e) {
          // Fallback
        }
      }

      if (!iconSet) {
        try {
          android.graphics.drawable.Drawable appIcon = reactContext.getPackageManager()
              .getApplicationIcon(reactContext.getPackageName());
          imageView.setImageDrawable(appIcon);
        } catch (Exception e) {
          // Final fallback to XML default
        }
      }
    }

    bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
      @Override
      public void onBubbleRemoved(BubbleLayout bubble) {
        bubbleView = null;
        sendEvent("floating-bubble-remove");
      }
    });

    bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
      @Override
      public void onBubbleClick(BubbleLayout bubble) {
        sendEvent("floating-bubble-press");
        reopenApp();
      }
    });

    bubbleView.setShouldStickToWall(true);
    if (bubblesManager != null) {
      bubblesManager.addBubble(bubbleView, x, y);
    }
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return Settings.canDrawOverlays(reactContext);
    }
    return true;
  }

  private void removeBubble() {
    if (bubbleView != null && bubblesManager != null) {
      try {
        bubblesManager.removeBubble(bubbleView);
        bubbleView = null;
      } catch (Exception e) {
        // Ignore
      }
    }
  }

  public void requestPermissionAction(final Promise promise) {
    if (!hasPermission()) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
          Uri.fromParts("package", reactContext.getPackageName(), null));
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
      try {
        reactContext.startActivity(intent);
      } catch (Exception e) {
        // Fallback to general settings if specific one fails
        Intent fallbackIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reactContext.startActivity(fallbackIntent);
      }
    }
    if (hasPermission()) {
      promise.resolve("");
    } else {
      promise.reject("PERMISSION_DENIED", "Permission not granted");
    }
  }

  private void initializeBubblesManager() {
    bubblesManager = new BubblesManager.Builder(reactContext)
        .setTrashLayout(R.layout.bubble_trash_layout)
        .setInitializationCallback(new OnInitializedCallback() {
          @Override
          public void onInitialized() {
            // Initialized
          }
        }).build();
    bubblesManager.initialize();
  }

  private void sendEvent(String eventName) {
    WritableMap params = Arguments.createMap();
    if (reactContext.hasActiveCatalystInstance()) {
      reactContext
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
          .emit(eventName, params);
    }
  }
}
