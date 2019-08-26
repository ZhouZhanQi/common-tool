package com.mengcc.core.utils.http;

import java.io.InputStream;

/**
 * @author zhouzq
 * @date 2018-08-21
 */
public class FileInputStreamPart {

    //文件名
    private String filename;

    //文件流
    private InputStream data;

    public static Builder custom() {
        return new Builder();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public static class Builder {
        private String filename;
        private InputStream data;

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder data(InputStream data) {
            this.data = data;
            return this;
        }

        public FileInputStreamPart build() {
            FileInputStreamPart part = new FileInputStreamPart();
            part.setFilename(this.filename);
            part.setData(this.data);
            return part;
        }
    }
}
