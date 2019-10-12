package com.lx.lib.java;

import java.io.File;

/**
 * Created by glennli on 2019/10/12.<br/>
 */
public final class FileUtil {
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                if (file.isDirectory()) {
                    File[] childFile = file.listFiles();
                    if (childFile == null || childFile.length == 0) {
                        return;
                    }

                    File[] var2 = childFile;
                    int var3 = childFile.length;

                    for (int var4 = 0; var4 < var3; ++var4) {
                        File f = var2[var4];
                        deleteFile(f);
                    }
                }
            }
        }
    }
}
