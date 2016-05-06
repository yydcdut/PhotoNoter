package com.yydcdut.note.utils;

/**
 * Created by yuyidong on 15/7/16.
 */
public interface Const {
    /* intent */
    String PHOTO_POSITION = "photoPosition";
    String CATEGORY_ID_4_PHOTNOTES = "categoryId4PhotoNotes";
    String COMPARATOR_FACTORY = "camparator";
    String USER_DETAIL_TYPE = "user_detail_type";

    String WEBVIEW_URL = "webview_url";
    String WEBVIEW_TITLE = "webview_title";
    /* 动画 */
    int DURATION = 1000;
    int DURATION_ACTIVITY = 2000;
    int RADIUS = 10;
    /* 缩略图 */
    int SMALL_PHOTO_WIDTH = 500;
    /* 广播 */
    String BROADCAST_PHOTONOTE_UPDATE = "com.yydcdut.note.model.photonotedbmodel";
    String TARGET_BROADCAST_PROCESS = "target_broadcast_process";//bool
    String TARGET_BROADCAST_PHOTO = "target_broadcast_target";
    String TARGET_BROADCAST_SERVICE = "target_broadcast_target";
    String BROADCAST_CAMERA_SERVICE_KILL = "com.yydcdut.note.killcameraservice";

    /* Camera */
    String CAMERA_SIZE_WIDTH = "width";
    String CAMERA_SIZE_HEIGHT = "height";
    String CAMERA_BACK = "0";
    String CAMERA_FRONT = "1";
    int CAMERA_SANDBOX_PHOTO_RATIO_4_3 = 0;
    int CAMERA_SANDBOX_PHOTO_RATIO_1_1 = 1;
    int CAMERA_SANDBOX_PHOTO_RATIO_FULL = 2;

    int LAYOUT_MAIN_CAPTURE = 101;

    int LAYOUT_PERSONAL_RATIO_1_1 = 4200;
    int LAYOUT_PERSONAL_RATIO_4_3 = 4201;
    int LAYOUT_PERSONAL_RATIO_FULL = 4202;
    int LAYOUT_PERSONAL_TIMER_0 = 4203;
    int LAYOUT_PERSONAL_TIMER_3 = 4204;
    int LAYOUT_PERSONAL_TIMER_5 = 4205;
    int LAYOUT_PERSONAL_TIMER_10 = 4206;
    int LAYOUT_PERSONAL_TIMER_15 = 4207;

    int CAMERA_ID_REAR = 1000;
    int CAMERA_ID_FRONT = 1001;

    int CAMERA_PARAMS_GRID_OFF = 4210;
    int CAMERA_PARAMS_GRID_ON = 4211;

    int CAMERA_PARAMS_SOUND_OFF = 4220;
    int CAMERA_PARAMS_SOUND_ON = 4221;

    String MARKDOWN_TITLE = "Markdown 功能介绍";

    String MARKDOWN_CONTENT =
            "在这个版本中我们增加了 Markdown 功能。Markdown 是一种使用纯文本编写的标记语言，可以产生格式丰富的页面排版效果，比如突出标题、居中、加粗、引用和生成列表。\n" +
                    "\n" +
                    "## **用法与规则：**\n" +
//                    "\n" +
//                    "你可以手动输入，也可以点击键盘上方的按钮快速输入 Markdown 符号。\n" +
                    "\n" +
                    "### **标题**\n" +
                    "使用“#”加空格在行首来创建标题\n" +
                    "\n" +
                    "例如：\n" +
                    "# 一级标题\n" +
                    "## 二级标题\n" +
                    "### 三级标题\n" +
                    "\n" +
                    "### **加粗功能**\n" +
                    "使用一组星号“**”来加粗一段文字\n" +
                    "\n" +
                    "例如：\n" +
                    "这是**加粗的文字**\n" +
                    "\n" +
                    "### **居中**\n" +
                    "使用一对中括号“[文字]”来居中一段文字，也可以和标题叠加使用\n" +
                    "\n" +
                    "例如：\n" +
                    "[### 这是一个居中的标题]\n" +
                    "\n" +
                    "### **引用**\n" +
                    "使用“> ”在段首来引用一段文字\n" +
                    "\n" +
                    "例如：\n" +
                    "> 这是一段引用\n" +
                    "> 这是一段引用\n" +
                    "\n" +
                    "### **无序列表**\n" +
                    "使用 “-”、“*”或“+”加空格 来创建无序列表\n" +
                    "\n" +
                    "例如：\n" +
                    "- 这是一个无序列表\n" +
                    "+ 这是一个无序列表\n" +
                    "* 这是一个无序列表\n" +
                    "\n" +
                    "### **有序列表**\n" +
                    "使用 数字圆点加空格 如“1. ”、“2. ”来创建有序列表\n" +
                    "\n" +
                    "例如：\n" +
                    "1. 这是一个有序列表\n" +
                    "2. 这是一个有序列表\n" +
                    "3. 这是一个有序列表";

