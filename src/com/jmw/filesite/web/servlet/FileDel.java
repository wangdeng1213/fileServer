package com.jmw.filesite.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;

public class FileDel extends HttpServlet
{
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    try
    {
      String files = new AesCBC().Decrypt(request.getParameter("file").replace(" ", "+"));

      String[] fileNames = files.split(",");
      for (String file : fileNames) {
    	String realName=file.substring(file.lastIndexOf("/")+1);
        File delfile = new File(Constant.UploadFileRootPath+file);
        if (!realName.startsWith("from_networkdisk_")&&delfile.exists()) {
          delfile.delete();
        }
      }
      out.write("ok");
    } catch (Exception ex) {
      out.write("erro");
    }
    finally {
      out.flush();
      out.close();
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doGet(request, response);
  }
  
  public String decodeUrl(String paramter)
  {
    try {
      return URLDecoder.decode(paramter, "UTF-8"); } catch (UnsupportedEncodingException e) {
    }
    throw new RuntimeException();
  }
}