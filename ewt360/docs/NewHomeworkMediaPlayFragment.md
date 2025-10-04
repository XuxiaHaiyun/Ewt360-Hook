# NewHomeworkMediaPlayFragment 数据上报还原文档

> 基于反编译代码提炼，展示 **课后习题、播放完成、评价入口、完成页按钮** 等关键场景的数据上报实现。  
> 所有上报均通过 **com.mistong.android.monitor.c**（业务事件）与 **MoseEvent**（通用埋点）双通道落地，保证数据完整且场景可追踪。

---

## 一、上报基础设施

| 工具类 | 典型调用链 | 说明 |
|---|---|---|
| `com.mistong.android.monitor.c` | `c.b.n("业务").c("事件").a(map).e()` | 视频业务专属事件，必带 `lessonId` |
| `MoseEvent` | `MoseEvent.i(eventId, map)` / `k()` | 通用埋点，支持曝光/点击；横屏场景独立事件 ID |

**防重复**：所有点击均先过 `com.mistong.android.mediaplayer.widget.g.c(500L)`（500 ms 防抖）。

---

## 二、场景还原

### 1. 课后习题按钮点击（`Af` 方法）

| 通道 | 事件标识 | 关键参数 |
|---|---|---|
| monitor.c | `homeworkVideo.examClick` | `lessonId=G` |
| MoseEvent | `ewt_app_base_public_media_landscape_exercises_click` | `scene=作业`（仅横屏） |

```kotlin
// 伪代码
if (g.c(500)) return
monitor.c.b.n("homeworkVideo").c("examClick")
    .a(mapOf("lessonId" to G)).e()

if (sc()) {
    MoseEvent.i("ewt_app_base_public_media_landscape_exercises_click",
                mapOf("scene" to "作业"))
}
```

---

2. 视频播放完成（`od` 方法）

通道	事件标识	关键参数	
monitor.c	`homeworkVideo.playFinish`	`finished≥80%` / `hasExercise` / `isLastLesson`	
MoseEvent	`ewt_app_teacher_homeworkcenter_media_play_finish_expo`	同上，文本化（仅横屏）	

```kotlin
val finished = vf().g() >= 0.8f
monitor.c.b.n("homeworkVideo").c("playFinish")
    .a(mapOf(
        "finished" to finished,
        "hasExercise" to T1,
        "isLastLesson" to Yb()
    )).e()

if (sc()) {
    MoseEvent.i("ewt_app_teacher_homeworkcenter_media_play_finish_expo",
                listOf(
                    "finished" to if(finished) "已完成" else "未完成",
                    "hasExercise" to if(T1) "是" else "否",
                    "isLastLesson" to if(Yb()) "是" else "否"
                ))
}
```

---

3. 评价入口 & 引导浮层点击

场景	事件 ID	参数	
评价按钮	`ewt_app_base_public_media_appraise_click`	`videoBizCode=q5()` / `action=评价入口`	
评价浮层	同上	`action=评价浮层`	

```kotlin
MoseEvent.i("ewt_app_base_public_media_appraise_click",
            mapOf(
                "videoBizCode" to q5(),
                "action" to "评价入口"   // or "评价浮层"
            ))
```

---

4. 播放完成页按钮点击（`md` 方法）

按钮	monitor.c 事件	MoseEvent 事件（横屏）	action 值	
重新观看	`finishAndRePlay`	`ewt_app_teacher_homeworkcenter_media_play_finish_item_click`	重新观看	
课后习题	`finishAndExamClick`	同上	课后习题	
返回列表	`finishAndGoBack`	同上	返回任务列表	

```kotlin
when (finishAction.f()) {
    0 -> {
        monitor.c.b.n("homeworkVideo").c("finishAndRePlay")
            .a(mapOf("action" to "重新观看")).e()
        if (sc()) MoseEvent.i(event, mapOf("action" to "重新观看"))
    }
    2 -> {
        monitor.c.b.n("homeworkVideo").c("finishAndExamClick")
            .a(mapOf("action" to "课后习题")).e()
        if (sc()) MoseEvent.i(event, mapOf("action" to "课后习题"))
    }
    4 -> {
        monitor.c.b.n("homeworkVideo").c("finishAndGoBack")
            .a(mapOf("action" to "返回任务列表")).e()
        if (sc()) MoseEvent.i(event, mapOf("action" to "返回任务列表"))
    }
}
```

---

三、设计要点

1. 双通道  
   - `monitor.c`：服务端核心指标，必带 `lessonId`，用于学习完成率、习题转化率计算。  
   - `MoseEvent`：行为分析 & 埋点平台，横屏/竖屏独立事件，方便漏斗细分。

2. 参数一致性

   所有事件均携带：  
   - 视频 ID（`lessonId` / `videoBizCode`）  
   - 学生 ID（全局 `IAccountManager` 底层注入）  
   - 动作细分（`action` 枚举）  

3. 异常隔离

   上报块全部 `try-catch`，失败不影响播放/答题主流程。

4. 防抖机制

   500 ms 点击防抖，防止双击产生脏数据。

5. 场景化拆分

   同一功能（评价、完成页）通过 `action` 区分来源，实现 子维度 追踪。

---

四、快速验证 checklist

- 点击“课后习题”：  
  - 日志出现 `homeworkVideo.examClick` & `lessonId=xxx`  
  - 横屏时额外看到 `ewt_app_base_public_media_landscape_exercises_click`

- 视频播到 80%+ 自动结束：  
  - `homeworkVideo.playFinish` 带 `"finished":true`  
  - 横屏曝光 `ewt_app_teacher_homeworkcenter_media_play_finish_expo`

- 完成页任意按钮点击：  
  - 对应 `finishAndXXX` 事件 + `action` 值  
  - 横屏 `finish_item_click` 相同 `action`

---

> 接入新按钮或新场景时，直接复用上述模板即可：monitor.c 负责业务指标，MoseEvent 负责行为埋点，参数保持同源，action 细化即可。

```