    String INTRODUCE_TITLE = "App 功能简介";

    String INTRODUCE_CONTENT =
            "## 欢迎界面\n" +
                    "\n" +
                    "可以在设置中关闭欢迎界面\n" +
                    "\n" +
                    "## 相册界面\n" +
                    "\n" +
                    "已开始进入相册界面是非选择界面，菜单选项为“新建分类”、“排序类型”、“进入选择状态”、“设置”，当单击图片之后进入详情页面；\n" +
                    "\n" +
                    "长点击某张图片便可以进入选择界面。当进入选择界面的时候，菜单变为“全选”、“移动到其他分类”、“删除”，当单击其他图片时，图片状态变为选中或者非选中状态。\n" +
                    "\n" +
                    "## 功能选择\n" +
                    "\n" +
                    "当点击悬浮按钮时弹出功能选择框，依次是“拍照”、“本地”，拍照可使用系统相机，但是如果这样，每次只能拍一张照片，而使用App相机，可以连续多拍，更多相机参数选项等，可以在设置中选择相机功能；接下来是本地，本地是将本地的照片复制到 照片笔记 当中来。\n" +
                    "\n" +
                    "## 分类界面\n" +
                    "\n" +
                    "当从屏幕最左边往右滑动或者点击最左上角汉堡菜单按钮的时候弹出分类界面，在这里可以登录第三方帐号、可以查看和选择分类、查看云空间大小。\n" +
                    "\n" +
                    "## 登录界面\n" +
                    "\n" +
                    "登录界面中可以第三方平台登录，提供多种登录方式，同时可以登录多格帐号。\n" +
                    "\n" +
                    "## 设置界面\n" +
                    "\n" +
                    "设置界面中用户可以根据自己的喜好选择主题、选择字体、编辑分类、处理第三方帐号、设置相机等。\n" +
                    "\n" +
                    "## 编辑分类\n" +
                    "\n" +
                    "设置里面的编辑分类功能，将用户创建的分类全部显示在这里，分别左滑和右滑显示不同的菜单。\n" +
                    "\n" +
                    "## App相机界面\n" +
                    "\n" +
                    "App相机中上方按钮分别是 闪光灯、相机预览拍照比例、延时拍照、网格和切换摄像头，迪昂机预览区域可以聚焦，同时可以调节曝光。\n" +
                    "\n" +
                    "## 详情界面\n" +
                    "\n" +
                    "在详情界面中文字部分如果\b每天显示全的话可往上滑动，文字部分一次是“标题”、“内容”和“时间”。点击图片进入图片详情界面；点击悬浮按钮进入文字编辑界面。\n" +
                    "\n" +
                    "或者点击 **三个点** 的按钮，查看照片的Exif信息；点击悬浮按钮可以查看地图坐标。\n" +
                    "\n" +
                    "## 图片详情界面\n" +
                    "\n" +
                    "在图片详情界面中可以对图片进行滤镜处理。\n" +
                    "\n";

}
