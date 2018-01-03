package com.jmw.filesite.web.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jmw.filesite.utils.AesCBC;
import com.jmw.filesite.utils.cache.Constant;

public class Document extends HttpServlet {
	public Document() {
		super();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer id=Integer.valueOf(request.getParameter("fileId"));
		if(id==0) return;
		String realName="";
		try {
			realName = new AesCBC().Decrypt(request.getParameter("realName").replace(" ", "+"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		File obj = new File(Constant.UploadFileRootPath + realName);
	    if (!obj.exists()) {
	        response.setContentType("text/html;charset=utf-8");
	        response.getWriter().print("<script>alert('指定文件不存在！')</script>");
	      return;
	    }
	    
		// 写流文件到前端浏览器
		ServletOutputStream out = response.getOutputStream();
		//response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(obj));
			
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
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

	@Override
	public void init() throws ServletException {
	}

}
