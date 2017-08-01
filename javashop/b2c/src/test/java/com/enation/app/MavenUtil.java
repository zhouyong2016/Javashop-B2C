package com.enation.app;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;

/**
 * 
 * maven工具类
 * 为解决因maven本地仓库无法更新jar包导致的pom.xml报错、项目无法构建等问题
 * @author jianghongyan
 * @version v1.0
 * @since v6.2
 * 2016年11月8日 下午11:32:39
 */
public class MavenUtil {
	    private static final String MAVEN_REPO_PATH = "/Users/qiqi/.m2/repository"; 
	    private static final String FILE_SUFFIX = "lastUpdated"; 
	    private static final Logger _log = Logger.getLogger(MavenUtil.class); 
	 
	    /** 
	     * @param args 
	     */ 
	    public static void main(String[] args) { 
	        File mavenRep = new File(MAVEN_REPO_PATH); 
	        if (!mavenRep.exists()) { 
	            _log.warn("Maven repos is not exist."); 
	            return; 
	        } 
	        File[] files = mavenRep.listFiles((FilenameFilter) FileFilterUtils .directoryFileFilter()); 
	        delFileRecr(files,null); 
	        _log.info("Clean lastUpdated files finished."); 
	    } 
	    /**
	     * 递归删除lastUpdate文件 
	     * @param dirs 文件目录
	     * @param files 文件名
	     */
	    private static void delFileRecr(File[] dirs, File[] files) { 
	        if (dirs != null && dirs.length > 0) { 
	            for(File dir: dirs){ 
	                File[] childDir = dir.listFiles((FilenameFilter) FileFilterUtils 
	                .directoryFileFilter()); 
	                File[] childFiles = dir.listFiles((FilenameFilter) FileFilterUtils 
	                .suffixFileFilter(FILE_SUFFIX)); 
	                delFileRecr(childDir,childFiles); 
	            } 
	        } 
	        if(files!=null&&files.length>0){ 
	            for(File file: files){ 
	                if(file.delete()){ 
	                    _log.info("File: ["+file.getName()+"] has been deleted."); 
	                } 
	            } 
	        } 
	    } 
}
