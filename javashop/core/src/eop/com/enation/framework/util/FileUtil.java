package com.enation.framework.util;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件工具类
 * 
 * @author kingapex 2010-1-6下午02:14:41
 */
public class FileUtil {

	private FileUtil() {
	}
 
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		// boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.TRANSLUCENT;
			/*
			 * if (hasAlpha) { transparency = Transparency.BITMASK; }
			 */

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		} catch (Exception e){
			e.printStackTrace();
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
			/*
			 * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
			 */
			bimage = new BufferedImage(image.getWidth(null),image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}
	
	
	public static void createFile(InputStream in, String filePath) {
		if(in==null) throw new RuntimeException("create file error: inputstream is null");
		int potPos = filePath.lastIndexOf('/') + 1;
		String folderPath = filePath.substring(0, potPos);
		createFolder(folderPath);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(filePath);
			byte[] by = new byte[1024];
			int c;
			while ((c = in.read(by)) != -1) {
				outputStream.write(by, 0, c);
			}
		} catch (IOException e) {
			e.getStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 是否是允许上传文件
	 * 
	 * @param ex
	 * @return
	 */
	public static boolean isAllowUp(String logoFileName) {
		logoFileName = logoFileName.toLowerCase();
		String allowTYpe = "gif,jpg,jpeg,bmp,swf,png,rar,doc,docx,xls,xlsx,pdf,zip,ico,txt,mp4"; 
		if (!logoFileName.trim().equals("") && logoFileName.length() > 0) {
			String ex = logoFileName.substring(logoFileName.lastIndexOf(".") + 1, logoFileName.length());
//			return allowTYpe.toString().indexOf(ex) >= 0;
			//lzf edit 20110717
			//解决只认小写问题
			//同时加入jpeg扩展名/png扩展名
			return allowTYpe.toUpperCase().indexOf(ex.toUpperCase()) >= 0;
		} else {
			return false;
		}
	}
	/**
	 * 是否是允许上传的图片
	 * @param imgFileName
	 * @return
	 */
	public static boolean isAllowUpImg(String imgFileName){
		imgFileName = imgFileName.toLowerCase();
		String allowTYpe = "gif,jpg,bmp,png,jpeg,swf"; 
		if (!imgFileName.trim().equals("") && imgFileName.length() > 0) {
			String ex = imgFileName.substring(imgFileName.lastIndexOf(".") + 1, imgFileName.length());
			return allowTYpe.toUpperCase().indexOf(ex.toUpperCase()) >= 0;
		} else {
			return false;
		}
	}
	
	/**
	 * 把内容写入文件
	 * 
	 * @param filePath
	 * @param fileContent
	 */
	public static void write(String filePath, String fileContent) {

		try {
			FileOutputStream fo = new FileOutputStream(filePath);
			OutputStreamWriter out = new OutputStreamWriter(fo, "UTF-8");

			out.write(fileContent);

			out.close();
		} catch (IOException ex) {

			System.err.println("Create File Error!");
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 将inputstream写入文件
	 * @param in 文件流
	 * @param path 要写入的文件路径
	 */
	public static void write(MultipartFile file,String path){
		try {
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 将inputstream写入文件
	 * @param in 文件流
	 * @param path 要写入的文件路径
	 */
	public static void write(InputStream stream,String path){
		try {
			FileUtils.copyInputStreamToFile(stream, new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 读取文件内容 默认是UTF-8编码
	 * 
	 * @param filePath
	 * @return
	 */
	public static String read(String filePath, String code) {
		if (code == null || code.equals("")) {
			code = "UTF-8";
		}
		String fileContent = "";
		File file = new File(filePath);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), code);
			BufferedReader reader = new BufferedReader(read);
			String line;
			while ((line = reader.readLine()) != null) {
				fileContent = fileContent + line + "\n";
			}
			read.close();
			read = null;
			reader.close();
			read = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			fileContent = "";
		}
		return fileContent;
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param filePath
	 */
	public static void delete(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()){
				if(file.isDirectory()){
					FileUtils.deleteDirectory(file);
				}else{
					file.delete();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean exist(String filepath) {
		File file = new File(filepath);

		return file.exists();
	}

	/**
	 * 创建文件夹
	 * 
	 * @param filePath
	 */
	public static void createFolder(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception ex) {
			System.err.println("Make Folder Error:" + ex.getMessage());
		}
	}
	
	/**
	 * 重命名文件、文件夹
	 * @param from
	 * @param to
	 */
	public static void renameFile(String from, String to) {
		try {
			File file = new File(from);
			if(file.exists()){
				file.renameTo(new File(to));
			}
		}catch (Exception ex) {
			System.err.println("Rename File/Folder Error:" + ex.getMessage());
		}
	}

	/**
	 * 得到文件的扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExt(String fileName) {

		int potPos = fileName.lastIndexOf('.') + 1;
		String type = fileName.substring(potPos, fileName.length());
		return type;
	}

	/**
	 * 通过File对象创建文件
	 * 
	 * @param file
	 * @param filePath
	 */
	public static void createFile(File file, String filePath) {
		int potPos = filePath.lastIndexOf('/') + 1;
		String folderPath = filePath.substring(0, potPos);
		createFolder(folderPath);
		FileOutputStream outputStream = null;
		FileInputStream fileInputStream = null;
		try {
			outputStream = new FileOutputStream(filePath);
			fileInputStream = new FileInputStream(file);
			byte[] by = new byte[1024];
			int c;
			while ((c = fileInputStream.read(by)) != -1) {
				outputStream.write(by, 0, c);
			}
		} catch (IOException e) {
			e.getStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	
	
	public static String readFile(String resource) {
		InputStream stream = getResourceAsStream(resource);
		String content = readStreamToString(stream);
		
		return content;

	}

	public static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1)
				: resource;

		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);

		}
	
		return stream;
	}

	public static String readStreamToString(InputStream stream) {
		StringBuffer fileContentsb =new StringBuffer();
		String fileContent="";

		try {
			InputStreamReader read = new InputStreamReader(stream, "utf-8");
			BufferedReader reader = new BufferedReader(read);
			String line;
			while ((line = reader.readLine()) != null) {
				fileContentsb.append(line+"\n");
			}
			read.close();
			read = null;
			reader.close();
			read = null;
			fileContent=fileContentsb.toString();
		} catch (Exception ex) {
			fileContent = "";
		}
		return fileContent;
	}

	/**
	 * delete file folder
	 * 
	 * @param path
	 */
	public static void removeFile(File path) {
		
		if (path.isDirectory()) {
			try {
				FileUtils.deleteDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		 
	}

	public static void copyFile(String srcFile,String destFile){
		try {
			if(FileUtil.exist(srcFile)){
				FileUtils.copyFile(new File(srcFile),new File( destFile ));
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void copyFolder(String sourceFolder, String destinationFolder)
		 {
		////System.out.println("copy " + sourceFolder + " to " + destinationFolder);
		try{
			File sourceF = new File(sourceFolder);
			if (sourceF.exists())
				FileUtils.copyDirectory(new File(sourceFolder), new File(
						destinationFolder));
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("copy file error");
		}
		
		
	}
	
	
	/**
	 * 拷贝源文件夹至目标文件夹
	 * 只拷贝新文件 
	 * @param sourceFolder
	 * @param targetFolder
	 */
	public static void copyNewFile(String sourceFolder, String targetFolder){
		try{
			File sourceF = new File(sourceFolder);
			
			if(!targetFolder.endsWith("/")) targetFolder=targetFolder+"/";
		 
			if (sourceF.exists()){
				File[] filelist = sourceF.listFiles();
				for(File f:filelist){
					File targetFile = new File(targetFolder+f.getName());
					
					if(  f.isFile()){
						 //如果目标文件比较新，或源文件比较新，则拷贝，否则跳过
						if(!targetFile.exists() || FileUtils.isFileNewer(f, targetFile)){
							FileUtils.copyFileToDirectory(f, new File(targetFolder));
							////System.out.println("copy "+ f.getName());
						}else{
						//	//System.out.println("skip  "+ f.getName());
						}
					}
				}
			}
		 
		 
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("copy file error");
		}
	}
	
	
	public static void unZip(String zipPath,String targetFolder,boolean cleanZip){
		File folderFile =new File(targetFolder);
		File zipFile = new File(zipPath);
		Project prj = new Project();
		Expand expand = new Expand();
		expand.setEncoding("UTF-8");
		expand.setProject(prj);
		expand.setSrc(zipFile);
		expand.setOverwrite(true);
		expand.setDest(folderFile);
		expand.execute();
		
		if(cleanZip){
			//清除zip包
			Delete delete = new Delete();
			delete.setProject(prj);
			delete.setDir(zipFile);
			delete.execute();
		}
	}

	
	public static void main(String arg[]) {
 
	}

}
