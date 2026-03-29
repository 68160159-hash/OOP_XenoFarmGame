package view;

import javax.swing.*;
import java.awt.*;
import main.XenoFarmGame;

//*** ManualScreen: หน้าจอแสดงคู่มือการเล่น พร้อมเอฟเฟกต์หน้าจอ CRT ***//
public class ManualScreen extends JPanel {
    private final XenoFarmGame game;

    public ManualScreen(XenoFarmGame game) {
        this.game = game;
        setLayout(null);
        setBackground(new Color(10, 10, 25)); // พื้นหลังอวกาศเข้ม

        // --- หัวข้อหน้าคู่มือ (Title) ---

        JLabel title = new JLabel("--- GALAXY MANUAL ---", SwingConstants.CENTER);

        title.setBounds(0, 35, 1000, 60);

        title.setFont(game.getPixelFont().deriveFont(48f)); // ลดขนาดลงนิดนึงให้ดูคลีน

        title.setForeground(Color.YELLOW);

        add(title);
        // --- 1. ส่วน HTML manualText ---
        String manualText = "<html><body style='text-align: center; color: #FFFFFF; font-family: sans-serif;'>"
                // ใช้ width: 100% เพื่อให้ข้อความดันขอบ JLabel เสมอ
                + "<div style='width: 100%;'>"

                + "<div style='margin-bottom: 22px; border-bottom: 1px solid #444; padding-bottom: 10px;'>"
                + "<h2 style='color: #00FFCC; margin-bottom: 5px; font-size: 18px;'>[ 1. CHOOSE YOUR ALIEN ]</h2>"
                + "<p style='font-size: 14px; margin: 0;'>Pick between <b style='color: #FF6666;'>Magma Cow</b> or <b style='color: #66CCFF;'>Aqua Pig</b> to start.</p>"
                + "</div>"

                + "<div style='margin-bottom: 22px; border-bottom: 1px solid #444; padding-bottom: 10px;'>"
                + "<h2 style='color: #FF9900; margin-bottom: 5px; font-size: 18px;'>[ 2. ELEMENTAL FEEDING ]</h2>"
                + "<p style='font-size: 14px; margin: 0;'><b style='color: #FF6666;'>MAGMA:</b> Needs <span style='color: #FFFF00;'>Solar Energy</span> | "
                + "<b style='color: #66CCFF;'>AQUA:</b> Needs <span style='color: #33CCFF;'>Dew Drop</span></p>"
                + "</div>"

                + "<div style='margin-bottom: 22px; border-bottom: 1px solid #444; padding-bottom: 10px;'>"
                + "<h2 style='color: #CCFF00; margin-bottom: 5px; font-size: 18px;'>[ 3. EVOLUTION & GOAL ]</h2>"
                + "<p style='font-size: 14px; margin: 0;'>Reach <b style='color: #00FF00;'>Level 6</b> to Evolve! | Reach <b style='color: #FFD700;'>Level 15</b> to Win!</p>"
                + "<p style='color: #FF5555; font-size: 12px; margin-top: 5px;'><i>⚠ WARNING: Beware of Space Predators! (8% Daily Risk)</i></p>"
                + "</div>"

                + "<div style='padding-top: 5px;'>"
                + "<h2 style='color: #DA70D6; margin-bottom: 5px; font-size: 18px;'>[ 4. ECONOMY ]</h2>"
                + "<p style='font-size: 14px; margin: 0;'>Adults (Lv.6+) produce <b style='color: #FFD700;'>50G</b> daily.</p>"
                + "<p style='font-size: 12px; color: #AAAAAA; margin-top: 5px;'><i>Keep them fed to ensure maximum production.</i></p>"
                + "</div>"

                + "</div></body></html>";

// --- 2. พิกัด JLabel (info) ---
        JLabel info = new JLabel(manualText);
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setVerticalAlignment(SwingConstants.CENTER);

// ปรับค่า Bounds: ขยับ x ไปทางซ้าย (100) และเพิ่ม width เป็น 800 เพื่อให้กรอบใหญ่กว่าข้อความ
        info.setBounds(100, 110, 800, 480);

// ใส่ขอบเรืองแสง (Cyan)
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 255, 80), 2), // กรอบสีฟ้า
                BorderFactory.createEmptyBorder(25, 50, 25, 50) // ระยะห่างจากขอบกรอบถึงตัวหนังสือ (บน, ซ้าย, ล่าง, ขวา)
        ));
        add(info);

        // --- ปุ่ม BACK ---
        JButton backBtn = game.createPixelBtn("BACK TO MENU", 375, 620, 250, 55, new Color(50, 50, 70));
        backBtn.addActionListener(e -> {
            game.playSFX("sound/click_basic.wav");
            game.changeScreen("Login");
        });
        add(backBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. วาด Scanlines (เอฟเฟกต์จอ CRT)
        g2d.setColor(new Color(0, 255, 255, 8)); // ลดความเข้มลงให้อ่านง่ายขึ้น
        for (int i = 0; i < getHeight(); i += 3) {
            g2d.drawLine(0, i, getWidth(), i);
        }

        // 2. วาด Vignette (ขอบมืด)
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {new Color(0, 0, 0, 0), new Color(0, 0, 0, 160)};
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point(getWidth() / 2, getHeight() / 2),
                getWidth() / 1.1f,
                fractions,
                colors
        );
        g2d.setPaint(rgp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}