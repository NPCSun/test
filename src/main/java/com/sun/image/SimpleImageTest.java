package com.sun.image;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.alibaba.simpleimage.ImageFormat;
import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.render.ReadRender;
import com.alibaba.simpleimage.render.ScaleParameter;
import com.alibaba.simpleimage.render.ScaleParameter.Algorithm;
import com.alibaba.simpleimage.render.ScaleRender;
import com.alibaba.simpleimage.render.WriteParameter;
import com.alibaba.simpleimage.render.WriteRender;

public class SimpleImageTest {

	public static void test() {
		File in = new File("D:\\image\\timg.jpg"); //原图片
		File out = new File("D:\\image\\timg1.jpg"); //目的图片
		WriteParameter writeParam = new WriteParameter(); //输出参数，默认输出格式为JPEG
		writeParam.setDefaultQuality(0.7f);
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		ImageRender wr = null;
		try {
			outStream = new FileOutputStream(out);
			inStream = new FileInputStream(in);
			BufferedImage originalPic = ImageIO.read(inStream);
			int imageWidth = originalPic.getWidth();
			int imageHeight = originalPic.getHeight();

			BufferedImage newPic = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);

			ColorConvertOp cco = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), null);
			cco.filter(originalPic, newPic);
			ImageIO.write(newPic, "jpeg", outStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);
			if (wr != null) {
				try {
					wr.dispose(); 
				} catch (SimpleImageException ignore) {
					// skip ... 
				}
			}
		}

	}

	public static void testCompress() {
		File in = new File("D:\\image\\1.jpg"); //原图片
		File out = new File("D:\\image\\2.jpg"); //目的图片
		
		WriteParameter writeParam = new WriteParameter(); //输出参数，默认输出格式为JPEG
		writeParam.setDefaultQuality(0.5f);
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		
		ImageRender imageRender = null;
		ImageRender scaleRender = null;
		ImageRender writeRender = null;
		try {
			inStream = new FileInputStream(in);
			outStream = new FileOutputStream(out);
			
			imageRender = new ReadRender(inStream);
			ScaleParameter scaleParam = new ScaleParameter(5000, 1479, Algorithm.AUTO);
			scaleRender = new ScaleRender(imageRender, scaleParam);
			
			writeRender = new WriteRender(scaleRender, outStream, ImageFormat.JPEG, writeParam);
			writeRender.render(); //触发图像处理
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//图片文件输入输出流必须记得关闭
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);
			//
			if (imageRender != null) {
				try {
					if(imageRender!=null){
						imageRender.dispose();
					}
					if(scaleRender!=null){
						scaleRender.dispose();
					}
					if(writeRender!=null){
						writeRender.dispose();
					}
				} catch (SimpleImageException ignore) {
					// skip ... 
				}
			}
		}
	}

	public static void main(String[] args) {
		testCompress();
		//test();
	}

}
