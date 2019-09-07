package com.mapleLeaf.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileTool {
	/**
	 * 读取本地文件内容
	 * @param filepath
	 * @return
	 * @throws IOException 
	 */
	public static String readLocalFileContent(String filepath) throws IOException{
		File file = new File(filepath);
		String content="";
		try(FileInputStream fis = new FileInputStream(file);
				BufferedReader br = new  BufferedReader(new InputStreamReader(fis,"UTF-8"))) {
			
			
			while(br.ready()){
				content+=br.readLine()+"\n";
				
			}
			/*
			 byte[] tmp = new byte[1024];
			 int len=0;
			 while((len=fis.read(tmp))!=-1){
				
				content+=new String(tmp,0,len);
			}*/
		} 
		if(content.length()>0){
			return content.substring(0, content.length()-1);
		}
		return content;
	}
	
	/**
	 * 写入本地文件内容
	 * @param filepath
	 * @param content
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void writeLocalFileContent(String filepath,String content) 
			throws IOException{
		File file = new File(filepath);
		try(FileOutputStream fos = new FileOutputStream(file);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos,"UTF-8"))) {
			//fos.write(content.getBytes());
			bw.write(content);
			bw.flush();
		} 
	}
	/**
	 * 列出指定目录下的 子文件夹,子文件
	 * @param parent
	 * @param flag  1:文件夹 2:文件  其它:全部 
	 * @return
	 */
	public static List<String> listLocalFiles(String parent,String flag){
		File root = new File(parent);
		File[] files = root.listFiles();
		List<String> fileList = new ArrayList<>();
		for(File f:files){
			String fileName = f.getPath().substring(f.getPath().lastIndexOf(File.separator)+1);
			
			if("1".equals(flag)){
				if(f.isDirectory()){
					fileList.add(fileName);
				}else{
					continue;
				}
				
			}else if("2".equals(flag)&&f.isDirectory()){
				if(f.isFile()){
					fileList.add(fileName);
				}else{
					continue;
				}
			}else{
				fileList.add(fileName);
			}
		}
		return fileList;
	}
	
	/**
	 * 创建文件
	 * @param path
	 * @param file
	 * @return
	 */
	public static boolean createLocalFile(String path,String file){
		boolean boo=false;
		 File f = new File(path);
		 if(!f.exists()){
			 f.mkdirs();
		 }
		 f = new File(path+File.separator+file);
		 if(!f.exists()){
			 try {
				 boo=f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
		 return boo;
	}
	/**
	 * 得到 真实路径，
	 * 如果是jar包，获取jar所在文件中下的 文件路径;
	 * 如果开发工具 ，则获取项目根目录下的 文件路径
	 * @param path
	 * @return
	 */
	public static String getRealPath(String path){
		if(path==null||"".equals(path)){
			return path;
		}
		if(path.startsWith("/")){
			path = path.substring(1);
		}
		String spath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
		if(spath.indexOf(".jar!")!=-1){//说明 在jar包中
			String rootPath = System.getProperty("user.dir");
			return rootPath+File.separator+path;
		}else{
			spath = Thread.currentThread().getContextClassLoader().getResource(path).getFile();
			return spath;
		}
	}
}
