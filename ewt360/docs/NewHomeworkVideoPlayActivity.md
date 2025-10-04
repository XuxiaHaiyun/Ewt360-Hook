# NewHomeworkVideoPlayActivity 代码梳理与监控逻辑

> Android 作业视频播放页，基于 ARouter + ViewModel + EventBus 实现，集成视频播放、教师列表、收藏、检测、护眼提醒等功能。

---

## 1. 页面定位
| 项        | 说明                                                                 |
|-----------|----------------------------------------------------------------------|
| 类        | `NewHomeworkVideoPlayActivity` 继承 `BaseVideoPlayerActivity`       |
| 路由      | `/homework/newCommonPlayer`                                          |
| 核心依赖  | ARouter、ViewModel、EventBus、BaseQuickAdapter、EUIDialog            |

---

## 2. 功能总览
| 功能分类   | 具体实现                                                                 |
|------------|--------------------------------------------------------------------------|
| 视频播放   | `NewHomeworkMediaPlayFragment` 承载，支持全屏/横屏，事件切换视频        |
| 数据获取   | `NewHomeworkVideoPlayViewModel` 请求 `PlayerLessonBean` / 收藏状态 / 报告链接 |
| 教师列表   | `HomeworkTeacherAdapter` 展示，点击跳转 `/teacher/open_detail`            |
| 学习辅助   | 40 min 护眼弹窗、学习检测（考试按钮）、收藏状态更新                       |
| 降级&监控  | `@DowngradeClassAnnotation` + `com.mistong.android.monitor.c` 事件埋点    |

---

## 3. 路由参数（必传）
| 参数名            | 含义               |
|-------------------|--------------------|
| `playVideoId`     | 视频 ID            |
| `homeworkId`      | 作业 ID            |
| `schoolId`        | 学校 ID            |
| `subjectId`       | 学科 ID            |
| `isNewThreeScene` | 是否新三年级场景   |
| `isSchoolVideo`   | 是否校本视频       |
| `onlyMustLearn`   | 仅展示必学内容     |

---

## 4. 关键方法
| 方法                | 作用                                                         |
|---------------------|--------------------------------------------------------------|
| `onCreate`          | 初始化路由、注册 EventBus、绑定 VM、加载 Fragment             |
| `setOnPlayUpdateListener` | 订阅 `VIDEO_ITEM_CLICK` 事件，切换播放源               |
| `onExamClick`       | 订阅 `EXAM_BTN_CLICK` 事件，打开学习检测 H5                 |
| `H9()`              | 40 min 计时器，触发护眼弹窗并埋点                           |

---

## 5. 待补充 / 修复
1. **生命周期空洞**  
   `onDestroy` / `onStart` / `onStop` 为 native 方法，需补 Java 实现（EventBus 反注册、计时器取消）。

2. **空安全**  
   多处 `i.v("viewModel")` 可能 NPE，需加非空保护或默认值。

3. **混淆还原**  
   类 `a`、`c`、`w` 等需对照 ProGuard 映射还原真实类名（如 `a` → `ActivityNewHomeworkVideoPlayBinding`）。

---

## 6. 学生监控逻辑详解

### 6.1 监控维度
| 维度         | 手段                                                                 |
|--------------|----------------------------------------------------------------------|
| 观看时长     | Fragment 回调累计 → VM 存储 → 后端上报；40 min 强制护眼弹窗         |
| 专注度       | 切屏 `onPause`/`onResume` 计数 + 埋点；拖拽/倍速行为标记非专注       |
| 数据上报     | `com.mistong.android.monitor.c` 实时上报，携带学生 ID                |

### 6.2 关键埋点事件
| 事件名               | 触发时机                          | 携带字段示例                          |
|----------------------|-----------------------------------|---------------------------------------|
| `page_enter`         | 进入播放页                        | `studentId`                           |
| `video_play/pause`   | 播放/暂停                         | `videoId, progress, duration`         |
| `video_watch_40min`  | 连续观看 40 min                   | `studentId, homeworkId`               |
| `video_screen_switch`| 切出 App                          | `studentId, switchCount, watchTime`   |
| `exam_click`         | 点击“考试”按钮                    | `studentId, homeworkId, videoId`      |

### 6.3 伪代码参考
#### 40 min 护眼计时
```kotlin
private fun startWatchTimer() {
    Timer().scheduleAtFixedRate(timerTask {
        if (isPlaying) {
            totalSeconds++
            if (totalSeconds >= 2400) {
                runOnUiThread {
                    pauseVideo()
                    showEyeCareDialog()
                    Monitor.c.n("video_watch_40min")
                           .a("studentId" to studentId)
                           .e()
                }
                cancel()
            }
        }
    }, 0, 1000)
}
```

切屏检测

```kotlin
override fun onPause() {
    super.onPause()
    if (isPlaying) {
        pauseVideo()
        switchCount++
        Monitor.c.n("video_screen_switch")
               .a("studentId" to studentId,
                  "switchCount" to switchCount,
                  "watchTimeBeforeSwitch" to totalSeconds)
               .e()
    }
}
```

---

7. 下一步可交付
- 还原全部混淆类名对照表  
- 补全 `onDestroy`/`onStart`/`onStop` 空实现  
