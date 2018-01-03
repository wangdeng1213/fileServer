package com.jmw.filesite.training.commons.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipUtils
{
  public static final byte[] MAGIC_NUMBER = { 80, 75, 3, 4 };

  public static boolean isZipFile(byte[] rawZipFile)
  {
    if (rawZipFile.length < MAGIC_NUMBER.length) {
      return false;
    }
    for (int i = 0; i < MAGIC_NUMBER.length; i++) {
      if (MAGIC_NUMBER[i] != rawZipFile[i]) {
        return false;
      }
    }
    return true;
  }

  public static byte[] toRawZipFile(List<ZipEntry> entries, List<byte[]> files)
    throws IOException
  {
    if (entries.size() != files.size()) {
      return null;
    }
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ZipOutputStream zip = new ZipOutputStream(bytes);
    Iterator entriesItr = entries.iterator();
    Iterator filesItr = files.iterator();
    while (entriesItr.hasNext()) {
      byte[] file = (byte[])filesItr.next();
      ZipEntry entry = (ZipEntry)entriesItr.next();
      zip.putNextEntry(entry);
      zip.write(file, 0, file.length);
    }
    zip.close();
    return bytes.toByteArray();
  }

  public static List<ZipEntry> toZipEntryList(byte[] rawZipFile) throws IOException
  {
    ArrayList entries = new ArrayList();
    ByteArrayInputStream bytes = new ByteArrayInputStream(rawZipFile);
    ZipInputStream zip = new ZipInputStream(bytes);
    ZipEntry entry = zip.getNextEntry();
    while (entry != null) {
      entries.add(entry);
      entry = zip.getNextEntry();
    }
    zip.close();
    return entries;
  }

  public static List<byte[]> toByteArrayList(byte[] rawZipFile) throws IOException
  {
    ArrayList files = new ArrayList();
    ByteArrayInputStream bytes = new ByteArrayInputStream(rawZipFile);
    ZipInputStream zip = new ZipInputStream(bytes);

    ZipEntry entry = zip.getNextEntry();
    while (entry != null) {
      ByteArrayOutputStream file = new ByteArrayOutputStream();
      byte[] buf = new byte[4096];
      int len;
      while ((len = zip.read(buf, 0, 4096)) != -1)
      {
        file.write(buf, 0, len);
      }
      files.add(file.toByteArray());
      entry = zip.getNextEntry();
    }
    zip.close();
    return files;
  }

  public static byte[] readZipByte(File[] srcfile) {
    ByteArrayOutputStream tempOStream = new ByteArrayOutputStream(1024);
    byte[] tempBytes = (byte[])null;
    byte[] buf = new byte[1024];
    try {
      ZipOutputStream out = new ZipOutputStream(tempOStream);
      for (int i = 0; i < srcfile.length; i++) {
        FileInputStream in = new FileInputStream(srcfile[i]);
        out.putNextEntry(new ZipEntry(srcfile[i].getName()));
        int len;
        while ((len = in.read(buf)) > 0)
        {
          out.write(buf, 0, len);
        }
        out.closeEntry();
        in.close();
      }
      tempOStream.flush();
      out.close();
      tempBytes = tempOStream.toByteArray();
      tempOStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return tempBytes;
  }

  public static byte[] zipFiles(Map<String, byte[]> files)
    throws Exception
  {
    ByteArrayOutputStream dest = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(
      new BufferedOutputStream(dest));
    byte[] data = new byte[2048];
    Iterator itr = files.keySet().iterator();
    while (itr.hasNext()) {
      String tempName = (String)itr.next();
      byte[] tempFile = (byte[])files.get(tempName);

      ByteArrayInputStream bytesIn = new ByteArrayInputStream(tempFile);
      BufferedInputStream origin = new BufferedInputStream(bytesIn, 2048);
      ZipEntry entry = new ZipEntry(tempName);
      out.putNextEntry(entry);
      int count;
      while ((count = origin.read(data, 0, 2048)) != -1)
      {
        out.write(data, 0, count);
      }
      bytesIn.close();
      origin.close();
    }
    out.close();
    byte[] outBytes = dest.toByteArray();
    dest.close();
    return outBytes;
  }

  public static byte[] zipEntriesAndFiles(Map<ZipEntry, byte[]> files) throws Exception
  {
    ByteArrayOutputStream dest = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(
      new BufferedOutputStream(dest));
    byte[] data = new byte[2048];
    Iterator itr = files.keySet().iterator();
    while (itr.hasNext()) {
      ZipEntry entry = (ZipEntry)itr.next();
      byte[] tempFile = (byte[])files.get(entry);
      ByteArrayInputStream bytesIn = new ByteArrayInputStream(tempFile);
      BufferedInputStream origin = new BufferedInputStream(bytesIn, 2048);
      out.putNextEntry(entry);
      int count;
      while ((count = origin.read(data, 0, 2048)) != -1)
      {
        out.write(data, 0, count);
      }
      bytesIn.close();
      origin.close();
    }
    out.close();
    byte[] outBytes = dest.toByteArray();
    dest.close();
    return outBytes;
  }

  public static Map<String, byte[]> unzipFiles(byte[] zipBytes)
    throws IOException
  {
    InputStream bais = new ByteArrayInputStream(zipBytes);
    ZipInputStream zin = new ZipInputStream(bais);

    Map extractedFiles = new HashMap();
    ZipEntry ze;
    while ((ze = zin.getNextEntry()) != null)
    {
      ByteArrayOutputStream toScan = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int len;
      while ((len = zin.read(buf)) > 0)
      {
        toScan.write(buf, 0, len);
      }
      byte[] fileOut = toScan.toByteArray();
      toScan.close();
      extractedFiles.put(ze.getName(), fileOut);
    }
    zin.close();
    bais.close();
    return extractedFiles;
  }

  public static Map<String, byte[]> unzipFiles(InputStream bais) throws IOException
  {
    ZipInputStream zin = new ZipInputStream(bais);

    Map extractedFiles = new HashMap();
    ZipEntry ze;
    while ((ze = zin.getNextEntry()) != null)
    {
      ByteArrayOutputStream toScan = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int len;
      while ((len = zin.read(buf)) > 0)
      {
        toScan.write(buf, 0, len);
      }
      byte[] fileOut = toScan.toByteArray();
      toScan.close();
      extractedFiles.put(ze.getName(), fileOut);
    }
    zin.close();
    bais.close();
    return extractedFiles;
  }
}