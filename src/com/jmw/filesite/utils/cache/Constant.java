package com.jmw.filesite.utils.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Constant
{
  public static String UploadFileRootPath = getJBPMPath();
  public static String SharePath = getSharePath();
  public static String OfficeHome = getOfficeHome();
  public static String Pdf2SwfPath = getPdf2SwfPath();
  public static String SwfSavePath = getSwfSavePath();
  public static String CertFilePath = getCertFilePath();

  private static String getJBPMPath()
  {
    if (UploadFileRootPath == null) {
      Properties prop = new Properties();
      InputStream in = Constant.class
        .getResourceAsStream("/file.properties");
      try {
        prop.load(in);
        UploadFileRootPath = prop.getProperty("fileUploadPath").trim();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return UploadFileRootPath;
  }
  
  private static String getSharePath()
  {
    if (SharePath == null) {
      Properties prop = new Properties();
      InputStream in = Constant.class
        .getResourceAsStream("/file.properties");
      try {
        prop.load(in);
        SharePath = prop.getProperty("sharePath").trim();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return SharePath;
  }
  
  private static String getOfficeHome()
  {
    if (OfficeHome == null) {
      Properties prop = new Properties();
      InputStream in = Constant.class
        .getResourceAsStream("/file.properties");
      try {
        prop.load(in);
        OfficeHome = prop.getProperty("officeHome").trim();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return OfficeHome;
  }
  
  private static String getPdf2SwfPath()
  {
    if (Pdf2SwfPath == null) {
      Properties prop = new Properties();
      InputStream in = Constant.class
        .getResourceAsStream("/file.properties");
      try {
        prop.load(in);
        Pdf2SwfPath = prop.getProperty("pdf2SwfPath").trim();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return Pdf2SwfPath;
  }
  
  private static String getSwfSavePath()
  {
    if (SwfSavePath == null) {
      Properties prop = new Properties();
      InputStream in = Constant.class
        .getResourceAsStream("/file.properties");
      try {
        prop.load(in);
        SwfSavePath = prop.getProperty("swfSavePath").trim();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return SwfSavePath;
  }
  private static String getCertFilePath() {
		if (CertFilePath == null) {
			Properties prop = new Properties();
			InputStream in = Constant.class
					.getResourceAsStream("/file.properties");
			try {
				prop.load(in);
				CertFilePath = prop.getProperty("certFilePath");
				CertFilePath = (CertFilePath == null ? "" : CertFilePath.trim());
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return CertFilePath;
	}
}