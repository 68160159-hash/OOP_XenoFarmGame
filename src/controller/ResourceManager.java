package controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

public class ResourceManager {
    // ไฟล์ฟอนต์
    private static final String FONT_NAME = "PixeloidSans.ttf";

    //*** โหลดฟอนต์ Pixel (Multi-path Search) ***//
    public static Font loadPixelFont(float size) {
        // รายชื่อ Path
        String[] possiblePaths = {
                "fonts/" + FONT_NAME,       // เคสมาตรฐานใน resources
                FONT_NAME,                  // เคสอยู่ใน root resources
                "src/fonts/" + FONT_NAME,   // เคสใน IDE
                "res/fonts/" + FONT_NAME    // เคสโฟลเดอร์สำรอง
        };

        for (String path : possiblePaths) {
            try {
                InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream(path);
                if (is != null) {
                    // ใช้ BufferedInputStream เพื่อความเสถียรในการโหลดฟอนต์
                    InputStream bis = new BufferedInputStream(is);
                    return Font.createFont(Font.TRUETYPE_FONT, bis).deriveFont(size);
                }

                // File System
                File file = new File(path);
                if (file.exists()) {
                    return Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(size);
                }
            } catch (Exception e) {
                // ข้ามไปเช็ค Path ถัดไป
            }
        }

        System.err.println("⚠️ Could not find " + FONT_NAME + " in any path. Using Monospaced.");
        return new Font("Monospaced", Font.BOLD, (int) size);
    }

    //*** โหลดรูปภาพแบบฉลาด (ครอบคลุมทั้ง Resource และ File System) ***//
    public static BufferedImage loadBufferedImage(String path) {
        try {
            // 1. ลองโหลดจาก ClassLoader (เหมาะสำหรับ JAR)
            InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream(path);
            if (is != null) return ImageIO.read(new BufferedInputStream(is));

            // 2. ลองโหลดจาก File System หลายๆ รูปแบบ
            String[] searchPaths = { path, "src/" + path, "res/" + path };
            for (String sPath : searchPaths) {
                File file = new File(sPath);
                if (file.exists()) return ImageIO.read(file);
            }

            System.err.println("❌ Image not found: " + path);
        } catch (Exception e) {
            System.err.println("❌ Error loading image: " + path + " | " + e.getMessage());
        }
        return createPlaceholderImage();
    }

    private static BufferedImage createPlaceholderImage() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.MAGENTA); // สีชมพูสะท้อนแสง (Placeholder มาตรฐาน)
        g.fillRect(0, 0, 32, 32);
        g.fillRect(32, 32, 32, 32);
        g.dispose();
        return img;
    }
}