package com.xxhy.ewt360hook;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.joor.Reflect;

public class MainHook
    implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

  @Override
  public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
    if ("com.mistong.ewt360".equals(lpparam.packageName)) {
      ClassLoader classLoader = lpparam.classLoader;
            
      // 爱加密入口
      XposedHelpers.findAndHookMethod(
          "s.h.e.l.l.S",
          classLoader,
          "onCreate",
          new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              Application app = (Application) param.thisObject;
            }
          });
      // EWT 真实入口
      XposedHelpers.findAndHookMethod(
          "com.mistong.ewt360.EwtApplication",
          classLoader,
          "onCreate",
          new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              Application app = (Application) param.thisObject;
            }
          });
      // 主页面
      XposedHelpers.findAndHookMethod(
          "com.mistong.ewt360.ui.activity.MainActivity",
          classLoader,
          "onCreate",
          Bundle.class,
          new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              ToastUtils.show("Ewt360 Hook模块已加载！");
            }
          });
      // MstAccountManager
      XposedHelpers.findAndHookMethod(
          "com.mistong.ewt360.user.MstAccountManager",
          classLoader,
          "isTeacher",
          new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
              param.setResult(true);
            }
          });
      // 用户信息
      XposedHelpers.findAndHookConstructor(
          "com.mistong.ewt360.user.model.UserInfoBean",
          classLoader,
          String.class,
          String.class,
          String.class,
          int.class,
          String.class,
          String.class,
          int.class,
          String.class,
          String.class,
          String.class,
          String.class,
          boolean.class,
          String.class,
          int.class,
          boolean.class,
          boolean.class,
          String.class,
          boolean.class,
          boolean.class,
          String.class,
          String.class,
          String.class,
          String.class,
          boolean.class,
          String.class,
          String.class,
          String.class,
          String.class,
          new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              Object thisObj = param.thisObject;
              Reflect.on(thisObj)
                  .set("isTeacher", true)
                  .set("realName", "嘉豪")
                  .set("nickName", "嘉豪")
                  .set("expireYear", "8888");
            }
          });
      // MstVideoView
      // 修改0.8倍速为20倍速
      XposedHelpers.findAndHookMethod(
          "com.mistong.android.mediaplayer.widget.MstVideoView",
          classLoader,
          "setSpeed",
          float.class,
          new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              super.beforeHookedMethod(param);
              float speed = (float) param.args[0];
              if (0.8f == speed) {
                param.args[0] = 20.0f;
              }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
            }
          });
      // 修改是否完成（进度≥80%视为完成）
      XposedHelpers.findAndHookMethod(
          "com.mistong.ewt360.core.media.video.l",
          classLoader,
          "g",
          new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
              param.setResult(1.0f);
            }
          });

      // 篡改专注度测试结果
      /*XposedHelpers.findAndHookConstructor(
          "com.mistong.ewt360.core.media.newvideo.bean.ReportVideoPointParam",
          classLoader,
          String.class,
          String.class,
          int.class,
          String.class,
          int.class,
          int.class,
          String.class,
          new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              super.beforeHookedMethod(param);
              param.args[5] = 2;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
            }
          });
      XposedHelpers.findAndHookMethod(
          "com.mistong.ewt360.core.media.newvideo.bean.ReportVideoPointParam",
          classLoader,
          "setSeriousCheckResult",
          int.class,
          new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              super.beforeHookedMethod(param);
              param.args[0] = 2;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
            }
          });*/

      // 显示专注测试布局时
      XposedHelpers.findAndHookMethod(
          "com.mistong.ewt360.core.media.view.VideoSeriousnessTestLayout",
          classLoader,
          "m",
          new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
              ToastUtils.show("Ewt360 Hook:已跳过专注度检测！");
              Reflect.on(param.thisObject).call("g");// 不知道为什么测试界面不会隐藏掉，调用g方法隐藏
            }
          });

      // 设置控件点击事件后立即点击
      XposedHelpers.findAndHookMethod(
          "com.mistong.ewt360.core.media.view.VideoSeriousnessTestTipLayout",
          classLoader,
          "setPassClick",
          android.view.View.OnClickListener.class,
          new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              super.afterHookedMethod(param);
              int id =
                  Reflect.on("com.mistong.ewt360.core.R$id", lpparam.classLoader)
                      .field("tv_pass")
                      .get();
              View view = Reflect.on(param.thisObject).call("a", id).get();
              android.view.View.OnClickListener listener =
                  (android.view.View.OnClickListener) param.args[0];
              listener.onClick(view);
            }
          });
    }
  }

  @Override
  public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam arg0)
      throws Throwable {
    // TODO: Implement this method
  }

  @Override
  public void initZygote(StartupParam arg0) throws Throwable {
    // TODO: Implement this method
  }
}
