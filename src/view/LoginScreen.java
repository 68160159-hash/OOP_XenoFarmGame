package view;

import javax.swing.*;
import java.awt.*;
import main.XenoFarmGame;
import controller.ResourceManager;

public class LoginScreen extends JPanel {
    private final XenoFarmGame game;
    private final JTextField nameField;
    private final Image bgImage = ResourceManager.loadBufferedImage("images/menu_bg.png");

    public LoginScreen(XenoFarmGame game) {
        this.game = game;
        setLayout(null);

        // --- 1. ส่วนกรอกชื่อผู้เล่น (ตำแหน่ง Y = 410) ---
        nameField = new JTextField("ENTER PLAYER NAME...");
        nameField.setBounds(350, 410, 300, 45);
        nameField.setFont(game.getPixelFont().deriveFont(20f));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(45, 45, 45));
        nameField.setForeground(Color.GRAY);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        // เพิ่ม FocusListener เพื่อให้ Placeholder หายไปเมื่อคลิก
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (nameField.getText().equals("ENTER PLAYER NAME...")) {
                    nameField.setText("");
                    nameField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setForeground(Color.GRAY);
                    nameField.setText("ENTER PLAYER NAME...");
                }
            }
        });
        add(nameField);

        // --- 2. การวางปุ่ม ---

        // ปุ่ม START GAME (ตำแหน่ง Y = 490)
        JButton startBtn = game.createPixelBtn("START GAME", 350, 490, 300, 70, new Color(110, 210, 110));
        startBtn.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            // เช็คทั้งค่าว่างและค่า Placeholder
            if (playerName.isEmpty() || playerName.equals("ENTER PLAYER NAME...")) {
                JOptionPane.showMessageDialog(this, "Please enter your name, Pilot!", "System", JOptionPane.INFORMATION_MESSAGE);
            } else {
                game.setPlayerName(playerName);
                game.playSFX("sound/confirm_start.wav");
                game.changeScreen("Select");
            }
        });
        add(startBtn);

        // ปุ่ม HOW TO PLAY (ตำแหน่ง Y = 570)
        JButton manualBtn = game.createPixelBtn("HOW TO PLAY", 350, 570, 300, 70, new Color(130, 130, 130));
        manualBtn.addActionListener(e -> {
            game.playSFX("sound/click_basic.wav");
            game.changeScreen("Manual");
        });
        add(manualBtn);

        // --- 3. ปุ่ม MUTE (มุมบนขวา) ---
        JButton muteBtn = game.createPixelBtn("MUTE", 870, 20, 100, 40, new Color(80, 80, 90));
        muteBtn.addActionListener(e -> {
            game.toggleMute();
            if (muteBtn.getText().equals("MUTE")) {
                muteBtn.setText("UNMUTE");
                muteBtn.setBackground(new Color(150, 50, 50));
            } else {
                muteBtn.setText("MUTE");
                muteBtn.setBackground(new Color(80, 80, 90));
            }
        });
        add(muteBtn);

        // Timer สำหรับ Animation ชื่อเกม
        new Timer(30, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        }

        // ชื่อเกมแบบลอยตัว (Floating Animation)
        int floatingY = (int) (Math.sin(System.currentTimeMillis() * 0.003) * 10);
        int baseY = 140;

        String titleText = "XENO-FARM";
        g2d.setFont(game.getPixelFont().deriveFont(90f));

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(titleText);
        int startX = (getWidth() - textWidth) / 2;

        // วาดเงาและตัวหนังสือสีเหลือง
        g2d.setColor(Color.BLACK);
        g2d.drawString(titleText, startX + 6, baseY + 6 + floatingY);
        g2d.setColor(new Color(255, 255, 0));
        g2d.drawString(titleText, startX, baseY + floatingY);
    }
}