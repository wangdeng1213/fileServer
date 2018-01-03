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
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipOutput;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

public class UploadFive extends HttpServlet
{
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
	  try{
	  // 创建文件解析对象
	  DiskFileItemFactory fac = new DiskFileItemFactory();
	  ServletFileUpload upload = new ServletFileUpload(fac);
	  FileItemIterator fileList =null;
	  fileList = upload.getItemIterator(request);
	  InputStream stream = null;
	    String  rootpath="";
	    String rootFolder="";
	    String fileName = "";
	    StringBuffer html = new StringBuffer();
	    String realName = "";
	    String extName = "";
	    byte[] bytes =null;
	    long fileSize = 0L;
	  while (fileList.hasNext()) {

	        FileItemStream item = fileList.next();
	        String name = item.getFieldName();
	        stream = item.openStream();

	        if (item.isFormField()) {
	           if("port".equals(name)){
	        	   rootpath= Streams.asString(stream);
	           }
	           if("rootFolder".equals(name)){
	        	   rootFolder= Streams.asString(stream);
	           }
	        } 
	        if (!item.isFormField()) {
	        	
	            realName = item.getName();
	            fileName = realName;
	            fileSize = stream.available() / 1024L;
	           // inStream =stream;
                bytes = IOUtils.toByteArray(stream);

	         }
	  }
    rootFolder = rootpath + "/" + rootFolder;
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
        if (realName.lastIndexOf(".") >= 0) {
          extName = realName.substring(realName.lastIndexOf(".") + 1);
          realName = realName.substring(0, realName.lastIndexOf("."));
        }

        File file = new File(savePath + "/" + fileName);
        if (file.exists()) {
          response.getWriter().print("exist:" + fileName);
          return;
        }

        String uniquedName=UUID.randomUUID().toString()+".zip";
       // byte[] bytes = IOUtils.toByteArray(inStream);
       // streamList.add(new ByteArrayInputStream(bytes));

        byte[] zipByte = getEncryptZipByte(new ByteArrayInputStream(bytes),fileName, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
        FileUtils.writeByteFile(zipByte, new File(savePath + "/" + uniquedName));
        
        String fileInfo="";
			  InputStream is = new ByteArrayInputStream(bytes);
	    	  BufferedImage buff = ImageIO.read(is);
	    	 int w= 0;
	    	 int h= 0;
	    	  try
	    	  {
	    		  w= buff.getWidth();
	    		  System.out.println("w===="+w);
	    		  System.out.println("w===="+h);
	    		  h= buff.getHeight();
	    	  }
	    	  catch(Exception ex1)
	    	  {
	    		  
	    	  }
	    	  is.close(); //关闭Stream
	    	 fileInfo = "{fileName:'" + realName + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "','width':'"+w+"','height':'"+h+"'}";
			//fileInfo = "{fileName:'" + realName + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "',fileRequire:'-1','width':'"+w+"','height':'"+h+"'}";
        html.append(fileInfo);

    response.getWriter().print(html.toString());
    } catch (Exception ex) {
        ex.printStackTrace();
        return;
      }
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
}