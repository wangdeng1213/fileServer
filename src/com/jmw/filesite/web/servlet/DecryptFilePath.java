package com.jmw.filesite.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipInput;
import com.jmw.filesite.training.commons.file.FileUtils;
import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;

public class DecryptFilePath extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    try
    {
    	String filePath="";
      String files = new AesCBC().Decrypt(request.getParameter("file").replace(" ", "+"));
      String[] fileNames = files.split(",");
      for (String file : fileNames) {    	
    	filePath=Constant.UploadFileRootPath+file;
      }
      
      File obj = new File(filePath);
      if (!obj.exists()) {
          response.setContentType("text/html;charset=utf-8");
          response.getWriter().print("<script>alert('指定文件不存在！')</script>");
        return;
      }

      //解压文件     
      try {
  	    byte[] unzipByte = FileUtils.readFileByte(filePath);
  	    filePath=unzipFiles(unzipByte, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56",filePath); 
  	  
  	} catch(Exception ex) {
  		ex.printStackTrace();
  	}
      out.write("1"+filePath);
    } catch (Exception ex) {
      out.write("0");
    }
    finally {
      out.flush();
      out.close();
    }
  }
  public String unzipFiles(byte[] zipBytes, String password,String filePath) throws IOException {
		InputStream bais = new ByteArrayInputStream(zipBytes);
		EncryptZipInput zin = new EncryptZipInput(bais,password);
		String tempPath=filePath;
		EncryptZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			filePath=URLDecoder.decode(filePath.substring(0, filePath.lastIndexOf("/"))+File.separator+UUID.randomUUID().toString()+File.separator+ze.getName(),"UTF-8");
		    new File(filePath.substring(0,filePath.lastIndexOf(File.separator))).mkdirs();
			FileOutputStream fos = new FileOutputStream(filePath);
			byte[] buf = new byte[1024];
			int len;
			while ((len = zin.read(buf)) > 0) {
				fos.write(buf,0,len);
			}			
			fos.close();
			return filePath;
		}
		zin.close();
		bais.close();
		new File(tempPath).delete();
		return filePath;

	}
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doGet(request, response);
  }
}
