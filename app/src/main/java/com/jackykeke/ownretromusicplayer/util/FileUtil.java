package com.jackykeke.ownretromusicplayer.util;

import java.io.File;
import java.io.IOException;

/**
 * @author keyuliang on 2022/9/21.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public final class FileUtil {

    private FileUtil(){

    }


    public static String safeGetCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        }catch (IOException e){
            e.printStackTrace();
            return file.getAbsolutePath();
        }
    }
}
