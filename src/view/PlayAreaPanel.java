package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.awt.RenderingHints;

import main.XenoFarmGame;
import model.Alien;
import controller.ResourceManager;

public class PlayAreaPanel extends JPanel {
    private final XenoFarmGame game;
    private final BufferedImage bgImage, tombstoneImg, solarFoodImg, dewFoodImg;
    private final BufferedImage[] astroFrames = new BufferedImage[3];
    private int currentAstroFrame = 0;

    private int dayCount = 1;
    private String statusMsg = "SYSTEM: Welcome to your Xeno-Farm!";
    private final Random random = new Random();
    private final ArrayList<Point> graves = new ArrayList<>();

    // ระบบแสดงผลอาหารชั่วคราว
    private Point feedingLocation = null;
    private int feedingType = -1; // 0: Solar, 1: Dew
    private long feedingStartTime = 0;

    public PlayAreaPanel(XenoFarmGame game) {
        this.game = game;
        setLayout(null);

        bgImage = ResourceManager.loadBufferedImage("images/farm_bg.png");
        tombstoneImg = ResourceManager.loadBufferedImage("images/tombstone.png");
        astroFrames[0] = ResourceManager.loadBufferedImage("images/astro1.png");
        astroFrames[1] = ResourceManager.loadBufferedImage("images/astro2.png");
        astroFrames[2] = ResourceManager.loadBufferedImage("images/astro3.png");
        solarFoodImg = ResourceManager.loadBufferedImage("images/solar_food.png");
        dewFoodImg = ResourceManager.loadBufferedImage("images/dew_food.png");

        Timer gameLoop = new Timer(20, e -> {
            currentAstroFrame = (int) ((System.currentTimeMillis() / 500) % 3);
            for (Alien pet : game.getMyPets()) pet.updateMovement();

            // ซ่อนอาหารหลังผ่านไป 0.5 วินาที
            if (feedingType != -1 && (System.currentTimeMillis() - feedingStartTime) > 500) {
                feedingType = -1;
            }
            repaint();
        });
        gameLoop.start();
        setupButtons();
    }

    private void setupButtons() {
        JButton feedBtn = game.createPixelBtn("FEED ALL", 260, 675, 160, 45, new Color(40, 150, 40));
        feedBtn.addActionListener(e -> feedAction());
        add(feedBtn);

        JButton nextBtn = game.createPixelBtn("NEXT DAY", 430, 675, 160, 45, new Color(60, 120, 200));
        nextBtn.addActionListener(e -> nextDayAction());
        add(nextBtn);

        JButton muteBtn = game.createPixelBtn("MUTE", 600, 675, 100, 45, new Color(150, 50, 50));
        muteBtn.addActionListener(e -> game.toggleMute());
        add(muteBtn);

        JButton menuBtn = game.createPixelBtn("MENU", 50, 675, 120, 45, new Color(80, 80, 80));
        menuBtn.addActionListener(e -> game.changeScreen("Login"));
        add(menuBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (bgImage != null) g2.drawImage(bgImage, 0, 0, 1000, 750, null);

        for (Point p : graves) {
            if (tombstoneImg != null) g2.drawImage(tombstoneImg, p.x, p.y, 60, 60, null);
        }

        // วาดนักบิน + ชื่อผู้เล่น
        drawAstro(g2);

        // วาดสัตว์เลี้ยง (Z-Order)
        ArrayList<Alien> sortedPets = new ArrayList<>(game.getMyPets());
        Collections.sort(sortedPets, Comparator.comparingInt(Alien::getY));

        for (Alien pet : sortedPets) {
            int size = (pet.getDays() >= 6) ? 200 : 100;
            if (pet.isFacingRight()) {
                g2.drawImage(pet.getImage(), pet.getX(), pet.getY(), size, size, null);
            } else {
                g2.drawImage(pet.getImage(), pet.getX() + size, pet.getY(), -size, size, null);
            }
            drawPetHUD(g2, pet, size);
        }

        // วาดอาหาร
        if (feedingType != -1 && feedingLocation != null) {
            BufferedImage food = (feedingType == 0) ? solarFoodImg : dewFoodImg;
            if (food != null) g2.drawImage(food, feedingLocation.x + 40, feedingLocation.y - 30, 50, 50, null);
        }

        renderUI(g2);
    }

    private void drawAstro(Graphics2D g2) {
        int astroX = 420;
        int astroWidth = 130;
        int astroHeight = 160;
        // ระบบลอยตัว
        int floatingY = 510 + (int) (Math.sin(System.currentTimeMillis() * 0.005) * 12);

        if (astroFrames[currentAstroFrame] != null) {
            // 1. วาดตัวนักบิน
            g2.drawImage(astroFrames[currentAstroFrame], astroX, floatingY, astroWidth, astroHeight, null);

            // 2. เตรียมข้อมูลชื่อ
            String name = game.getPlayerName();
            if (name == null || name.isEmpty()) name = "Pilot";

            g2.setFont(game.getPixelFont().deriveFont(Font.BOLD, 15f));
            FontMetrics fm = g2.getFontMetrics();
            int nameWidth = fm.stringWidth(name);

            // 3. ปรับตำแหน่งให้ขยับลงมาใกล้หัวมากขึ้น 🌟
            // เปลี่ยนจาก -25 เป็น -10 (ยิ่งลบน้อย ยิ่งใกล้หัว)
            int nx = astroX + (astroWidth / 2) - (nameWidth / 2);
            int ny = floatingY - 10;

            // 4. วาดกรอบสี่เหลี่ยมโปร่งใสสีขาว
            int paddingX = 12;
            int paddingY = 6;
            int rectX = nx - paddingX;
            int rectY = ny - fm.getAscent() - paddingY;
            int rectWidth = nameWidth + (paddingX * 2);
            int rectHeight = fm.getHeight() + (paddingY * 2);

            // วาดพื้นหลังขาวโปร่งใส
            g2.setColor(new Color(255, 255, 255, 160));
            g2.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 12, 12);

            // วาดเส้นขอบขาว
            g2.setColor(new Color(255, 255, 255, 220));
            g2.drawRoundRect(rectX, rectY, rectWidth, rectHeight, 12, 12);

            // 5. วาดชื่อผู้เล่น สีดำเทา
            g2.setColor(new Color(20, 20, 20));
            g2.drawString(name, nx, ny);
        }
    }
    private void drawPetHUD(Graphics2D g2, Alien pet, int size) {
        int x = pet.getX() + 10, y = pet.getY() - 20;
        g2.setFont(game.getPixelFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(Color.WHITE);
        g2.drawString(pet.getName() + " Lv." + pet.getDays(), x, y - 10);
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, size-20, 10, 5, 5);
        g2.setColor(pet.getHunger() < 30 ? Color.RED : new Color(100, 255, 100));
        g2.fillRoundRect(x, y, (int)((pet.getHunger()/100.0)*(size-20)), 10, 5, 5);
    }

