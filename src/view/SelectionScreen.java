package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import main.XenoFarmGame;
import model.Alien;
import controller.ResourceManager;

public class SelectionScreen extends JPanel {
    private final JTextField petNameField; // เปลี่ยนชื่อให้ชัดเจน
    private String selectedType = "Magma Cow";
    private final XenoFarmGame game;
    private BufferedImage magmaImg, aquaImg;

    public SelectionScreen(XenoFarmGame game) {
        this.game = game;
        setLayout(null);
        setBackground(new Color(15, 15, 35)); // สีพื้นหลังโทนอวกาศเข้ม

        // โหลดรูปภาพ
        magmaImg = ResourceManager.loadBufferedImage("images/magma_cow.png");
        aquaImg = ResourceManager.loadBufferedImage("images/aqua_pig.png");

        // --- 1. ช่องกรอกชื่อสัตว์เลี้ยง (พร้อมระบบ Placeholder) ---
        petNameField = new JTextField("ENTER PET NAME...");
        petNameField.setBounds(350, 60, 300, 45); // ขยับขึ้นด้านบน
        petNameField.setFont(game.getPixelFont().deriveFont(20f));
        petNameField.setHorizontalAlignment(JTextField.CENTER);
        petNameField.setBackground(new Color(240, 240, 240));
        petNameField.setForeground(Color.GRAY);
        petNameField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        // ระบบคลิกแล้วคำหายไป
        petNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (petNameField.getText().equals("ENTER PET NAME...")) {
                    petNameField.setText("");
                    petNameField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (petNameField.getText().isEmpty()) {
                    petNameField.setForeground(Color.GRAY);
                    petNameField.setText("ENTER PET NAME...");
                }
            }
        });
        add(petNameField);

        // --- 2. ปุ่มเลือกประเภทสัตว์เลี้ยง ---
        JButton selectMagma = game.createPixelBtn("MAGMA COW", 250, 480, 200, 50, new Color(150, 50, 50));
        selectMagma.addActionListener(e -> {
            selectedType = "Magma Cow";
            game.playSFX("sound/click_basic.wav");
            repaint();
        });
        add(selectMagma);

        JButton selectAqua = game.createPixelBtn("AQUA PIG", 550, 480, 200, 50, new Color(50, 50, 150));
        selectAqua.addActionListener(e -> {
            selectedType = "Aqua Pig";
            game.playSFX("sound/click_basic.wav");
            repaint();
        });
        add(selectAqua);

        // --- 3. ปุ่มยืนยันและเริ่มเกม ---
        JButton startBtn = game.createPixelBtn("CONFIRM & START", 350, 580, 300, 70, new Color(45, 150, 45));
        startBtn.addActionListener(e -> {
            String pName = petNameField.getText().trim();
            if (pName.isEmpty() || pName.equals("ENTER PET NAME...")) {
                JOptionPane.showMessageDialog(this, "Please name your alien friend!");
            } else {
                game.addPet(new Alien(pName, selectedType));
                game.playSFX("sound/confirm_start.wav");
                game.changeScreen("PlayArea");
            }
        });
        add(startBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // เลือกรูปที่จะแสดง
        BufferedImage preview = selectedType.equals("Magma Cow") ? magmaImg : aquaImg;

        if (preview != null) {
            // --- ปรับขนาดรูปสัตว์ให้ใหญ่ขึ้นเป็น 300x300 ---
            // คำนวณให้กึ่งกลางจอ: (1000 - 300) / 2 = 350
            int imgSize = 300;
            int x = (getWidth() - imgSize) / 2;
            int y = 140; // ตำแหน่งความสูงรูป

            g2d.drawImage(preview, x, y, imgSize, imgSize, null);

            // เพิ่มกรอบแสงรอบตัวที่เลือก (Highlight)
            g2d.setColor(selectedType.equals("Magma Cow") ? new Color(255, 100, 100, 50) : new Color(100, 100, 255, 50));
            g2d.fillRoundRect(x - 10, y - 10, imgSize + 20, imgSize + 20, 20, 20);
        }
    }
}