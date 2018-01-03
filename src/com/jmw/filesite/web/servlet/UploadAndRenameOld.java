package com.jmw.filesite.web.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipOutput;
import com.jmw.filesite.training.commons.file.FileUtils;
import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;
import com.jmw.filesite.utils.winzipaes.AesZipFileEncrypter;
import com.jmw.filesite.utils.winzipaes.impl.AESEncrypterBC;

public class UploadAndRenameOld extends HttpServlet
{
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    String rootpath = request.getParameter("port");
    String rootFolder = rootpath + "/" + request.getParameter("rootFolder");
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

    DiskFileItemFactory fac = new DiskFileItemFactory();

    ServletFileUpload upload = new ServletFileUpload(fac);

    File folder = new File(savePath);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    List fileList = null;
    try
    {
      fileList = upload.parseRequest(request);
    } catch (FileUploadException ex) {
      ex.printStackTrace();
      return;
    }

    String fileName = "";

    Iterator it = fileList.iterator();
    StringBuffer html = new StringBuffer();
    String realName = "";
    String extName = "";
    long fileSize = 0L;
    while (it.hasNext()) {
      FileItem item = (FileItem)it.next();
     
      if (!item.isFormField()) {
    	
        realName = item.getName();
        fileName = realName;
        fileSize = item.getSize() / 1024L;

        if ((realName == null) || (realName.trim().equals("")))
        {
          continue;
        }
        // 获得格式化后的名称
        String danweidaima = request.getParameter("danweidaima");
        String xiangmumingcheng = URLDecoder.decode(request.getParameter("xiangmumingcheng"),"UTF-8");
        String gongzuoshixiang = URLDecoder.decode(request.getParameter("gongzuoshixiang"),"UTF-8");
        
        String wenjianleibiedaima = "";
        if (realName.lastIndexOf(".") >= 0) {
          extName = realName.substring(realName.lastIndexOf(".") + 1);
          realName = realName.substring(0, realName.lastIndexOf("."));
        }
        if("".equals(extName)){
        	wenjianleibiedaima = "O";
        }else if("TXT".equals(extName.toUpperCase())||"DOC".equals(extName.toUpperCase())||"XLS".equals(extName.toUpperCase())||"XLSX".equals(extName.toUpperCase())){
        	// 文本文件
        	wenjianleibiedaima = "T";
        }else if("JPG".equals(extName.toUpperCase())||"JPEG".equals(extName.toUpperCase())||"GIF".equals(extName.toUpperCase())||"AI".equals(extName.toUpperCase())||"PDG".equals(extName.toUpperCase())||"PNG".equals(extName.toUpperCase())){
        	// 图像文件
        	wenjianleibiedaima = "I";
        }
//        else if("jpg".equals(extName)||"jpeg".equals(extName)||"gif".equals(extName)||"ai".equals(extName)||"pdg".equals(extName)){
//        	// 图形文件
//        	wenjianleibiedaima = "G";
//        }
        else if("AVI".equals(extName.toUpperCase())||"MPEG".equals(extName.toUpperCase())||"MOV".equals(extName.toUpperCase())||"ASF".equals(extName.toUpperCase())){
        	// 影像文件
        	wenjianleibiedaima = "V";
        }else if("MP3".equals(extName.toUpperCase())||"MID".equals(extName.toUpperCase())||"WAV".equals(extName.toUpperCase())||"RM".equals(extName.toUpperCase())||"APE".equals(extName.toUpperCase())||"FLAC".equals(extName.toUpperCase())){
        	// 声音文件
        	wenjianleibiedaima = "A";
        }else if("CLASS".equals(extName.toUpperCase())||"JAVA".equals(extName.toUpperCase())||"XML".equals(extName.toUpperCase())||"PROPERTIES".equals(extName.toUpperCase())||"JSP".equals(extName.toUpperCase())){
        	// 程序文件
        	wenjianleibiedaima = "P";
        }else if("SQL".equals(extName.toUpperCase())||"MDF".equals(extName.toUpperCase())||"NDF".equals(extName.toUpperCase())||"LDF".equals(extName.toUpperCase())){
        	// 数据文件
        	wenjianleibiedaima = "D";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dDate = sdf.format(new Date());  
        fileName = danweidaima+"-"+xiangmumingcheng+"-[]-"+gongzuoshixiang+"-"+realName+"-M-"+wenjianleibiedaima+"-"+sdf.format(new Date());
        realName = fileName;
        
        

        File file = new File(savePath + "/" + fileName);
        if (file.exists()) {
          response.getWriter().print("exist:" + fileName);
          return;
        }

        String uniquedName=UUID.randomUUID().toString()+".zip";
        byte[] zipByte = getEncryptZipByte(item.getInputStream(),fileName+"."+extName, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
        FileUtils.writeByteFile(zipByte, new File(savePath + "/" + uniquedName));
        
        String fileInfo="";
		try {
			  InputStream is = item.getInputStream();
	    	  BufferedImage buff = ImageIO.read(is);
	    	 int w= 0;
	    	 int h= 0;
	    	  try
	    	  {
	    		  w= buff.getWidth();
	    		  h= buff.getHeight();
	    	  }
	    	  catch(Exception ex1)
	    	  {
	    		  
	    	  }
	    	  is.close(); //关闭Stream
	    	 fileInfo = "{fileName:'" + realName + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "','width':'"+w+"','height':'"+h+"'}";
			//fileInfo = "{fileName:'" + realName + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "',fileRequire:'-1','width':'"+w+"','height':'"+h+"'}";
		} catch (Exception e) {
			e.printStackTrace();
		}
        html.append(fileInfo);
      }

    }
    response.getWriter().print(html.toString());
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