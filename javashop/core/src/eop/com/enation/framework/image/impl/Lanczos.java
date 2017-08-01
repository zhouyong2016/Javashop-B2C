package com.enation.framework.image.impl;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 
 * @author Kanon
 *
 */
public class Lanczos {
    private static final double WORK_LOAD_FACTOR = 0.265;
    
    private static final double LANCZOS_SUPPORT = 3;
    private static final double LANCZOS_WINDOW = 3;
    private static final double LANCZOS_SCALE = 1;
    private static final double LANCZOS_BLUR = 1;
 
    private static final double EPSILON = 1.0e-6;
 
    private static class ContributionInfo {
        private double weight;
        private int pixel;
    }
     
    public static BufferedImage resizeImage(BufferedImage image, double ratio) {
        return resizeImage(image, (int)(image.getWidth() * ratio + 0.5), (int)(image.getHeight() * ratio + 0.5));        
    }
 
    public static BufferedImage resizeImage(BufferedImage image, double xRatio, double yRatio) {
        return resizeImage(image, (int)(image.getWidth() * xRatio + 0.5), (int)(image.getHeight() * yRatio + 0.5));        
    }
    /***
     * 
     * @param image 图片
     * @param width 宽度
     * @param height 高度
     * @return
     */
    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
    	
    	//缩小图片宽度百分比
        double xFactor = width * 1.0 / image.getWidth();
        
        //缩小图片高度百分比
        double yFactor = height * 1.0 / image.getHeight();
 
        //新建BufferedImage
        BufferedImage resizeImage = new BufferedImage(width, height, image.getType());
        BufferedImage filterImage = null;
 
        if (xFactor * yFactor > WORK_LOAD_FACTOR) {
            filterImage = new BufferedImage(width, image.getHeight(), image.getType());
            horizontalFilter(image, filterImage, xFactor);
            verticalFilter(filterImage, resizeImage, yFactor);
        } else {
            filterImage = new BufferedImage(image.getWidth(), height, image.getType());
            verticalFilter(image, filterImage, yFactor);
            horizontalFilter(filterImage, resizeImage, xFactor);
        }
        return resizeImage;
    }
 
    private static void verticalFilter(BufferedImage image, BufferedImage resizeImage, double yFactor) {
       
    	double scale = Math.max(1.0 / yFactor, 1.0);
        double support = scale * LANCZOS_SUPPORT;
        if (support < 0.5) {
            support = 0.5;
            scale = 1.0;
        }
        scale = 1.0 / scale;
 
        for (int y = 0; y < resizeImage.getHeight(); y++) {
            double center = (y + 0.5) / yFactor;
            int start = (int) (Math.max(center - support - EPSILON, 0.0) + 0.5);
            int stop = (int) (Math.min(center + support, image.getHeight()) + 0.5);
            double density = 0.0;
            ContributionInfo[] contribution = new ContributionInfo[stop - start];
            int n;
            for (n = 0; n < (stop - start); n++) {
                contribution[n] = new ContributionInfo();
                contribution[n].pixel = start + n;
                contribution[n].weight = getResizeFilterWeight(scale * (start + n - center + 0.5));
                density += contribution[n].weight;
            }
 
            if ((density != 0.0) && (density != 1.0)) {
                density = 1.0 / density;
                for (int i = 0; i < n; i++)
                    contribution[i].weight *= density;
            }
            for (int x = 0; x < resizeImage.getWidth(); x++) {
                double red = 0;
                double green = 0;
                double blue = 0;
                for (int i = 0; i < n; i++) {
                    double alpha = contribution[i].weight;
                    int rgb = image.getRGB(x, contribution[i].pixel);
                    red += alpha * ((rgb >> 16) & 0xFF);
                    green += alpha * ((rgb >> 8) & 0xFF);
                    blue += alpha * (rgb & 0xFF);
                }
                int rgb = roundToQuantum(red) << 16 | roundToQuantum(green) << 8
                        | roundToQuantum(blue);
                resizeImage.setRGB(x, y, rgb);
            }
        }
    }
 
    private static void horizontalFilter(BufferedImage image, BufferedImage resizeImage,
            double xFactor) {
        double scale = Math.max(1.0 / xFactor, 1.0);
        double support = scale * LANCZOS_SUPPORT;
        if (support < 0.5) {
            support = 0.5;
            scale = 1.0;
        }
        scale = 1.0 / scale;
 
        for (int x = 0; x < resizeImage.getWidth(); x++) {
            double center = (x + 0.5) / xFactor;
            int start = (int) (Math.max(center - support - EPSILON, 0.0) + 0.5);
            int stop = (int) (Math.min(center + support, image.getWidth()) + 0.5);
            double density = 0.0;
            ContributionInfo[] contribution = new ContributionInfo[stop - start];
            int n;
            for (n = 0; n < (stop - start); n++) {
                contribution[n] = new ContributionInfo();
                contribution[n].pixel = start + n;
                contribution[n].weight = getResizeFilterWeight(scale * (start + n - center + 0.5));
                density += contribution[n].weight;
            }
 
            if ((density != 0.0) && (density != 1.0)) {
                density = 1.0 / density;
                for (int i = 0; i < n; i++)
                    contribution[i].weight *= density;
            }
            for (int y = 0; y < resizeImage.getHeight(); y++) {
                double red = 0;
                double green = 0;
                double blue = 0;
                for (int i = 0; i < n; i++) {
                    double alpha = contribution[i].weight;
                    int rgb = image.getRGB(contribution[i].pixel, y);
                    red += alpha * ((rgb >> 16) & 0xFF);
                    green += alpha * ((rgb >> 8) & 0xFF);
                    blue += alpha * (rgb & 0xFF);
                }
                int rgb = roundToQuantum(red) << 16 | roundToQuantum(green) << 8
                        | roundToQuantum(blue);
                resizeImage.setRGB(x, y, rgb);
            }
        }
    }
 
    private static double getResizeFilterWeight(double x) {
        double blur = Math.abs(x) / LANCZOS_BLUR;
        double scale = LANCZOS_SCALE / LANCZOS_WINDOW;
        scale = sinc(blur * scale);
        return scale * sinc(blur);
    }
 
    private static double sinc(double x) {
        if (x == 0.0)
            return 1.0;
        return Math.sin(Math.PI * x) / (Math.PI * x);
    }
 
    private static int roundToQuantum(double value) {
        if (value <= 0.0)
            return 0;
        if (value >= 255)
            return 255;
        return (int) (value + 0.5);
    }
     
    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("d:/1.jpg"));
        ImageIO.write(resizeImage(image, 800, 800), "JPEG", new File("d:/1d800.jpg"));
    }
}
