# Android 抽象媒体控制器类（视频播放器）

> 专为 Android 视频播放器设计的抽象控制器，封装播放控制、进度调节、全屏/锁屏、弹窗交互等能力，是视频 UI 的「控制中枢」。

---

## 一、核心定位
- **平台**：Android  
- **类型**：抽象类（继承即可拿到完整播放控制逻辑）  
- **职责**：  
  - 播放/暂停、进度调节、倍速、全屏、锁屏  
  - 弹窗交互（进度、倍速、下一集）  
  - 无障碍 & 按键 & 触摸事件统一处理  

---

## 二、功能模块拆解

| 模块 | 关键能力 | 入口/组件 | 备注 |
|---|---|---|---|
| **基础播放** | 播放/暂停 | `J()` | 驱动 `LottieAnimationView` 状态动画 |
|  | 快进/快退 | `e0(int s)` / `d0(int s)` | 秒级跳转 |
|  | 进度同步 | `b0()` | 同步 `SeekBar` 与当前时间 |
|  | 时间格式化 | `o0(long ms)` | 120000 ms → `02:00` |
| **UI 显隐** | 显示 | `show(int delayMs)` | 默认 5 s 后自动隐藏 |
|  | 隐藏 | `hide()` | 淡出动画 `D` |
|  | 动画 | `ObjectAnimator` | 淡入 `C` / 淡出 `D` |
| **弹窗** | 进度拖动弹窗 | `o` | 缩略图预览（子类实现 `j0()`） |
|  | 倍速弹窗 | `p` | `m0()` 触发 |
|  | 下一集提示 | `q` | `g0()` 触发 |
| **锁屏** | 锁屏/解锁 | `p0()` | `LottieAnimationView K` 状态动画 |
| **全屏** | 进入/退出 | `L()` / `M()` | 按钮 `e` / `f` |
| **无障碍** | 事件分发 | `onInitializeAccessibilityEvent` | 适配 TalkBack |
| **输入事件** | 触摸/轨迹球 | `onTouchEvent` / `onTrackballEvent` | 触发 `show()` |

---

## 三、类结构速览

### 1. 抽象方法（子类必须实现）
| 方法 | 用途 |
|---|---|
| `getLayoutId()` | 返回控制器布局 `R.layout.xxx` |
| `getBottomControllerHeight()` | 底部控制栏高度（用于弹窗垂直定位） |

### 2. 核心成员变量
| 变量 | 类型 | 说明 |
|---|---|---|
| `a` | `a`（自定义播放器接口） | 播放器实例 |
| `b` | `SeekBar` | 进度条 |
| `c` | `TextView` | 当前时间/总时长 |
| `d` | `LottieAnimationView` | 播放/暂停动画 |
| `e` / `f` | `ImageView` | 全屏进入/退出按钮 |
| `K` | `LottieAnimationView` | 锁屏动画 |
| `o` / `p` / `q` | `PopupWindow` | 进度、倍速、下一集弹窗 |

### 3. 关键监听器
| 监听器 | 场景 |
|---|---|
| `G` | 播放按钮点击 |
| `H` | 全屏按钮点击 |
| `I` | `SeekBar` 拖动回调 |
| `setOnClickPlayButtonListener` | 外部播放状态回调 |
| `setOnScreenLockListener` | 锁屏状态回调 |
| `setPlayerOrientationListener` | 屏幕方向变化回调 |

---

## 四、状态管理

| 类/字段 | 作用 |
|---|---|
| `UserState.isUserPause` | 记录用户是否手动暂停，避免自动逻辑覆盖 |

---

## 五、快速接入步骤

1. **继承**  
   ```java
   public class MyController extends AbstractMediaController {
       @Override
       protected int getLayoutId() {
           return R.layout.video_controller;
       }
       @Override
       protected int getBottomControllerHeight() {
           return getResources().getDimensionPixelSize(R.dimen.controller_h);
       }
   }
