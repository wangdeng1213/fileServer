package com.jmw.filesite.training.commons.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.activation.DataHandler;

public class FileUtils
{
  public static File[] getFileList(String fileDir)
  {
    File dir = new File(fileDir);
    for (String children : dir.list()) {
      System.out.println(children);
    }
    return dir.listFiles();
  }

  public static byte[] readFileByte(File file)
  {
    FileInputStream fis = null;
    FileChannel fc = null;
    byte[] data = (byte[])null;
    try {
      fis = new FileInputStream(file);
      fc = fis.getChannel();
      data = new byte[(int)fc.size()];
      fc.read(ByteBuffer.wrap(data));
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();

      if (fc != null) {
        try {
          fc.close();
        }
        catch (IOException ex) {
          ex.printStackTrace();
        }
      }
      if (fis != null)
        try {
          fis.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
    }
    catch (IOException e)
    {
      e.printStackTrace();

      if (fc != null) {
        try {
          fc.close();
        }
        catch (IOException ex) {
          ex.printStackTrace();
        }
      }
      if (fis != null)
        try {
          fis.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
    }
    finally
    {
      if (fc != null) {
        try {
          fc.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return data;
  }

  public static byte[] readFileByte(String filename)
    throws IOException
  {
    if ((filename == null) || (filename.equals(""))) {
      throw new NullPointerException("无效的文件路径");
    }
    File file = new File(filename);
    long len = file.length();
    byte[] bytes = new byte[(int)len];

    BufferedInputStream bufferedInputStream = new BufferedInputStream(
      new FileInputStream(file));
    int r = bufferedInputStream.read(bytes);
    if (r != len)
      throw new IOException("读取文件不正确");
    bufferedInputStream.close();

    return bytes;
  }

  public static String writeByteFile(byte[] bytes, File file)
  {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      fos.write(bytes);
    } catch (FileNotFoundException e) {
      e.printStackTrace();

      if (fos != null)
        try {
          fos.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
    }
    catch (IOException e)
    {
      e.printStackTrace();

      if (fos != null)
        try {
          fos.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
    }
    finally
    {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
    return "success";
  }

  public static void moveFile(String fromDir, String toDir, String errDir)
  {
    try
    {
      File destDir = new File(toDir);
      if (!destDir.exists()) {
        destDir.mkdirs();
      }

      for (File file : new File(fromDir).listFiles())
        if (file.isDirectory()) {
          moveFile(file.getAbsolutePath(), toDir + File.separator + 
            file.getName(), errDir);
          file.delete();
          System.out.println("文件夹" + file.getName() + "删除成功");
        } else {
          File moveFile = new File(toDir + File.separator + 
            file.getName());
          if (moveFile.exists()) {
            moveFileToErrDir(moveFile, errDir);
          }
          file.renameTo(moveFile);
          System.out.println("文件" + moveFile.getName() + "转移到错误目录成功");
        }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void moveFileToErrDir(File moveFile, String errDir)
  {
    int i = 0;
    String errFile = errDir + File.separator + "rnError" + 
      moveFile.getName();
    while (new File(errFile).exists()) {
      i++;
      errFile = errDir + File.separator + i + "rnError" + 
        moveFile.getName();
    }
    moveFile.renameTo(new File(errFile));
  }

  public static byte[] getFileByte(InputStream in)
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
    try {
      copy(in, out);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out.toByteArray();
  }

  private static void copy(InputStream in, OutputStream out)
    throws IOException
  {
    try
    {
      byte[] buffer = new byte[4096];
      int nrOfBytes = -1;
      while ((nrOfBytes = in.read(buffer)) != -1) {
        out.write(buffer, 0, nrOfBytes);
      }
      out.flush();
    }
    catch (IOException localIOException)
    {
      try {
        if (in != null)
          in.close();
      }
      catch (IOException localIOException1) {
      }
      try {
        if (out != null)
          out.close();
      }
      catch (IOException localIOException2)
      {
      }
    }
    finally
    {
      try
      {
        if (in != null)
          in.close();
      }
      catch (IOException localIOException3) {
      }
      try {
        if (out != null)
          out.close();
      }
      catch (IOException localIOException4)
      {
      }
    }
  }

  public static boolean writeDataHandlerToFile(DataHandler attachinfo, String filename)
  {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(filename);
      writeInputStreamToFile(attachinfo.getInputStream(), fos);
      fos.close();
    } catch (Exception e) {
      return false;
    } finally {
      if (fos != null)
        try {
          fos.close();
        }
        catch (Exception localException2) {
        }
    }
    return true;
  }

  private static void writeInputStreamToFile(InputStream is, OutputStream os) throws Exception
  {
    int n = 0;
    byte[] buffer = new byte[8192];
    while ((n = is.read(buffer)) > 0)
      os.write(buffer, 0, n);
  }
}