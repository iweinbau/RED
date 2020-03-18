package parser;

import math.Point2D;
import math.RGBSpectrum;

import java.awt.image.BufferedImage;

public class Image {

    BufferedImage imageBuffer;

    int imageWidth;
    int imageHeight;

    public Image(BufferedImage imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.imageWidth = imageBuffer.getWidth();
        this.imageHeight = imageBuffer.getHeight();
    }

    public RGBSpectrum lookUp(Point2D uv) {
        int x = (int) Math.floor((uv.getX()-Math.floor(uv.getX())) * (imageWidth-1));
        int y = (int) Math.floor((uv.getY()-Math.floor(uv.getY())) * (imageHeight-1));
        int pixel = imageBuffer.getRGB(x,y);
        double r = ((pixel & 0xff0000) >> 16) / 255.;
        double g = ((pixel & 0xff00) >> 8) / 255.;
        double b = ((pixel & 0xff)) / 255.;
        return new RGBSpectrum(r,g,b);
    }
}
