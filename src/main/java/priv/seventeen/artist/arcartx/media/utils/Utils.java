/*
    Copyright (C) 2025 17Artist

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package priv.seventeen.artist.arcartx.media.utils;

import priv.seventeen.artist.arcartx.media.MediaPlayerModule;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

/**
 * @program: ArcartX_MediaPlayer
 * @description: 工具
 * @author: 17Artist
 * @create: 2025-07-22 20:02
 **/
public class Utils {

    public static int[] convertBufferedImageToIntArray(BufferedImage image) {
        // 检查图像格式
        if(image.getType() != BufferedImage.TYPE_INT_ARGB){
            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = newImage.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = newImage;
        }
        return  ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    }

    public static String parseUrl(String url) {
        String target = null;
        File file = new File(MediaPlayerModule.getMediaFolder(),url);
        if(file.exists()) { target = file.getAbsolutePath(); }
        else if(url.startsWith("http") || url.startsWith("https")) { target = url; }
        return target;
    }

}
