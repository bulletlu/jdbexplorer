package cn.com.qimingx.utils;

import java.awt.Color;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

public class PDFUtils {
	// 基本字体
	private static BaseFont BASE_FONT = null;
	static {
		try {
			BASE_FONT = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",
					false);
		} catch (Exception e) {
		}
	}

	public static Font createChineseFont() {
		return createChineseFont(12, Font.NORMAL, Color.BLACK);
	}

	public static Font createChineseFont(Color color) {
		return createChineseFont(12, Font.NORMAL, color);
	}

	public static Font createChineseFont(int fontSize) {
		return createChineseFont(fontSize, Font.NORMAL, Color.BLACK);
	}

	public static Font createChineseFont(int fontSize, int fontStyle) {
		return createChineseFont(fontSize, fontStyle, Color.BLACK);
	}

	// 创建中文字体
	public static Font createChineseFont(int fontSize, int fontStyle,
			Color fontColor) {
		if (BASE_FONT == null) {
			throw new RuntimeException("iText BaseFont not init...");
		}
		Font font = new Font(BASE_FONT, fontSize, fontStyle, fontColor);
		return font;
	}
}