    private void renderUI(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, 1000, 100);
        g2.setFont(game.getPixelFont().deriveFont(26f));
        g2.setColor(Color.YELLOW);
        g2.drawString("GOLD: " + game.getGolds() + " G", 30, 45);
        g2.setColor(Color.WHITE);
        g2.drawString("DAY: " + dayCount, 460, 45);
        g2.setFont(game.getPixelFont().deriveFont(16f));
        g2.setColor(Color.CYAN);
        g2.drawString(statusMsg, 320, 85);
        g2.setFont(game.getPixelFont().deriveFont(14f));
        g2.setColor(new Color(172, 255, 100));
        g2.drawString("SOLAR: " + game.getInventory().getSolarCount(), 30, 72);
        g2.setColor(new Color(100, 180, 255));
        g2.drawString("DEW: " + game.getInventory().getDewCount(), 30, 92);
    }

    private void feedAction() {
        if (game.getMyPets().isEmpty()) return;
        boolean fed = false;
        for (Alien p : game.getMyPets()) {
            if (p.getType().contains("Magma") && game.getInventory().useSolar()) {
                p.feed(); fed = true; feedingType = 0; feedingLocation = new Point(p.getX(), p.getY());
            } else if (p.getType().contains("Aqua") && game.getInventory().useDew()) {
                p.feed(); fed = true; feedingType = 1; feedingLocation = new Point(p.getX(), p.getY());
            }
        }
        if (fed) {
            statusMsg = "SYSTEM: Aliens fed!"; game.playSFX("sound/eat.wav");
            feedingStartTime = System.currentTimeMillis();
        }
    }

    private void nextDayAction() {
        dayCount++;
        game.playSFX("sound/confirm_start.wav");
        int income = 0;
        Iterator<Alien> it = game.getMyPets().iterator();
        while (it.hasNext()) {
            Alien p = it.next();
            p.addDay(); // เรียกใช้เมธอดที่เราเพิ่มใน Alien.java
            p.decreaseHunger(20);
            if (random.nextInt(100) < 8) {
                graves.add(new Point(p.getX(), p.getY())); it.remove();
            } else if (p.getDays() >= 6) {
                income += 50;
            }
        }
        game.addGolds(income);
        game.getInventory().addSolar(5);
        game.getInventory().addDew(5);
        statusMsg = "DAY " + dayCount + ": Earned " + income + " G";
    }
}