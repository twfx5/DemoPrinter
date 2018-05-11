package com.jc.printer;

import android.Manifest;

/*
* 文件名：Constant
* 描    述：全局常量
* 作    者：杨兴
* 时    间：2018.03.15
* 版    权：武汉精臣智慧标识科技有限公司
*/
public class Constant {

    public final static String BLUETOOTH_STATUS = "bluetooth_status";

    // 用户信息
    public final static String USER_INFO = "user_info";

    // 是否已登录
    public final static String IS_LOGIN = "is_login";

    // 保存着上传图片的路径
    public final static String UPLOAD_PICTURE_PATH = "/imageUpload/";

    // json格式数据缓存的路径
    public final static String JSON_DATA_CACHE_PATH = "/jsonCache/";

    // 保存报错信息文件的路径
    public final static String CRASH_ERROR_FILE_PATH = "/crash/";

    // 下载文件的路径
    public final static String DOWNLOAD_FILE_PATH = "/download/";

    // 图片缓存的路径
    public final static String IMAGE_CACHE_PATH = "/imageCache/";

    // 系统可用RAM内存大小
    public static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    // 磁盘缓存大小限制
    public static final int MAX_DISK_CACHE_SIZE = 40 * 1024 * 1024; // 40M

    // RAM内存缓存大小限制
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;

    // 网络请求错误标识码
    public final static String NAME_PASSWORD_ERROR = "name_password_error"; // 用户名或者密码错误
    public final static String PARAM_ERROR = "param_error"; // 传入参数错误
    public final static String SERVER_ERROR = "server_error"; // 服务器异常
    public final static String URL_ERROR = "url_error"; // url有误
    public final static String UNKNOW_ERROR = "unknow_error"; // 未知异常
    public final static String DATA_ACCESS_ERROR = "data_access_error"; // 接口数据层异常
    public final static String DATA_REQUEST_ERROR = "app_request_error"; // 请求异常

    /**
     * 权限
     */
    public static String[] PERMISSIONSTR = new String[]
            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.LOCATION_HARDWARE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.BLUETOOTH};

    public final static String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public final static String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public final static String LOCATION_HARDWARE = Manifest.permission.LOCATION_HARDWARE;
    public final static String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public final static String WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS;
    public final static String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public final static String CAMERA = Manifest.permission.CAMERA;
    public final static String BLUETOOTH = Manifest.permission.BLUETOOTH;
    public static final int RESULT_SCAN_PERMISSIONSTR_TAG = 200;//扫描返回标识
    public static final int RESULT_VOICE_PERMISSIONSTR_TAG = 201;//语音识别返回标识
    public static final int RESULT_OCR_PERMISSIONSTR_TAG = 202;//图片识别返回标识
    public static final int RESULT_OCR_NO_PERMISSIONSTR_TAG = 203;//图片识别费手写返回标识

    // 打印横向偏移量
    public final static String PRINT_HON_OFFSET = "print_hon_offset";

    // 打印竖向偏移量
    public final static String PRINT_VER_OFFSET = "print_ver_offset";

    // 打印横向偏移量
    public final static String PRINT_TAG_X = "print_tag_x";

    // 打印竖向偏移量
    public final static String PRINT_TAG_Y = "print_tag_y";

    public final static String PRINT_TAG_TYPE = "print_tag_type";    // 打印标签类型
    public final static String PRINT_TAG_ROTATE = "print_tag_rotate";    // 打印翻转
    public final static String PRINT_TAG_CABLE_DIRECTION = "print_tag_cable_direction";    // 打印线缆标签尾巴方向
    public final static String PRINT_TAG_CABLE_LENGTH = "print_tag_cable_length";    // 打印线缆尾巴 长度

    // 打印张数
    public final static String PRINT_NUM = "print_num";

    // 打印黑度
    public final static String PRINT_BACK = "print_back";

    // 打印连续标签长度
    public final static String PRINT_SCALE = "print_scale";

    // 行业Id
    public final static String INDUSTRY_ID = "industry_id";

    // 模版Id
    public final static String TEMPLATE_ID = "template_id";

    // 模版类型Id
    public final static String TEMPLATE_TYPE_ID = "template_type_id";

    // 模版名称
    public final static String TEMPLATE_NAME = "template_name";

    // 模版关键字
    public final static String TEMPLATE_NAME_KEY = "template_name_key";

    // 分类Id
    public final static String CATEGORY_ID = "category_id";

    // token
    public final static String TOKEN = "token";

    // versionCode
    public final static String VERSION_CODE = "versionCode";

    // UpdateUrl
    public final static String UDDATE_URL = "UpdateUrl";

    // UpdateContent
    public final static String UDDATE_CONTENT = "UpdateContent";

    public static final int RESULT_TEMPLATE_TAG = 100;//画板返回标识
    public static final int RESULT_SCAN_TAG = 101;//扫描返回标识
    public static final int RESULT_VOICE_TAG = 102;//语音识别返回标识
    public static final int RESULT_OCR_TAG = 103;//图片识别返回标识
    public static final int RESULT_OCR_NO_TAG = 112;//图片识别费手写返回标识
    public static final int RESULT_PRINT_TAG = 104;//画板返回标识
    public static final int RESULT_BINDING_TAG = 105;//绑定返回标识
    public static final String RESULT_TAG = "RESULT_TAG";
    public static final String RESULT_CONTENT = "RESULT_CONTENT";

    public static final int REQUEST_CODE_GENERAL_BASIC = 106;
    public static final int REQUEST_CODE_ACCURATE_BASIC = 107;
    public static final int REQUEST_CODE_GENERAL_ENHANCED = 109;
    public static final int REQUEST_CODE_GENERAL_WEBIMAGE = 110;

    // ocrStr
    public final static String OCR_STR = "ocrStr";

    public static String ACTION_EXIT_APP = "ACTION_EXIT_APP";

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_NAME = "device_name";
}
