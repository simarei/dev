package javadev;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

public class FileDownload {

	/**
	 * ファイルダウンロード用のメソッド（HTTP）
	 * ダウンロードが成功すればtrue, 失敗したらfalseを返す
	 * 
	 * @param urlStr ダウンロードしたいファイル
	 * @param target 保存先ファイル名
	 * @throws IOException
	 */
	public static boolean HTTPdownload(String urlStr, String target) throws IOException {
		
		// get input stream
		HttpURLConnection con = null;
		InputStream in = null;
		
		URL url = new URL(urlStr);
		
		con = (HttpURLConnection) url.openConnection();
		
		// BASIC認証が必要な場合は追加 ここから
		// String userpass = "userid:password";	// 適切なuserid, passwordをセット
		// String authEncoded = new sun.misc.BASE64Encoder().encode(userpass.getBytes());
		// con.setRequestProperty("Authorization", "Basic " + authEncoded);
		// BASIC認証が必要な場合は追加 ここまで
		
		con.setRequestProperty("Content-Type", "application/octet-stream");
		con.setRequestMethod("GET");
		
		con.connect();
		
		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
			in = con.getInputStream();
		}
		
		if (in == null) {
			return false;
		}
		
		FileOutputStream fos = new FileOutputStream(new File(target));
		int line = -1;
		
		while ((line = in.read()) != -1) {
			fos.write(line);
		}
		
		in.close();
		fos.close();
		
		return true;
	}
	
	/**
	 * ファイルダウンロード用のメソッド（HTTPS）
	 * ダウンロードが成功すればtrue, 失敗したらfalseを返す
	 * 
	 * @param urlStr ダウンロードしたいファイル
	 * @param target 保存先ファイル名
	 * @throws IOException
	 */
	public static boolean HTTPSdownload(String urlStr, String target) throws IOException {
		
		// get input stream
		HttpsURLConnection con = null;
		InputStream in = null;
		
		URL url = new URL(urlStr);
		
		con = (HttpsURLConnection) url.openConnection();
		con.setRequestProperty("Content-Type", "application/octet-stream");
		con.setRequestMethod("GET");
		
		con.connect();
		
		if (con.getResponseCode() == HttpsURLConnection.HTTP_OK) {
			in = con.getInputStream();
		}
		
		if (in == null) {
			return false;
		}
		
		FileOutputStream fos = new FileOutputStream(new File(target));
		int line = -1;
		
		while ((line = in.read()) != -1) {
			fos.write(line);
		}
		
		in.close();
		fos.close();
		
		return true;
	}
	
	public static void main(String[] args) {
		
		boolean isSuccess = false;
		String url = "https://github.com/favicon.ico";
		String targetPath = "C:\\Users\\cc\\Desktop\\Monalisa.bmp";
		
		String protocol = url.split(":")[0].toLowerCase();
		
		// Usage
		try {
			
			if (protocol.equals("https")) {
				isSuccess = HTTPSdownload(url, targetPath);
			} else {
				isSuccess = HTTPdownload(url, targetPath);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(isSuccess);
		
	}

}
