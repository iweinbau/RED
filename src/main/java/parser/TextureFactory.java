package parser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextureFactory {

    HashMap<String,Image> alreadyLoadedImages = new HashMap<>();

    public Image getImageTexture(String imageFile) {
        if (alreadyLoadedImages.containsKey(imageFile)) {
            return alreadyLoadedImages.get(imageFile);
        } else {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream resource = classLoader.getResourceAsStream(imageFile);
            try {
                BufferedImage imageBuffer = ImageIO.read(resource);
                if (imageBuffer == null)
                    throw new IOException("File not Found");
                return new Image(imageBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0,0, Color.magenta.getRGB());
        return new Image(image);
    }
}
