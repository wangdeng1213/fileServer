package com.jmw.filesite.web.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipInput;
import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;
import com.jmw.filesite.utils.winzipaes.AesZipFileDecrypter;
import com.jmw.filesite.utils.winzipaes.impl.AESDecrypterBC;
import com.jmw.filesite.utils.winzipaes.impl.ExtZipEntry;
import com.jmw.filesite.training.commons.file.FileUtils;

public class ReportOutputFile extends HttpServlet
{
  private String tempName="";
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String filePath="";
	try {
		filePath = new AesCBC().Decrypt(request.getParameter("p").replace(" ", "+"));
	}
	catch (Exception e1) {
		filePath = request.getParameter("p");
	}
	
	filePath=Constant.UploadFileRootPath+filePath;
    File obj = new File(filePath);
    if (!obj.exists()) {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print("<script>alert('指定文件不存在！')</script>");
      return;
    }

    //解压文件
    byte[] outBytes=null;
    try {
	    byte[] unzipByte = FileUtils.readFileByte(filePath);
	    outBytes= unzipFiles(unzipByte, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
	} catch(Exception ex) {
		ex.printStackTrace();
	}
    
    ServletOutputStream out = response.getOutputStream();
    
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    try {
      bis = new BufferedInputStream(new ByteArrayInputStream(outBytes));
      bos = new BufferedOutputStream(out);
      byte[] buff = new byte[2048];
      int bytesRead;
      while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
      {
        bos.write(buff, 0, bytesRead);
      }
    } catch (IOException e) {
      throw e;
    } finally {
      if (bis != null)
        bis.close();
      if (bos != null)
        bos.close();
    }
  }

  public byte[] unzipFiles(byte[] zipBytes, String password) throws IOException {
		InputStream bais = new ByteArrayInputStream(zipBytes);
		EncryptZipInput zin = new EncryptZipInput(bais,password);
		EncryptZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			ByteArrayOutputStream toScan = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = zin.read(buf)) > 0) {
				toScan.write(buf, 0, len);
			}
			byte[] fileOut = toScan.toByteArray();
			toScan.close();		
			tempName=ze.getName();
			return fileOut;
		}
		zin.close();
		bais.close();
		return null;
	}
  
  public String unZipFile(File inFile,OutputStream outFile) throws DataFormatException, IOException{
	String fileName="";
	AESDecrypterBC aesBC=new AESDecrypterBC();
	AesZipFileDecrypter aesDecrypter = new AesZipFileDecrypter(inFile,aesBC);
	List<ExtZipEntry> entryList = aesDecrypter.getEntryList();
	for(ExtZipEntry entry:entryList){
		fileName = entry.getName();
		aesDecrypter.extractEntry(entry, outFile, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
	}
	aesDecrypter.close();
	return fileName;
  }
}