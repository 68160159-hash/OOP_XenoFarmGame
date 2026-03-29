package model;

import java.awt.image.BufferedImage;
import controller.ResourceManager;

public class Alien {
    private String name, type;
    private int days = 1;
    private int hunger = 100; // --- เพิ่ม: ค่าความหิวเริ่มต้น (0-100) ---
    private int x, y, targetX, targetY;
    private boolean facingRight = true;
    private BufferedImage sprite;

    public Alien(String name, String type) {
        // ถ้าผู้เล่นไม่กรอกชื่อมา ให้ตั้งชื่อเริ่มต้นตามประเภท
        this.name = (name == null || name.trim().isEmpty() || name.equals("ENTER PET NAME...")) ? "XenoPet" : name;
        this.type = type;

        // สุ่มจุดเกิดให้อยู่ในโดม
        this.x = 200 + (int)(Math.random() * 500);
        this.y = 300 + (int)(Math.random() * 200);
        this.targetX = x;
        this.targetY = y;
        updateSprite();
    }

    // --- ระบบขยับ (AI เดินสุ่มในฉาก) ---
    public void updateMovement() {
        // เดินเข้าหาจุดหมาย (Target)
        if (x < targetX) {
            x += 1; // ปรับความเร็วให้ดูนุ่มนวลขึ้น
            facingRight = true;
        } else if (x > targetX) {
            x -= 1;
            facingRight = false;
        }

        if (y < targetY) y += 1;
        else if (y > targetY) y -= 1;

        // เมื่อถึงจุดหมาย ให้สุ่มจุดหมายใหม่
        if (Math.abs(x - targetX) < 5 && Math.abs(y - targetY) < 5) {
            if (Math.random() < 0.02) { // มีโอกาส 2% ในแต่ละเฟรมที่จะเริ่มเดินใหม่
                targetX = 200 + (int)(Math.random() * 600); // เดิม 150-800
                targetY = 300 + (int)(Math.random() * 200); // เดิม 280-530
            }
        }
    }

    // --- ระบบการเลี้ยงดู ---
    public void feed() {
        this.hunger = Math.min(100, this.hunger + 30);
        // เราจะไม่บวก days ที่นี่แล้ว เพราะเราจะไปบวกตอน Next Day แทน
        // หรือถ้าอยากให้ Feed แล้วเลเวลขึ้นด้วย
        updateSprite();
    }

    // 🌟 เพิ่มเมธอดนี้เพื่อให้ PlayAreaPanel เรียกใช้ตอนกด Next Day
    public void addDay() {
        this.days++;
        updateSprite(); // เช็คการเปลี่ยนร่างทันทีเมื่อเลเวลถึง
    }

    public void decreaseHunger(int amount) {
        this.hunger = Math.max(0, this.hunger - amount);
    }

    // --- ระบบเปลี่ยนร่าง (Evolution) ---
    public void updateSprite() {
        String path;
        // เช็คประเภทและเลเวล (6 ขึ้นไปเปลี่ยนร่าง)
        if (type.contains("Cow") || type.contains("Magma")) {
            path = (days >= 6) ? "images/magma_cow_adult.png" : "images/cow_walk.png";
        } else {
            path = (days >= 6) ? "images/aqua_pig_adult.png" : "images/pig_walk.png";
        }

        BufferedImage img = ResourceManager.loadBufferedImage(path);
        if (img != null) this.sprite = img;
    }

    // --- Getter / Setter ---
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDays() { return days; }
    public int getHunger() { return hunger; } // สำหรับวาดแถบเลือด
    public String getName() { return name; }
    public String getType() { return type; }
    public BufferedImage getImage() { return sprite; }
    public boolean isFacingRight() { return facingRight; }
}