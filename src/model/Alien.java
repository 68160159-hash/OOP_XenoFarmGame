package model;

import java.awt.image.BufferedImage;
import controller.ResourceManager;

public class Alien {
    private String name, type;
    private int days = 1;
    private int hunger = 100;
    private int x, y, targetX, targetY;
    private boolean facingRight = true;
    private BufferedImage sprite;

    public Alien(String name, String type) {
        this.name = (name == null || name.trim().isEmpty() || name.equals("ENTER PET NAME...")) ? "XenoPet" : name;
        this.type = type;

        // สุ่มจุดเกิดเริ่มต้นให้อยู่ในโซนปลอดภัย (ฝั่งซ้ายของโดมก่อน)
        this.x = 200 + (int)(Math.random() * 300);
        this.y = 300 + (int)(Math.random() * 150);
        this.targetX = x;
        this.targetY = y;
        updateSprite();
    }

    // --- แก้ไขระบบขยับ เพื่อหลบนักบิน ---
    public void updateMovement() {
        if (x < targetX) {
            x += 1;
            facingRight = true;
        } else if (x > targetX) {
            x -= 1;
            facingRight = false;
        }

        if (y < targetY) y += 1;
        else if (y > targetY) y -= 1;

        if (Math.abs(x - targetX) < 5 && Math.abs(y - targetY) < 5) {
            if (Math.random() < 0.02) {
                /* 💡 แก้ไขตรงนี้: จำกัดขอบเขตการสุ่ม (Boundary Box)
                   เดิม: x (200-800), y (300-500)
                   ใหม่: บีบพื้นที่ x ให้เล็กลงทางฝั่งขวา เพื่อไม่ให้ไปทับนักบินที่ยืนอยู่ช่วง x > 650
                */
                targetX = 200 + (int)(Math.random() * 450); // สุ่ม X ได้แค่ 200 ถึง 650 (หลบขอบขวา)
                targetY = 320 + (int)(Math.random() * 180); // สุ่ม Y ให้อยู่ในพื้นโดม
            }
        }
    }

    public void feed() {
        this.hunger = Math.min(100, this.hunger + 30);
        updateSprite();
    }

    public void addDay() {
        this.days++;
        updateSprite();
    }

    public void decreaseHunger(int amount) {
        this.hunger = Math.max(0, this.hunger - amount);
    }

    public void updateSprite() {
        String path;
        if (type.contains("Cow") || type.contains("Magma")) {
            path = (days >= 6) ? "images/magma_cow_adult.png" : "images/cow_walk.png";
        } else {
            path = (days >= 6) ? "images/aqua_pig_adult.png" : "images/pig_walk.png";
        }

        BufferedImage img = ResourceManager.loadBufferedImage(path);
        if (img != null) this.sprite = img;
    }

    // Getter / Setter เหมือนเดิม
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDays() { return days; }
    public int getHunger() { return hunger; }
    public String getName() { return name; }
    public String getType() { return type; }
    public BufferedImage getImage() { return sprite; }
    public boolean isFacingRight() { return facingRight; }
}