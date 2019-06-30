package com.lx.lib.webviewcache.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lixiang on 2019/6/30.
 */
public final class WrapperInputStream extends InputStream {
    private InputStream inputStream;
    private OutputStream outputStream;

    public WrapperInputStream(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public int read() throws IOException {
        if (inputStream == null) {
            return -1;
        }
        int read = inputStream.read();
        if (read != -1) {
            outputStream.write(read);
            // TODO: 2019/6/30 提供回调，读取完成
        }
        return read;
    }
}
