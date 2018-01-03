package com.jmw.filesite.web.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;
/**
 * 下载服务器固定路径的文件
 * @author gushipan
 * 
 */
public class DownFixedFile extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String filePath = "";
		try {
			filePath = decodeUrl(request.getParameter("p"));
		} catch (Exception e0) {
			e0.printStackTrace();
		}
	    File obj = new File(Constant.SharePath + filePath);
	    if (!obj.exists()) {
	        response.setContentType("text/html;charset=utf-8");
	        response.getWriter().print("<script>alert('指定文件不存在！')</script>");
	      return;
	    }

	    String fileName="";

	    ByteArrayOutputStream os = null;
	    try {
	    	fileName = decodeUrl(obj.getName());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	    
	    ServletOutputStream out = response.getOutputStream();
	    response.setHeader("Content-disposition", "attachment;filename=\"" + encodeName(fileName,request) + "\"");
	    BufferedInputStream bis = null;
	    BufferedOutputStream bos = null;
	    try {
	      bis = new BufferedInputStream(new FileInputStream(obj));
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
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
	  
}
