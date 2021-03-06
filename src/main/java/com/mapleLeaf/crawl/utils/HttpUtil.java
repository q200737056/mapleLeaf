package com.mapleLeaf.crawl.utils;

import java.io.BufferedReader; 
import java.io.ByteArrayInputStream;
import java.io.IOException; 
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.net.HttpURLConnection; 
import java.net.MalformedURLException;
import java.net.URL; 
import java.nio.charset.Charset; 
import java.util.List;
import java.util.Map; 
import java.util.zip.GZIPInputStream;
import java.util.Vector; 

/**
 * HTTP访问类
 * @author 
 */ 
public class HttpUtil { 
	
	//默认编码格式
    private String encode;
   //每次请求都用自动发送此cookies,请求完毕后自动更新此cookies
    private String cookies="";	
    
   
    /**
     * 设置好编码类型，若不设置，默认是Java虚拟机当前的文件编码
     * <ul>
     * 		<li>使用时首先会自动获取请求地址的编码，获取编码失败时才会使用此处的编码</li>
     * </ul>
     * @see HttpUtil#HttpUtil(String)
     */
    public HttpUtil() { 
        this.encode = Charset.defaultCharset().name(); 
    }
    
    /**
     * 设置好编码类型，若不设置则默认是Java虚拟机当前的文件编码
     * @param encode 使用时首先会自动获取请求地址的编码，获取编码失败时才会使用此处的编码<br/> {@link HttpUtil#UTF8} {@link HttpUtil#GBK}
     */
    public HttpUtil(String encode) { 
        this.encode = encode; 
    } 
   
    /**
     * 设置默认的响应字符集，若不设置默认是UTF-8编码
     * @param encode 字符编码 ，默认使用UTF-8，传入参数如{@link HttpUtil#GBK}
     */ 
    public void setEncode(String encode) { 
        this.encode = encode; 
    } 
    
    /**
     * 获取上次请求完成后获得的Cookies
     * @return cookies
     */
    public String getCookies() {
		return cookies;
	}
    
