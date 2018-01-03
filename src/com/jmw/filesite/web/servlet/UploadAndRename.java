package com.jmw.filesite.web.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

import com.jmw.filesite.nochump.util.zip.EncryptZipEntry;
import com.jmw.filesite.nochump.util.zip.EncryptZipOutput;
import com.jmw.filesite.training.commons.file.FileUtils;
import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;
import com.jmw.filesite.utils.winzipaes.AesZipFileEncrypter;
import com.jmw.filesite.utils.winzipaes.impl.AESEncrypterBC;

public class UploadAndRename extends HttpServlet
{
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    FileItemIterator fileList =null;
    try
    {
      DiskFileItemFactory fac = new DiskFileItemFactory();
 	  ServletFileUpload upload = new ServletFileUpload(fac);
 	  fileList = upload.getItemIterator(request);
 	  InputStream stream = null;
 	    String  rootpath="";
 	    String rootFolder="";
    String fileName = "";
    String danweidaima = "";
    String xiangmumingcheng ="" ;
    String gongzuoshixiang = "";
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
           if("danweidaima".equals(name)){
        	   danweidaima= Streams.asString(stream);
           }
           if("xiangmumingcheng".equals(name)){
        	   xiangmumingcheng= URLDecoder.decode(Streams.asString(stream),"UTF-8");
           }
           if("gongzuoshixiang".equals(name)){
        	   gongzuoshixiang= URLDecoder.decode(Streams.asString(stream),"UTF-8");
           }
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
        byte[] zipByte = getEncryptZipByte(new ByteArrayInputStream(bytes),fileName+"."+extName, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
        FileUtils.writeByteFile(zipByte, new File(savePath + "/" + uniquedName));
        
        String fileInfo="";
		try {
			  InputStream is = new ByteArrayInputStream(bytes);
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
    response.getWriter().print(html.toString());
    } catch (FileUploadException ex) {
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