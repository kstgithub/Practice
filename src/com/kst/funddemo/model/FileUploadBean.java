package com.kst.funddemo.model;

import java.io.File;

/***
 * 模型基类
 *
 */
public class FileUploadBean extends BaseBeen {
    File gzFile = null;
    public FileUploadBean() {
        setUrl("/ci/user/behavior/log/upload");
    }

    public void setFile(File gzFile){
        this.gzFile = gzFile;
    }

    public File getFile(){
        return gzFile;
    }
}
