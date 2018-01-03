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
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipOutput;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class CorpUploadAndRename extends HttpServlet
{
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {

	String workName = URLDecoder.decode(request.getParameter("workName"),"UTF-8");
	
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
        if (realName.lastIndexOf(".") >= 0) {
          extName = realName.substring(realName.lastIndexOf(".") + 1);
          realName = realName.substring(0, realName.lastIndexOf("."));
        }

        File file = new File(savePath + "/" + fileName);
        if (file.exists()) {
          response.getWriter().print("exist:" + fileName);
          return;
        }

  	  String wenjianleibiedaima = "";
	  if("".equals(extName)){
      	wenjianleibiedaima = "O";
      }else if("TXT".equals(extName.toUpperCase())||"DOC".equals(extName.toUpperCase())||"XLS".equals(extName.toUpperCase())||"XLSX".equals(extName.toUpperCase())){
      	// 文本文件
      	wenjianleibiedaima = "T";
      }else if("JPG".equals(extName.toUpperCase())||"JPEG".equals(extName.toUpperCase())||"GIF".equals(extName.toUpperCase())||"AI".equals(extName.toUpperCase())||"PDG".equals(extName.toUpperCase())||"PNG".equals(extName.toUpperCase())){
      	// 图像文件
      	wenjianleibiedaima = "I";
      }
//      else if("jpg".equals(extName)||"jpeg".equals(extName)||"gif".equals(extName)||"ai".equals(extName)||"pdg".equals(extName)){
//      	// 图形文件
//      	wenjianleibiedaima = "G";
//      }
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
	  
	 String fname = "[]-"+workName+"-"+realName+"-M-"+wenjianleibiedaima+"-"+dDate;
	 
        String uniquedName=UUID.randomUUID().toString()+".zip";
        byte[] zipByte = getEncryptZipByte(item.getInputStream(),fname+"."+extName, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
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
	    	 
	    	 fileInfo = "{fileName:'" + fname + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "','width':'"+w+"','height':'"+h+"'}";
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