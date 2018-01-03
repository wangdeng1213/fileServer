package com.jmw.filesite.utils;
import java.io.File;

import com.jmw.filesite.utils.cache.Constant;

public class Pdf2SwfUtil {

	//private static String PDF2SWF_PATH = "C:/Program Files/SWFTools/pdf2swf.exe";
	private static String PDF2SWF_PATH = Constant.Pdf2SwfPath;
	
	public static boolean convert2SWF(String inputFile, String swfFile) {
		File pdfFile = new File(inputFile);
		File outFile = new File(swfFile);
		if(!inputFile.endsWith(".pdf")){
			System.out.println("文件格式非PDF！");
			return false;
		}
		if(!pdfFile.exists()){
			System.out.println("PDF文件不存在！");
			return false;
		}
		if(outFile.exists()){
			System.out.println("SWF文件已存在！");
			return false;
		}
		
		//如果outFile目录不存在
		if(!outFile.getParentFile().exists()) {  
            if(!outFile.getParentFile().mkdirs()) {  
                System.out.println("创建目标swf文件所在目录失败！"); 
                return false;
            } 
		}  
		
		String command = PDF2SWF_PATH +" " +inputFile+" -o "+swfFile+" -T 9 -f -s storeallcharacters ";
		
		try {
			//System.out.println("开始转换文档: "+inputFile);
			//System.out.println("command = "+command);
			Runtime.getRuntime().exec(command);
			//System.out.println("成功转换为SWF文件！");
			return true;
		//} catch (IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("转换文档为swf文件失败！");
			System.out.println("转换文档为swf文件失败！ e.getMessage() = " + e.getMessage() + "e.getCause()= " + e.getCause());
			return false;
		}
	}
	
}
