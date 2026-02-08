package com.anatdx.murasaki;

import com.anatdx.murasaki.utils.MultiLocaleEntity;

public class Helps {

    public static final MultiLocaleEntity APPS = new MultiLocaleEntity();
    public static final MultiLocaleEntity HOME = new MultiLocaleEntity();
    public static final MultiLocaleEntity DOWNLOAD = new MultiLocaleEntity();
    public static final MultiLocaleEntity SUI = new MultiLocaleEntity();

    static {
        APPS.put("zh-CN", "https://shizuku.rikka.app/zh-hans/apps/");
        APPS.put("zh-TW", "https://shizuku.rikka.app/zh-hant/apps/");
        APPS.put("en", "https://shizuku.rikka.app/apps/");

        HOME.put("zh-CN", "https://shizuku.rikka.app/zh-hans/");
        HOME.put("zh-TW", "https://shizuku.rikka.app/zh-hant/");
        HOME.put("en", "https://shizuku.rikka.app/");

        DOWNLOAD.put("zh-CN", "https://shizuku.rikka.app/zh-hans/download/");
        DOWNLOAD.put("zh-TW", "https://shizuku.rikka.app/zh-hant/download/");
        DOWNLOAD.put("en", "https://shizuku.rikka.app/download/");

        SUI.put("en", "https://github.com/RikkaApps/Sui");
    }
}
