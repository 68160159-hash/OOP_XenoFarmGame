package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

import view.*;
import model.Alien;
import model.Inventory;
import controller.ResourceManager;

//*** 🚀 XENO-FARM: GALAXY SANCTUARY Central Controller (Main Class) จัดการสถานะเกม, ระบบเสียง, และการเปลี่ยนหน้าจอ ***//
public class XenoFarmGame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    // --- Game State (Model) ---
    private final List<Alien> myPets = new ArrayList<>();
    private final Inventory inventory = new Inventory(15, 15);
    private int golds = 100;
    private String playerPilotName = "Unknown Pilot"; // เก็บชื่อนักบิน

    // --- Resources & Audio ---
    private Font pixelFont;
    private Clip bgmClip;
    private boolean isMute = false;

    public XenoFarmGame() {
        // 1. โหลด Resource สำคัญ (Font)
        this.pixelFont = ResourceManager.loadPixelFont(18f);

        setupWindow();

        // 2. สร้างหน้าจอต่างๆ (View)
        // ส่ง (this) เข้าไปเพื่อให้หน้าจอย่อยเรียกใช้ Method ในคลาสหลักได้
        mainPanel.add(new LoginScreen(this), "Login");
        mainPanel.add(new SelectionScreen(this), "Select");
        mainPanel.add(new PlayAreaPanel(this), "PlayArea");
        mainPanel.add(new ManualScreen(this), "Manual");

        add(mainPanel);

        // 3. เริ่มระบบเสียงและแสดงหน้าแรก
        playBGM("sound/bensound-innovation.wav");
        cardLayout.show(mainPanel, "Login");

        setVisible(true);
    }

    private void setupWindow() {
        setTitle("XENO-FARM : Galaxy Sanctuary by Patcharada");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // --- 🔹 ระบบจัดการชื่อและสถานะเกม (Getters & Setters) 🔹 ---
    public void setPlayerName(String name) { this.playerPilotName = name; }
    public String getPlayerName() { return playerPilotName; }

    public Inventory getInventory() { return inventory; }
    public List<Alien> getMyPets() { return myPets; }
    public void addPet(Alien pet) { if (pet != null) myPets.add(pet); }
    public int getGolds() { return golds; }
    public void addGolds(int amount) { this.golds += amount; }

    public Font getPixelFont() {
        return (pixelFont != null) ? pixelFont : new Font("Monospaced", Font.BOLD, 18);
    }

    // --- 🔹 การควบคุมหน้าจอ 🔹 ---
    public void changeScreen(String name) {
        cardLayout.show(mainPanel, name);
        playSFX("sound/click_basic.wav"); // เล่นเสียงคลิกทุกครั้งที่เปลี่ยนหน้า
    }

    // --- 🔹 UI Factory (สร้างปุ่มสไตล์ Pixel) 🔹 ---
    public JButton createPixelBtn(String text, int x, int y, int w, int h, Color bg) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setFont(getPixelFont().deriveFont(14f));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createBevelBorder(0, Color.WHITE, Color.DARK_GRAY));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    // --- 🔹 ระบบเสียง (Audio System) 🔹 ---
    public void playSFX(String path) {
        if (isMute) return;
        try (InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            System.err.println("SFX Error: " + path);
        }
    }

    public void playBGM(String path) {
        try (InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        } catch (Exception e) {
            System.err.println("BGM Error: " + path);
        }
    }

    public void toggleMute() {
        this.isMute = !isMute;
        if (bgmClip != null) {
            try {
                if (bgmClip.isControlSupported(BooleanControl.Type.MUTE)) {
                    BooleanControl muteControl = (BooleanControl) bgmClip.getControl(BooleanControl.Type.MUTE);
                    muteControl.setValue(isMute);
                } else {
                    FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(isMute ? gainControl.getMinimum() : 0.0f);
                }
            } catch (Exception e) {
                System.err.println("Mute Control Error: " + e.getMessage());
            }
        }
    }

    // --- 🚀 จุดเริ่มต้นโปรแกรม ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(XenoFarmGame::new);
    }
}