package com.bdc.ppjoker.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bdc.ppjoker.model.BottomBar;
import com.bdc.ppjoker.model.Destination;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {
    private static HashMap<String, Destination> sDestMap;
    private static BottomBar sBottomBarConfig;

    public static HashMap<String, Destination> getDestConfig() {
        if (sDestMap == null) {
            String content = parseAssetFile("destnation.json");
            sDestMap = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            });
        }
        return sDestMap;
    }

    public static BottomBar getBottomBarConfig() {
        if (sBottomBarConfig == null) {
            String content = parseAssetFile("main_tabs_config.json");
            sBottomBarConfig = JSON.parseObject(content, BottomBar.class);
        }
        return sBottomBarConfig;
    }

    private static String parseAssetFile(String fileName) {
        AssetManager assetManager = AppGlobal.getApplication().getAssets();
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = assetManager.open(fileName);
            String line = null;
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return sb.toString();
    }

}
