package com.jmw.filesite.web.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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

public class KbUpload extends HttpServlet
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
		  String tableKey ="";
		  String keyId="";
		  String  nozip ="";
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
		           if("nozip".equals(name)){
		        	   nozip= Streams.asString(stream);
		           }
		           if("keyId".equals(name)){
		        	   keyId= Streams.asString(stream);
		           }
		           if("tableKey".equals(name)){
		        	   tableKey= Streams.asString(stream);
		           }
		           
		        } 
		        if (!item.isFormField()) {
		        	
		            realName = item.getName();
		            fileName = realName;
		            fileSize = stream.available() / 1024L;
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
	    //转换后的swf保存路径
	    String swfSavePath = Constant.SwfSavePath;
	    swfSavePath = swfSavePath + rootFolder;


	    File folder = new File(savePath);
	    if (!folder.exists()) {
	      folder.mkdirs();
	    }
	        if (realName.lastIndexOf(".") >= 0) {
	          extName = realName.substring(realName.lastIndexOf(".") + 1);
	          realName = realName.substring(0, realName.lastIndexOf("."));
	        }
	        //如果特别指出不压缩，就不上传成.zip文件,此时需要传递扩展名
	        if(null != nozip && "nozip".equals(nozip) && ("pdf".equals(extName) || "doc".equals(extName) || "docx".equals(extName))){
	        	 File file = new File(savePath + fileName);
	        	 
	             if (file.exists()) {
	               response.getWriter().print("exist:" + fileName);
	               return;
	             }
	             
	        	String struniqued = UUID.randomUUID().toString();
	        	String uniquedName = struniqued + "." + extName;
	        	String strSwfFileName = struniqued + ".swf";
	        	 
	        	try {
		        	byte[] zipByte;
		        	zipByte =bytes;
		        	//FileOutputStream fos=new FileOutputStream(new File(savePath + "/" + uniquedName));
		        	FileOutputStream fos=new FileOutputStream(new File(savePath + uniquedName));
					fos.write(zipByte);
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			    String fileInfo="";
				try {
					fileInfo = "{fileName:'" + realName + "',fileType:'" + extName + "',fileSize:'" + fileSize;
					fileInfo += "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "',tableKey:'" + tableKey;
					fileInfo += "',keyId:'" + keyId; 
					fileInfo += "',saveSwfPath:'" + new AesCBC().Encrypt(rootFolder + struniqued + ".swf")  + "'}";
					
				} catch (Exception e) {
					e.printStackTrace();
				}
		        html.append(fileInfo);
	        } else {
	        	File file = new File(savePath + "/" + fileName);
	             if (file.exists()) {
	               response.getWriter().print("exist:" + fileName);
	               return;
	             }
	             
		        String uniquedName=UUID.randomUUID().toString()+".zip";
		        byte[] zipByte;
		        if(null!=request.getParameter("encrypt")){
		        	try {
						zipByte=Encrypt(bytes, "[C@13a2e266$53#5", "5369298253621472");
						FileOutputStream fos=new FileOutputStream(new File(savePath + "/" + uniquedName));
						fos.write(zipByte);
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }else{
		        	zipByte = getEncryptZipByte(new ByteArrayInputStream(bytes),fileName, "98jk34kxu52#$5422b7cb-d6490a1c57f43bwn56");
		        	FileUtils.writeByteFile(zipByte, new File(savePath + "/" + uniquedName));
		        }
		        
		        String fileInfo="";
				try {
					fileInfo = "{fileName:'" + realName + "',fileType:'" + extName + "',fileSize:'" + fileSize + "',savePath:'" + new AesCBC().Encrypt(rootFolder + uniquedName) + "',tableKey:'" + tableKey + "',keyId:'" + keyId + "'}";
				} catch (Exception e) {
					e.printStackTrace();
				}
		        html.append(fileInfo);
	        }
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
  
	private byte[] createBytes(InputStream stream) throws IOException {
		byte[] bytes = new byte[1024];
		int len = -1;
		ByteArrayOutputStream memoryOut = new ByteArrayOutputStream();
		while ((len = stream.read(bytes, 0, bytes.length)) != -1) {
			memoryOut.write(bytes, 0, len);
		}
		return memoryOut.toByteArray();
	}
	
	/**
	 * ASE加密算法(采用CBC工作模式,PKCS5Padding填充方式)
	 * @param 加密源
	 * @param 加密密钥
	 * @param 初始向量
	 * @return 加密后的字符串
	 * @throws Exception
	 */
    public byte[] Encrypt(byte[] sSrc, String sKey, String sIv) throws Exception {  
        if (sKey == null) {  
            System.out.print("Key为空null");  
            return null;  
        }  
        // 判断Key是否为16位  
        if (sKey.length() != 16) {  
            System.out.print("Key长度不是16位");  
            return null;  
        }  
        byte[] raw = sKey.getBytes();  
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"  
        IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度  
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);  
        byte[] encrypted = cipher.doFinal(sSrc);  
        return encrypted;
        //return new Base64().encodeBase64String(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。  
    }
}