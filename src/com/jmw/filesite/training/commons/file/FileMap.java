package com.jmw.filesite.training.commons.file;

public class FileMap
{
  private String fileName = "";
  private Long fileSize;
  private byte[] fileData = null;
  private String status = "";

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Long getFileSize() {
    return this.fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }

  public byte[] getFileData() {
    return this.fileData;
  }

  public void setFileData(byte[] fileData) {
    this.fileData = fileData;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}