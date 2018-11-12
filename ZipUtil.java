package javadev;

import java.io.*;
// Apache antのant.jarへのパスが必要
import org.apache.tools.zip.*;

public class ZipUtil {

	/**
	 * 指定されたディレクトリをZIPに固める
	 * @param dirName 固めたいファイルまたはディレクトリ
	 */
	public static void zip(String dirName) {
		
		String zipFilePath;
		
		File baseFile = new File(dirName);
		
		// ディレクトリならそのまま、ファイルであれば拡張子を取り除く
		if (baseFile.isDirectory()) {
			zipFilePath = dirName + ".zip";
		} else {
			dirName = dirName.split("\\.")[0];
			zipFilePath = dirName + ".zip";
		}
		
		File zipFile = new File(zipFilePath);
		
		// Zipの階層を作る際に除外するディレクトリパス
		String[] basePath = baseFile.getPath().split("\\\\");
		String absolPath = "";
		for (int i=0; i<basePath.length - 1; i++) {
			absolPath += basePath[i];
			absolPath += "\\"; 
		}
		
		ZipOutputStream zos = null;
		
		try {
			zos = new ZipOutputStream(new FileOutputStream(zipFile));
			
			// 再帰
			archive(baseFile, zos, absolPath);
			
			System.out.println("finish.");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zos != null) {
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * 再帰処理用
	 * 
	 * @param baseFile 固める元のファイル
	 * @param zos
	 * @param absolPath Zipの中のネストから除外する上位パス
	 * @throws IOException
	 */
	private static void archive(File baseFile, ZipOutputStream zos, String absolPath) throws IOException {
		System.out.println("zipping file " + baseFile.getName());
		if (baseFile.isDirectory()) {
			File[] files = baseFile.listFiles();
			for (File f : files) {
				archive(f, zos, absolPath);
			}
		} else {
			zos.setLevel(5);
			
			// 文字コード指定
			zos.setEncoding("Shift_JIS");
			
			zos.putNextEntry(new ZipEntry(baseFile.getPath().replace(absolPath, "").replace("\\", "/")));
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(baseFile));
			
			int readSize  = 0;
			byte buf[] = new byte[1024];
			while ((readSize = bis.read(buf, 0, buf.length)) != -1 ) {
				zos.write(buf, 0, readSize);
			}
			
			bis.close();
			zos.closeEntry();
		}

	}
	
	public static void main(String[] args) {
		// for debug
		zip("かためたいファイル");
	}


}
