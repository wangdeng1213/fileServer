package com.jmw.filesite.web.servlet;

import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;
import com.jmw.filesite.utils.winzipaes.AesZipFileEncrypter;
import com.jmw.filesite.utils.winzipaes.impl.AESEncrypterBC;
import com.jmw.filesite.training.commons.file.FileUtils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipOutput;


import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;


public class OnlineAuditingUpload extends HttpServlet
{
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
	 response.setCharacterEncoding("UTF-8");
	 //response.setContentType("application/json;charset=utf-8");
    String rootpath = request.getParameter("port");
    String rootFolder = rootpath + "/" + request.getParameter("rootFolder");
    String uploadContentType=request.getParameter("uploadContentType");
    // 判断从哪个页面点过来了的
    String from = request.getParameter("from");
    String taskId = request.getParameter("taskId");
    request.setCharacterEncoding("UTF-8");
    if (rootFolder == null)
    {
      rootFolder = "/";
    }
    else
    {
      if (!rootFolder.startsWith("/"))
      {
        rootFolder = "/" + rootFolder;
      }
      if (!rootFolder.endsWith("/"))
      {
        rootFolder = rootFolder + "/";
      }
    }
    String savePath = Constant.UploadFileRootPath;
    savePath = savePath + rootFolder;

    File folder = new File(savePath);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    String fileName = "";
    StringBuffer json = new StringBuffer();
    String extName = "";
    long fileSize = 0L;
        String uniquedName=UUID.randomUUID().toString()+".zip";
        File strToFile=null;
    	try {
    		
    		//上传审计意见
    		if("opinion".equals(uploadContentType)){
    			 fileName=request.getParameter("OpinionName");
    			 //fileName=URLDecoder.decode(fileName,"UTF-8");
    			 extName=request.getParameter("DocType");
    			 String strContent=request.getParameter("OpinionContent");

    			 //strContent=URLDecoder.decode(URLDecoder.decode(strContent,"UTF-8"),"UTF-8");
    			 //需要创建新的word文件
    			 if(writeDoc(savePath+"/"+fileName+"."+extName, strContent)){
    				 	strToFile = new File(savePath+"/"+fileName+"."+extName);
    				 	fileSize=strToFile.length()/1024L;
    				 	byte[] zipByte = getEncryptZipByte(new FileInputStream(strToFile),fileName+"."+extName, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
    					FileUtils.writeByteFile(zipByte, new File(savePath + "/" + uniquedName));
    			 }
    		}
    		 //上传文件
    		 if("file".equals(uploadContentType)){
    			 SmartUpload mySmartUpload =new SmartUpload();
        		 mySmartUpload.initialize(this.getServletConfig(),request,response);
        		 mySmartUpload.upload();
    			 fileName=mySmartUpload.getRequest().getParameter("DocTitle");
    			 fileName=URLDecoder.decode(URLDecoder.decode(fileName,"UTF-8"),"UTF-8");
    			 //fileName=URLDecoder.decode(fileName,"UTF-8");
    			 extName=mySmartUpload.getRequest().getParameter("DocType");
				 String FilePath;
				 com.jspsmart.upload.File myFile = null;
				 myFile = mySmartUpload.getFiles().getFile(0);
				 fileSize=myFile.getSize()/1024L;
				 FilePath = myFile.getFileName();
				 if (!myFile.isMissing()){
						myFile.saveAs(FilePath,mySmartUpload.SAVE_PHYSICAL);	// 保存上传文件到内存
						File tfile = new File(FilePath);
						byte[] zipByte = getEncryptZipByte(new FileInputStream(tfile),fileName+"."+extName, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
						FileUtils.writeByteFile(zipByte, new File(savePath + "/" + uniquedName));
				 }
    		 }
		} catch (SmartUploadException e1) {
			e1.printStackTrace();
		}
       
        
        String fileInfo="";
        //获取系统时间
   	    Date date = new Date();
   	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if(strToFile!=null){
	    		  strToFile.delete();
	    	  }
			if("file".equals(uploadContentType)){
				fileInfo = "{fileName:'" + encodeBase64(fileName) + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "',result:'succeed',addTime:'"+sdf.format(date.getTime()).toString()+"',width:'0',height:'0'}";
			}else{
	    	 //fileInfo = "{fileName:'" + URLEncoder.encode(fileName,"UTF-8") + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "',result:'succeed',addTime:'"+sdf.format(date.getTime()).toString()+"'}";
	    	 //fileInfo = "{\"fileName\":\"" + fileName + "\",\"fileType\":\"" + extName + "\",\"fileSize\":\"" + fileSize + "\",\"savePath\":\"" + new AesCBC().Encrypt(rootFolder + uniquedName) + "\",\"result\":\"succeed\",\"addTime\":\""+sdf.format(date.getTime()).toString()+"\",\"width\":0,\"height\":0}";
	    	 fileInfo = "{fileName:'" + fileName + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "',result:'succeed',addTime:'"+sdf.format(date.getTime()).toString()+"',width:'0',height:'0'}";
	    	 if("opinion".equals(uploadContentType)){
	    		String jsonpCallback = request.getParameter("callback");//客户端请求参数
	    		fileInfo=jsonpCallback+"("+fileInfo+")";
	    	 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        json.append(fileInfo);
        response.getWriter().print(json.toString());
  }
  
  public String encodeBase64(String str){
	  return new Base64().encodeBase64String(str.getBytes());
  }
  
  public byte[] getEncryptZipByte(InputStream srcStream,String fileName,String password) {
	  ByteArrayOutputStream tempOStream = new ByteArrayOutputStream(1024);
		byte[] tempBytes = null;
		byte[] buf = new byte[1024];
		try {
			EncryptZipOutput out = new EncryptZipOutput(tempOStream,password);
			out.putNextEntry(new EncryptZipEntry(URLEncoder.encode(fileName,"UTF-8")));
			int len;
			while ((len = srcStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.closeEntry();
			srcStream.close();
			tempOStream.flush();
			out.close();
			tempBytes = tempOStream.toByteArray();
			tempOStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempBytes;
  }
  
  /**
   * 解压文件
   * @param in 输入文件流
   * @param out 输出文件流
   * @param fileName 文件名称
   * @throws IOException
   */
  private void zipFile(InputStream in, OutputStream out, String fileName) throws IOException{
		AESEncrypterBC aesBC=new AESEncrypterBC();
		AesZipFileEncrypter.zipAndEncrypt(in, out, URLEncoder.encode(fileName,"UTF-8"), "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56", aesBC);
  }
  
  
  public  boolean writeDoc(String path, String content) { 
	    boolean w = false; 
	    try { 
	    	XWPFDocument doc = new XWPFDocument();  
	        XWPFParagraph p1;
	        XWPFRun r1;
	        String [] paragraphs = content.split("\n");
	        if(paragraphs.length==0){
	        	//只有一个段落
	        	 p1= doc.createParagraph();  
	 	         r1 = p1.createRun();  
	 	         r1.setText(content);  
	        }else{
	        	//	多个段落
	        	 for(int i=0;i<paragraphs.length;i++){
		        	 p1= doc.createParagraph();  
		 	         r1 = p1.createRun();  
		 	         r1.setText(paragraphs[i]);  
		        }
	        }
	        FileOutputStream out = new FileOutputStream(path);  
	        doc.write(out);  
	        out.close();  
		    w=true; 
	    } catch (IOException e) { 
	    	e.printStackTrace(); 
	    } 
	    	return w; 
	 }
}