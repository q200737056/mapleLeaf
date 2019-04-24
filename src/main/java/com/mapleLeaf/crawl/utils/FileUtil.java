package com.mapleLeaf.crawl.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.mapleLeaf.common.util.StrUtil;
import com.mapleLeaf.crawl.other.TrustAnyHostnameVerifier;
import com.mapleLeaf.crawl.other.TrustAnyTrustManager;



/**
 * 文件操作
 *
 */
public class FileUtil {

	
	static final String ENCODE="UTF-8";	//默认文件编码UTF-8
	
	/**
	 * 读文件，返回文件文本信息，默认编码UTF-8
	 * @param path 文件路径 C:\xnx3.txt
	 * @return String 读取的文件文本信息
	 */
	public static String read(String path){
		return read(path,ENCODE);
	}
	
	/**
	 * 读文件，返回文件文本信息
	 * @param path 文件路径 C:\xnx3.txt
	 * @param encode 文件编码.如 FileUtil.GBK
	 * @return String 返回的文件文本信息
	 */
	public static String read(String path,String encode){
		StringBuffer xnx3_content=new StringBuffer();
		try{
			File file=new File(path);
			BufferedReader xnx3_reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),encode));
			String date=null;
			while((date=xnx3_reader.readLine())!=null){
				xnx3_content.append(date+"\n");
			}
			xnx3_reader.close();
		}catch (Exception e) {
		}
		
		return xnx3_content.toString();
	}
	
	/**
	 * 读文件，返回文件内容
	 * @param file
	 * @param encode 编码，如FileUtil.GBK
	 * @return String 读取的文件文本信息
	 */
	public static String read(File file,String encode){
		StringBuffer xnx3_content=new StringBuffer();
		try{
			BufferedReader xnx3_reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),encode));
			String date=null;
			while((date=xnx3_reader.readLine())!=null){
				xnx3_content.append(date);
			}
			xnx3_reader.close();
		}catch (Exception e) {
		}
		
		return xnx3_content.toString();
	}
	
	
	/**
	 * 写文件
	 * @param path 传入要保存至的路径————如D:\\a.txt
	 * @param xnx3_content 传入要保存的内容
	 * @return 成功|失败
	 */
	public static boolean write(String path,String xnx3_content){
		try {
			FileWriter fw=new FileWriter(path);
			java.io.PrintWriter pw=new java.io.PrintWriter(fw);
			pw.print(xnx3_content);
			pw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	/**
	 * 写文件
	 * @param path 传入要保存至的路径————如D:\\a.txt
	 * @param xnx3_content 传入要保存的内容
	 * @param encode 写出文件的编码
	 * 				<li>{@link FileUtil#UTF8}
	 * 				<li>{@link FileUtil#GBK}
	 * @return 成功|失败
	 * @throws IOException 
	 */
	public static void write(String path,String xnx3_content,String encode) throws IOException{
        FileOutputStream fos = new FileOutputStream(path); 
        OutputStreamWriter osw = new OutputStreamWriter(fos, encode); 
        osw.write(xnx3_content); 
        osw.flush(); 
	}
	
	/**
	 * 写文件
	 * @param file 传入要保存至的路径————如D:\\a.txt
	 * @param xnx3_content 传入要保存的内容
	 * @return boolean
	 */
	public static boolean write(File file,String xnx3_content){
		try {
			java.io.PrintWriter pw=new java.io.PrintWriter(file);
			pw.print(xnx3_content);
			pw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * InputStream转为文件并保存，为jar包内的资源导出而写
	 * <pre>
	 * 	FileUtil.inputStreamToFile(getClass().getResourceAsStream("dm.dll"), "C:\\dm.dll");
	 * </pre>
	 * @param inputStream 输入流
	 * @param targetFilePath 要保存的文件路径
	 */
	public static void inputStreamToFile(InputStream inputStream, String targetFilePath) {
		File file = new File(targetFilePath);
		OutputStream os = null;

		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				os.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *  复制文件
	 *  <pre>copyFile("E:\\a.txt", "E:\\aa.txt");</pre>
	 * @param sourceFile 源文件，要复制的文件所在路径
	 * @param targetFile 复制到那个地方
	 */
    public static void copyFile(String sourceFile, String targetFile){
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        }catch (Exception e) {
			e.printStackTrace();
		}finally {
			 // 关闭流
            if (inBuff != null)
				try {
					inBuff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            if (outBuff != null)
				try {
					outBuff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
        }
    }

	
	/**
	 * 删除单个文件，java操作
	 * @param fileName 文件名，包含路径。如E:\\a\\b.txt
	 * @return boolean true：删除成功
	 */
	public static boolean deleteFile(String fileName){
		boolean xnx3_result=false;
		
		java.io.File f=new java.io.File(fileName);
		if(f.isFile()&&f.exists()){
			f.delete();
			xnx3_result=true;
		}
		
		return xnx3_result;
	}
	
	
	/**
	 * 传入绝对路径，判断该文件是否存在
	 * @param filePath 文件的绝对路径，如 "C:\\WINDOWS\\system32\\msvcr100.dll"
	 * @return Boolean true:存在
	 */
    public static boolean exists(String filePath){
    	java.io.File f = new java.io.File(filePath);
    	return f.exists();
    }


	/**
	 * 通过网址获得文件长度
	 * @param url 文件的链接地址
	 * @return 文件长度(Hander里的Content-Length)
	 * 			<li>失败返回-1
	 */
	public static long getFileSize(String url) {
		int nFileLength = -1;
		try {
			URL xnx3_url = new URL(url);
			HttpURLConnection httpConnection = (HttpURLConnection) xnx3_url
					.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "Internet Explorer");

			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 400) {
				System.err.println("Error Code : " + responseCode);
				return -2; // -2 represent access is error
			}
			String sHeader;
			for (int i = 1;; i++) {
				sHeader = httpConnection.getHeaderFieldKey(i);
				if (sHeader != null) {
					if (sHeader.equals("Content-Length")) {
						nFileLength = Integer.parseInt(httpConnection
								.getHeaderField(sHeader));
						break;
					}
				} else
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nFileLength;
	}

	/**
	 * 从互联网下载文件。适用于http、https协议
	 * <li>下载过程会阻塞当前线程
	 * <li>若文件存在，会先删除存在的文件，再下载
	 * @param downUrl 下载的目标文件网址 如 "http://www.xnx3.com/down/java/j2se_util.zip"
	 * @param savePath 下载的文件保存路径。如 "C:\\test\\j2se_util.zip"
	 * @return 返回下载出现的异常
	 * 			<li>若返回null，则为下载成功，下载完毕，没有出现异常
	 * 			<li>若返回具体字符串，则出现了异常，被try捕获到了，返回e.getMessage()异常信息
	 * @throws IOException 
	 */
	public static void downFiles(String downUrl,String savePath) throws IOException{
		downFiles(downUrl, savePath, null);
	}
	
	/**
	 * 从互联网下载文件。适用于http、https协议
	 * <li>下载过程会阻塞当前线程
	 * <li>若文件存在，会先删除存在的文件，再下载
	 * @param downUrl 下载的目标文件网址 如 "http://www.xnx3.com/down/java/j2se_util.zip"
	 * @param savePath 下载的文件保存路径。如 "C:\\test\\j2se_util.zip"
	 * @param param 包含在请求头中的一些参数。比如 User-Agent 等。若为空，则不传递任何参数。<br/>例如：
	 * 			<ul>
	 * 				<li>key:User-Agent &nbsp;&nbsp;&nbsp;&nbsp; value: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36</li>
	 * 				<li>key:Host &nbsp;&nbsp;&nbsp;&nbsp; value:xnx3.com</li>
	 * 			</ul>
	 * @return 返回下载出现的异常
	 * 			<li>若返回null，则为下载成功，下载完毕，没有出现异常
	 * 			<li>若返回具体字符串，则出现了异常，被try捕获到了，返回e.getMessage()异常信息
	 * @throws IOException 
	 */
	public static void downFiles(String downUrl,String savePath, Map<String, String> param) throws IOException{
		//判断文件是否已存在，若存在，则先删除
		if(exists(savePath)){
			FileUtil.deleteFile(savePath);
		}
		
		int nStartPos = 0;
		int nRead = 0;
		
		URL url = new URL(downUrl);
		if(downUrl.indexOf("http://") > -1){
			// 打开连接
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			// 获得文件长度
			long nEndPos = getFileSize(downUrl);
			
			RandomAccessFile oSavedFile = new RandomAccessFile(savePath, "rw");
			if(param != null){
				for (Map.Entry<String, String> entry : param.entrySet()) {
					httpConnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}else{
				httpConnection.setRequestProperty("User-Agent", "Internet Explorer");
			}
			String sProperty = "bytes=" + nStartPos + "-";
			// 告诉服务器book.rar这个文件从nStartPos字节开始传
			httpConnection.setRequestProperty("RANGE", sProperty);
			InputStream input = httpConnection.getInputStream();
			if(nEndPos == -1){
				//没有取得长度字节数，那么就直接将其保存就好了
				oSavedFile.write(inputstreamToByte(input));
			}else{
				byte[] b = new byte[1024];
				// 读取网络文件,写入指定的文件中
				while ((nRead = input.read(b, 0, 1024)) > 0 && nStartPos < nEndPos) {
					oSavedFile.write(b, 0, nRead);
					nStartPos += nRead;
				}
			}
			
			httpConnection.disconnect();
			oSavedFile.close();
		}else if(downUrl.indexOf("https://") > -1){
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("SSL");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
	        try {
				sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
	        conn.setSSLSocketFactory(sc.getSocketFactory());
	        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
	        
	        //文件的长度
	        int nEndPos = StrUtil.stringToInt(conn.getHeaderField("Content-Length"), -1);

	        RandomAccessFile oSavedFile = new RandomAccessFile(savePath, "rw");
	        if(param != null){
				for (Map.Entry<String, String> entry : param.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}else{
				//conn.setRequestProperty("User-Agent", "Internet Explorer");
			}
//	        conn.setRequestProperty("User-Agent", "Internet Explorer");
			String sProperty = "bytes=" + nStartPos + "-";
			// 告诉服务器book.rar这个文件从nStartPos字节开始传
//			conn.setRequestProperty("RANGE", sProperty);
			InputStream input = conn.getInputStream();
			
			if(nEndPos == -1){
				//没有取得长度字节数，那么就直接将其保存就好了
				oSavedFile.write(inputstreamToByte(input));
			}else{
				byte[] b = new byte[1024];
				// 读取网络文件,写入指定的文件中
				while ((nRead = input.read(b, 0, 1024)) > 0 && nStartPos < nEndPos) {
					oSavedFile.write(b, 0, nRead);
					nStartPos += nRead;
				}
			}
			
			conn.disconnect();
			oSavedFile.close();
		}
		
	}
	
	/**
	 * 将 {@link BufferedReader} 转换为 {@link String}
	 * @param br {@link BufferedReader}
	 * @return String 若失败，返回 ""
	 */
	public static String BufferedReaderToString(BufferedReader br) {
		String inputLine;
		String str = "";
		try {
			while ((inputLine = br.readLine()) != null) {
				str += inputLine;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 将 {@link InputStream} 转化为 byte[]
	 * @throws IOException
	 */
	public static byte[] inputstreamToByte(InputStream input) throws IOException {
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[4096];
	    int n = 0;
	    while (-1 != (n = input.read(buffer))) {
	        output.write(buffer, 0, n);
	    }
	    return output.toByteArray();
	}
	
	/**
	 * 输入文件路径，返回这个文件的创建时间
	 * @param filePath 要获取创建时间的文件的路径，绝对路径
	 * @return 此文件创建的时间
	 */
	public static Date getCreateTime(String filePath){  
		Path path=Paths.get(filePath);    
		BasicFileAttributeView basicview=Files.getFileAttributeView(path, BasicFileAttributeView.class,LinkOption.NOFOLLOW_LINKS );  
		BasicFileAttributes attr;  
		try {
			attr = basicview.readAttributes();  
			Date createDate = new Date(attr.creationTime().toMillis());  
			return createDate;  
		} catch (Exception e) {  
			e.printStackTrace();  
		}
		Calendar cal = Calendar.getInstance();  
		cal.set(1970, 0, 1, 0, 0, 0);  
		return cal.getTime();  
	}
	public static void createCacheFile(String path){
		
		createCacheFileByPath(path);
		createCacheFileByPath(path+"images/");
		createCacheFileByPath(path+"js/");
		createCacheFileByPath(path+"css/");
		createCacheFileByPath(path+"fonts/");
	}
	public static void createCacheFileByPath(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
}
