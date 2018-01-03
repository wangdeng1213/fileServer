package com.jmw.filesite.utils;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.jmw.filesite.utils.cache.Constant;

public class Doc2PdfUtil {
	public static enum STATUS {
		SUCCESS, FAIL, NOINSTALL
	};
	private static OfficeManager officeManager;
	//private static String OFFICE_HOME = "C:\\Program Files\\OpenOffice.org 3";
	private static String OFFICE_HOME = Constant.OfficeHome;
	
	private static int port[] = { 8100,8110,8120 };

	public static STATUS convert2PDF(File inputFile, File pdfFile) {
		Date start = new Date();
		try{
			startService();
			Logger.getLogger(Doc2PdfUtil.class).info("进行文档转换:" + inputFile.getName() );
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
			converter.convert(inputFile, pdfFile);
		}catch (Exception e) {
			return STATUS.NOINSTALL;
		}finally{
			stopService();
		}
		long l = (start.getTime() - new Date().getTime());
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		Logger.getLogger(Doc2PdfUtil.class).info("生成" + pdfFile.getName() + "耗费：" + min + "分" + s + "秒");
		if (pdfFile.exists()) {
			return STATUS.SUCCESS;
		} else {
			return STATUS.FAIL;
		}
		
	}

	public static void startService() {
		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
		try {
			//System.out.println("准备启动服务....");
			configuration.setOfficeHome(OFFICE_HOME);// 设置OpenOffice.org安装目录
			configuration.setPortNumbers(port); // 设置转换端口，默认为8100
			configuration.setMaxTasksPerProcess(3);//设置�?��进程�?
			configuration.setTaskExecutionTimeout(1000 * 60 * 3L);// 设置任务执行超时�?分钟
			configuration.setTaskQueueTimeout(1000 * 60 * 60 * 24L);// 设置任务队列超时�?4小时
			officeManager = configuration.buildOfficeManager();
			officeManager.start(); // 启动服务
			System.out.println("office转换服务启动成功!");
		} catch (Exception ce) {
			System.out.println("office转换服务启动失败!详细信息:" + ce);
		}
	}

	public static void stopService() {
		System.out.println("关闭office转换服务....");
		if (officeManager != null) {
			officeManager.stop();
		}
		System.out.println("关闭office转换成功!");
	}

}
