package com.dema.versatile.lib.core.in;

public interface ICMHttpFile extends ICMObj {
    String getFilePath();

    String getName();

    String getFileName();

    String getContentDisposition();

    String getContentType();

    void setFilePath(String strFilePath);

    void setName(String strName);

    void setFileName(String strFileName);

    void setContentDisposition(String strContentDisposition);

    void setContentType(String strContentType);
}