    /**
     * 设置请求时会附带传递的cookies
     * @param cookies {@link #getCookies()}获取到的值
     */
	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	/**
     * GET请求
     * @param url URL地址
     * @return 响应对象 若是返回null则相应失败抛出异常
     */ 
    public HttpResponse get(String url){ 
        try {
        	return this.send(url, "GET", null, null);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    } 
    
    /**
     * GET请求
     * @param urlString  URL地址
     * @param params 参数集合
     * @return 响应对象
     * @throws IOException
     */ 
    public HttpResponse get(String urlString, Map<String, String> params){ 
        try {
			return this.send(urlString, "GET", params, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null; 
		} 
    } 
   
    /**
     * GET请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param propertys 请求属性
     * @return 响应对象
     * @throws IOException
     */ 
    public HttpResponse get(String urlString, Map<String, String> params, Map<String, String> propertys){ 
        try {
			return this.send(urlString, "GET", params, propertys);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
    } 
   
    /**
     * POST请求
     * @param urlString URL地址
     * @return 响应对象
     * @throws IOException
     */ 
    public HttpResponse post(String urlString) { 
        try {
			return this.send(urlString, "POST", null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
    } 
   
    /**
     * POST请求
     * @param urlString URL地址
     * @param params 参数集合
     * @return 响应对象
     * @throws IOException
     */ 
    public HttpResponse post(String urlString, Map<String, String> params) { 
        try {
			return this.send(urlString, "POST", params, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
    } 
   
    /**
     * POST请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param propertys 请求属性,headers
     * @return 响应对象
     * @throws IOException
     */ 
    public HttpResponse post(String urlString, Map<String, String> params, Map<String, String> propertys){ 
        try {
			return this.send(urlString, "POST", params, propertys);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
    } 
   

    /**
     * HTTP请求
     * @param urlString 地址
     * @param method  GET/POST
     * @param parameters  添加由键值对指定的请求参数
     * @param propertys  添加由键值对指定的一般请求属性,headers
     * @return 响应对象
     * @throws IOException
     */ 
    private HttpResponse send(String urlString, String method, Map<String, String> parameters, 
            Map<String, String> propertys) throws IOException { 
        HttpURLConnection urlConnection = null; 
   
        if (method.equalsIgnoreCase("GET") && parameters != null) { 
            urlString = UrlUtil.mapToUrl(urlString, parameters);
        } 
   
        URL url = new URL(urlString); 
        urlConnection = (HttpURLConnection) url.openConnection(); 
        urlConnection.setRequestMethod(method); 
        urlConnection.setDoOutput(true); 
        urlConnection.setDoInput(true); 
        urlConnection.setUseCaches(false); 
        urlConnection.setRequestProperty("Cookie", this.cookies);
   
        if (propertys != null) 
            for (String key : propertys.keySet()) { 
                urlConnection.addRequestProperty(key, propertys.get(key)); 
            } 
   
        if (method.equalsIgnoreCase("POST") && parameters != null) { 
            StringBuffer param = new StringBuffer(); 
            for (String key : parameters.keySet()) { 
                param.append("&"); 
                param.append(key).append("=").append(parameters.get(key)); 
            } 
            urlConnection.getOutputStream().write(param.toString().getBytes()); 
            urlConnection.getOutputStream().flush(); 
            urlConnection.getOutputStream().close(); 
        } 
        return this.makeContent(urlString, urlConnection); 
    } 
    
//    /**
//     * 通过网址获取其 {@link InputStream}  有漏掉的字节
//     * @param url 网址
//     * @return {@link InputStream}
//     */
//    public static InputStream getInputStreamByUrl(String url){  
//    	InputStream is = null;  
//    	HttpURLConnection httpUrl = null;  
//    	URL u = null;  
//    	ByteArrayOutputStream baos = null;
//    	try {
//    		u = new URL(url);  
//    		httpUrl = (HttpURLConnection) u.openConnection();  
//    		httpUrl.connect();  
//    		is = httpUrl.getInputStream();  
//    		baos = new ByteArrayOutputStream();  
//    		byte[] buffer = new byte[1024];  
//    		int len;  
//    		while ((len = is.read(buffer)) > -1 ) {  
//    		    baos.write(buffer, 0, len);  
//    		}  
//    		baos.flush();                
//    	} catch (IOException e) {
//    		e.printStackTrace();
//    	} finally {
//    		try {
//    			is.close();
//    		} catch (IOException e) {
//    			e.printStackTrace();
//    		}
//        	httpUrl.disconnect();
//    	}
//    	
//    	if(baos != null){
//    		return new ByteArrayInputStream(baos.toByteArray());
//    	}
//    	return null;
//    }
    
    /**
     * 得到响应对象
     * @param urlConnection
     * @return 响应对象
     * @throws IOException
     */ 
    private HttpResponse makeContent(String urlString, HttpURLConnection urlConnection) throws IOException { 
        HttpResponse httpResponser = new HttpResponse(); 
        try { 
            InputStream in = urlConnection.getInputStream(); 
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in)); 
            httpResponser.setContentCollection(new Vector<String>()); 
            StringBuffer temp = new StringBuffer(); 
            String line = bufferedReader.readLine(); 
            while (line != null) { 
                httpResponser.getContentCollection().add(line); 
                temp.append(line).append("\r\n"); 
                line = bufferedReader.readLine(); 
            } 
            bufferedReader.close(); 
            String ecod = urlConnection.getContentEncoding(); 
            if (ecod == null) 
                ecod = this.encode; 
            httpResponser.setUrlString(urlString); 
            //urlConnection.getHeaderField("Set-Cookie");获取到的COOKIES不全，会将JSESSIONID漏掉，故而采用此中方式
            if(this.cookies == null || this.cookies.equals("")){
            	if(urlConnection.getHeaderFields().get("Set-Cookie") != null){
            		List<String> listS = urlConnection.getHeaderFields().get("Set-Cookie");
            		String cookie = "";
                	if(listS != null){
                        for (int i = 0; i < listS.size(); i++) {
            				cookie = cookie + (cookie.equals("")? "":", ") + listS.get(i);
            			}
                	}else{
                		cookie = urlConnection.getHeaderField("Set-Cookie");
                	}
                	this.cookies=cookie;
                	httpResponser.setCookie(this.cookies);
            	}
            }
            
            httpResponser.setDefaultPort(urlConnection.getURL().getDefaultPort()); 
            httpResponser.setFile(urlConnection.getURL().getFile()); 
            httpResponser.setHost(urlConnection.getURL().getHost()); 
            httpResponser.setPath(urlConnection.getURL().getPath()); 
            httpResponser.setPort(urlConnection.getURL().getPort()); 
            httpResponser.setProtocol(urlConnection.getURL().getProtocol()); 
            httpResponser.setQuery(urlConnection.getURL().getQuery()); 
            httpResponser.setRef(urlConnection.getURL().getRef());
            httpResponser.setUserInfo(urlConnection.getURL().getUserInfo()); 
            httpResponser.setContent(new String(temp.toString().getBytes(), ecod));
            httpResponser.setContentEncoding(ecod); 
            httpResponser.setCode(urlConnection.getResponseCode()) ; 
            httpResponser.setMessage(urlConnection.getResponseMessage()); 
            httpResponser.setContentType(urlConnection.getContentType()); 
            httpResponser.setMethod(urlConnection.getRequestMethod()); 
            httpResponser.setConnectTimeout(urlConnection.getConnectTimeout()); 
            httpResponser.setReadTimeout(urlConnection.getReadTimeout()); 
            httpResponser.setHeaderFields(urlConnection.getHeaderFields());
            
        } catch (IOException e) { 
        	httpResponser.setCode(404);
        } finally { 
            if (urlConnection != null) 
                urlConnection.disconnect(); 
        } 
        return httpResponser; 
    } 
    
    /**
     * gzip的网页用到
     * @param in 输入流
     * @param charset 编码格式 {@link HttpUtil#UTF8} {@link HttpUtil#GBK}
     * @return 网页源代码
     */
    public String uncompress(ByteArrayInputStream in,String charset) {
        try {
           GZIPInputStream gInputStream = new GZIPInputStream(in);
           byte[] by = new byte[1024];
           StringBuffer strBuffer = new StringBuffer();
           int len = 0;
           while ((len = gInputStream.read(by)) != -1) {
            strBuffer.append( new String(by, 0, len,charset) );
           }
           return strBuffer.toString();
        } catch (IOException e) {
           e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取经过GZIP压缩的网页源代码
     * @param requestUrl 请求URL地址
     * @return 网页源代码
     */
    public String getGZIP(String requestUrl){
    	String result=null;
    	
    	URL url = null;
    	InputStream is;
    	try {
    		for (int i = 0; i < 1; i++) {
    			url = new URL(requestUrl);
    			byte bytes[] = new byte[1024 * 10000];
    			int index = 0;
    			is = url.openStream();
    			int count = is.read(bytes, index,1024 * 100);
    			while (count != -1) {
    				index += count;
    				count = is.read(bytes, index,1);
    			}
    			ByteArrayInputStream biArrayInputStream=new ByteArrayInputStream(bytes);
    			result=uncompress(biArrayInputStream,this.encode);
    		}
    	} catch (MalformedURLException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
}