package com.jmw.filesite.web.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

public class OosFile extends HttpServlet
{
  private String tempName="";
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String filePath="";
    String newFilePath="";//文件新路径
    String fileName="";
	try {
		filePath = new AesCBC().Decrypt(request.getParameter("p").replace(" ", "+"));
	}
	catch (Exception e1) {
	}
	
	filePath=Constant.UploadFileRootPath+filePath;
    File obj = new File(filePath);
    if (!obj.exists()) {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print("<script>alert('指定文件不存在！')</script>");
      return;
    }
    
    String nounzip=request.getParameter("nounzip");
    //解压文件
    byte[] outBytes=null;
    try {
	    byte[] unzipByte = FileUtils.readFileByte(filePath);
	    
	    if(null==nounzip || "".equals(nounzip)){
	    	outBytes= unzipFiles(unzipByte, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
	    	  newFilePath = filePath.substring(0,filePath.lastIndexOf("."));
	 	    File dir = new File(newFilePath);
	 		if (!dir.exists()) {// 判断目录是否存在
	 			dir.mkdirs();
	 		}
	 	    newFilePath=newFilePath.replaceAll("\\\\","/");
	 	    //A-%E4%B8%BB%E4%BD%93%E5%B7%A5%E7%A8%8B-%5B%5D-%E9%A1%B9%E7%9B%AE%E5%BB%BA%E8%AE%AE%E4%B9%A6%28%E5%92%A8%E8%AF%A2%29-222-M--20170627.docx
	 	    BufferedInputStream bis = null;
	 	    BufferedOutputStream bos = null;
	 	    tempName=decodeUrl(tempName);
	 	    tempName= tempName.replace("-","").replace("[", "").replace("]", "").replace(" ", "");
	 	   // tempName=encodeName(tempName,request);
	 	    File file = new File(newFilePath+"/"+tempName);
	 		if (file.exists()) {// 判断文件是否存在
	 			System.out.println("目标文件已存在" + filePath);
	 			fileName =URLEncoder.encode(tempName,"utf-8");
	 			//return false;
	 		}else{
	 			file.createNewFile();
	 			OutputStream fos =new FileOutputStream(file);
	 			fileName =URLEncoder.encode(tempName,"utf-8");
	 		    try {
	 		    	 if(null==nounzip || "".equals(nounzip)){
	 		       	  bis = new BufferedInputStream(new ByteArrayInputStream(outBytes));
	 		         }else{
	 		       	  FileInputStream fis=new FileInputStream(filePath);
	 		       	  bis = new BufferedInputStream(fis);
	 		         }
	 		     // bis = new BufferedInputStream(new ByteArrayInputStream(outBytes));
	 		      byte[] buff = new byte[2048];
	 		      int bytesRead;
	 		      while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
	 		      {
	 		        fos.write(buff, 0, bytesRead);
	 		      }
	 		    } catch (IOException e) {
	 		      throw e;
	 		    } finally {
	 		      if (bis != null)
	 		        bis.close();
	 		      if (bos != null)
	 		        bos.close();
	 		      if (fos != null)
	 		    	  fos.close();
	 		    }
	 		}
	    }else{
	    	newFilePath = filePath.substring(0,filePath.lastIndexOf("/"));
	    	 newFilePath=newFilePath.replaceAll("\\\\","/");
	    	fileName =filePath.substring(filePath.lastIndexOf("/")+1);
	    }
	} catch(Exception ex) {
		ex.printStackTrace();
	}
   /* //解压文件
    byte[] outBytes=null;
    try {
	    byte[] unzipByte = FileUtils.readFileByte(filePath);
	    outBytes= unzipFiles(unzipByte, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
	} catch(Exception ex) {
		ex.printStackTrace();
	}*/
    
  //  ServletOutputStream out = response.getOutputStream();
    //截取filePath路径
    //d:\FTPFolder\8080/8080/4/prj/1011/865/6c1bc94e-199a-4675-ad6d-f603bd057326.zip
   
	//文件名进行转码
	String callback =request.getParameter("callback");
	String  fileInfo = callback+"({\"fileName\":\""+fileName+"\",\"pathName\":\""+newFilePath+"\"})";
  //String  fileInfo = "{\"fileName\":\""+"20170627.docx"+"\",\"pathName\":\""+"D:/FTPFolder/8080/8080/4/prj/1011/104"+"\"}";
	
	//String  fileInfo = "{\"fileName\":\""+fileName+"\",\"pathName\":\""+"D:/FTPFolder/8080/8080/4/prj/1011/1017"+"\"}";
  response.setCharacterEncoding("UTF-8");
  response.setContentType("application/json;charset=UTF-8");
 
     try {
          response.getWriter().write(fileInfo);
      } catch (IOException e) {
          e.printStackTrace();
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
  public String decodeUrl(String paramter)
  {
    try {
      return URLDecoder.decode(URLDecoder.decode(paramter, "UTF-8"), "UTF-8"); } catch (UnsupportedEncodingException e) {
    }
    throw new RuntimeException();
  }
}