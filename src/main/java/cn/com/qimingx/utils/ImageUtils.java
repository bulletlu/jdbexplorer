package cn.com.qimingx.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

/**
 * @author inc062805
 * 
 * 提供图片的侦测
 */
public class ImageUtils {
	public static void main(String[] args) {
		File f = new File("c:/temp/images/patch.jpg");
		InputStream input = null;
		try {
			input = new FileInputStream(f);
			BufferedImage image = ImageIO.read(input);
			System.out.println("image:" + image);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	/**
	 * 判断输入流 是不是是图片数据
	 */
	public static boolean isImage(InputStream input) {
		try {
			BufferedImage image = ImageIO.read(input);
			return image != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isImage(File file) {
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			return isImage(input);
		} catch (Exception e) {
			return false;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}
