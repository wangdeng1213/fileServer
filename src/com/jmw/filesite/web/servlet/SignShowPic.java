package com.jmw.filesite.web.servlet;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipInput;
import com.jmw.filesite.training.commons.file.FileUtils;
import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;
import com.jmw.filesite.utils.winzipaes.AesZipFileDecrypter;
import com.jmw.filesite.utils.winzipaes.impl.AESDecrypterBC;
import com.jmw.filesite.utils.winzipaes.impl.ExtZipEntry;

public class SignShowPic extends HttpServlet
{
	private String tempName="";
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
	  if(request.getParameter("p") == null || request.getParameter("p").equals("")){
		  return;
	  }
	  String filePath="";
	  try {		 
		filePath = new AesCBC().Decrypt(URLDecoder.decode(request.getParameter("p")).replace(" ", "+"));
	} catch (Exception e1) {
		e1.printStackTrace();
	}

	//filePath=Constant.CertFilePath+filePath;
    File obj = new File(filePath);
    if (!obj.exists()) {
      return;
    }

    String fileName = "";
    byte[] outBytes = FileUtils.readFileByte(filePath);
    String newFilePath = filePath.substring(0,filePath.lastIndexOf("."));
    fileName = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
	//newFilePath=newFilePath.replaceAll("\\\\","/");
    ServletOutputStream out = response.getOutputStream();
    response.setContentType("image/jpg");
    response.setHeader("Content-disposition", "inline;filename=\"" + encodeName(fileName,request) + "\"");
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
        out.close();
    }
  }

  public String decodeUrl(String paramter)
  {
    try {
      return URLDecoder.decode(URLDecoder.decode(paramter, "UTF-8"), "UTF-8"); } catch (UnsupportedEncodingException e) {
    }
    throw new RuntimeException();
  }

  public String encodeUrl(String paramter)
  {
    	try {
			return URLEncoder.encode(paramter, "UTF-8")
					.replaceAll("%5B", "[").replaceAll("%5D", "]")
					.replaceAll("%28", "(").replaceAll("%29", ")")
					.replaceAll("%23", "#");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return paramter;
		}
  }
  
  public String encodeName(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
	   String agent=request.getHeader("USER-AGENT");
	   fileName = fileName.replace("+", "%20");
	   String enableFileName = fileName;
	   if(agent != null && agent.indexOf("Firefox") != -1){
		   enableFileName=MimeUtility.encodeText(fileName);
	   }else{
		   enableFileName = encodeUrl(enableFileName);
	   }
	   return enableFileName;
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
  
  public String unZipFile(File inFile,OutputStream outFile) throws ZipException, IOException, DataFormatException{
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