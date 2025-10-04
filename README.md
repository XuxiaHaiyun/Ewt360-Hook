# Ewt360 Hook

增强升学e网通使用体验的 Xposed 模块。

## ⚠️ 免责声明

本模块仅供学习研究使用，请勿用于商业用途。使用本模块可能违反升学e网通的服务条款，请自行承担使用风险。开发者不对因使用本模块造成的任何后果负责。请合理使用本模块，不要影响正常的学习。

## 🚀 功能特性

- ✅ **自动跳过专注度检测** - 自动绕过学习过程中的专注度检测环节
- ✅ **视频倍速增强** - 将原有的0.8倍速限制改为最高20倍速播放

## 📋 使用要求

- Android 5.0+ 已获取 ROOT 设备
- Xposed框架（推荐LSPosed）
- 升学e网通 11.2.5 App

## 🔧 安装步骤

1. **安装Xposed框架**
   - 推荐使用 [LSPosed](https://github.com/LSPosed/LSPosed)
   - 需要Magisk环境

2. **下载模块**
   - 从Release页面下载最新版本的APK文件

3. **安装模块**
   - 安装下载的APK文件
   - 在模块管理器(Xposed Installer)中启用该模块
   - 重启设备

4. **配置作用域**
   - 在LSPosed中选择升学e网通作为作用域
   - 重启升学e网通App

## 🎯 使用说明

1. 模块激活后，功能会自动生效
2. 专注度检测将自动跳过
3. 视频播放界面可选择更高倍速（最高20倍）

## 🛠️ 技术实现

### 模块架构

- **入口类**: `com.xxhy.ewt360hook.MainHook`
- **Hook点**: 专注度检测方法、视频播放控制器
- **适配版本**: 升学e网通 11.2.5

### 🔍 反编译信息

- 包名: com.mistong.ewt360
- 加固：爱加密企业版（部分方法native化）
- 主要类: 
  - NewHomeworkVideoPlayActivity - 视频播放活动
  - NewHomeworkMediaPlayFragment - 视频播放片段（负责视频播放相关UI渲染与交互逻辑，常与播放活动联动）
  - com.mistong.android.mediaplayer.widget.MstVideoView - 视频播放器组件
  - AbstractMediaController - 媒体控制抽象类（定义播放、暂停、进度调节等核心控制方法，为具体控制器提供基础模板）

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 开源协议

[GPLv3 License](LICENSE)
