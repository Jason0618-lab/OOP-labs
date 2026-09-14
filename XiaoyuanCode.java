import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * High-fidelity 2D atelier for Aug14_2026_fixed.
 *
 * Compile outside this folder so .class files never land here:
 *   javac -encoding UTF-8 -d /tmp/aug14_gui_build Aug14_2026_GUI.java
 *   java -cp /tmp/aug14_gui_build Aug14_2026_GUI
 */
public class XiaoyuanCode {

    public static void main(String[] args) {
        System.setProperty("apple.awt.application.name", "Aug14 Atelier");
        System.setProperty("apple.awt.application.appearance", "NSAppearanceNameVibrantDark");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AtelierFrame frame = new AtelierFrame();
                frame.setVisible(true);
            }
        });
    }

    static int[] convertDays(int days) {
        int years = days / 365;
        int months = (days % 365) / 30;
        int remain = (days % 365) % 30;
        return new int[] {years, months, remain};
    }

    static String compareToZero(int x) {
        if (x > 0) {
            return x + " is more than zero";
        }
        if (x == 0) {
            return x + " is equal to zero";
        }
        return x + " is less than zero";
    }

    static final class Ink {
        static final Color VOID = new Color(7, 8, 11);
        static final Color NIGHT = new Color(14, 16, 22);
        static final Color WALNUT = new Color(42, 26, 16);
        static final Color WALNUT_DEEP = new Color(22, 13, 8);
        static final Color BRASS = new Color(196, 154, 74);
        static final Color BRASS_LIT = new Color(236, 208, 128);
        static final Color BRASS_DIM = new Color(92, 64, 28);
        static final Color BRASS_OXIDE = new Color(74, 88, 52);
        static final Color IVORY = new Color(244, 234, 210);
        static final Color PARCHMENT = new Color(226, 208, 168);
        static final Color PARCHMENT_EDGE = new Color(168, 140, 92);
        static final Color INK = new Color(28, 22, 16);
        static final Color INK_SOFT = new Color(62, 48, 34);
        static final Color VERMILION = new Color(168, 36, 28);
        static final Color VERMILION_LIT = new Color(210, 72, 48);
        static final Color VERDIGRIS = new Color(28, 102, 96);
        static final Color VERDIGRIS_LIT = new Color(72, 156, 142);
        static final Color GRAPHITE = new Color(58, 62, 70);
        static final Color LAMP = new Color(255, 196, 118);
        static final Color LAMP_SOFT = new Color(255, 168, 82, 70);
        static final Color FOIL = new Color(214, 176, 92);
        static final Color WAX = new Color(132, 22, 26);
        static final Color WAX_LIT = new Color(176, 42, 38);
        static final Color CREAM = new Color(250, 243, 226);
        static final Color SMOKE = new Color(18, 20, 26, 180);
    }

    static final class Type {
        final Font display;
        final Font displayItalic;
        final Font body;
        final Font label;
        final Font numeral;
        final Font micro;

        Type() {
            display = first(new String[] {
                "Didot", "Hoefler Text", "Baskerville", "Palatino", "Georgia", "Serif"
            }, Font.PLAIN, 54);
            displayItalic = display.deriveFont(Font.ITALIC);
            body = first(new String[] {
                "Iowan Old Style", "Palatino", "Baskerville", "Georgia", "Serif"
            }, Font.PLAIN, 16);
            label = first(new String[] {
                "Futura", "Avenir Next", "Optima", "Avenir", "SansSerif"
            }, Font.PLAIN, 11);
            numeral = first(new String[] {
                "Avenir Next", "Futura", "Optima", "Avenir", "Verdana", "SansSerif"
            }, Font.PLAIN, 28);
            micro = label.deriveFont(9.5f);
        }

        static Font first(String[] names, int style, float size) {
            String[] installed = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for (int i = 0; i < names.length; i++) {
                for (int j = 0; j < installed.length; j++) {
                    if (installed[j].equalsIgnoreCase(names[i])) {
                        return new Font(installed[j], style, (int) size).deriveFont(size);
                    }
                }
            }
            return new Font(Font.SERIF, style, (int) size).deriveFont(size);
        }
    }

    static final class Tex {
        final BufferedImage walnut;
        final BufferedImage paper;
        final BufferedImage brass;
        final BufferedImage grain;
        final BufferedImage leather;
        final BufferedImage dust;

        Tex() {
            walnut = walnut(768, 512);
            paper = paper(640, 800);
            brass = brass(384, 192);
            grain = grain(256, 256, 0x51L);
            leather = leather(320, 320);
            dust = dust(384, 384);
        }

        static BufferedImage walnut(int w, int h) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    float u = x / (float) w;
                    float v = y / (float) h;
                    float warp = fbm(u * 3.2f, v * 1.4f, null) * 1.6f;
                    float fine = fbm(u * 28f, v * 9f, null);
                    float pore = hash(x * 19, y * 23);
                    float grain = (float) Math.sin((u * 11.0 + v * 0.35 + warp) * Math.PI * 2.0);
                    float t = 0.50f + grain * 0.16f + fine * 0.10f + (pore > 0.984f ? -0.12f : 0f);
                    t += (0.5f - v) * 0.04f;
                    int r = clampByte(46 + t * 62);
                    int g = clampByte(24 + t * 28);
                    int b = clampByte(13 + t * 12);
                    img.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }
            return img;
        }

        static BufferedImage paper(int w, int h) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Random rng = new Random(0x50415045);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    float u = x / (float) w;
                    float v = y / (float) h;
                    float pulp = fbm(x * 0.035f, y * 0.033f, null);
                    float fiber = fbm(x * 0.09f + y * 0.012f, y * 0.07f, null);
                    int speck = rng.nextInt(1400);
                    float fox = 0f;
                    fox += 0.09f * (1f - clamp01(dist(u, v, 0.18f, 0.22f) * 4.2f));
                    fox += 0.06f * (1f - clamp01(dist(u, v, 0.82f, 0.78f) * 3.4f));
                    fox += 0.04f * (1f - clamp01(dist(u, v, 0.70f, 0.16f) * 5.0f));
                    float edge = Math.min(Math.min(u, 1f - u), Math.min(v, 1f - v));
                    float shade = 1f - smooth(1f - clamp01(edge * 8f)) * 0.08f;
                    float t = 0.55f + pulp * 0.18f + fiber * 0.10f;
                    int r = clampByte((226 + t * 18 - fox * 34 - (speck < 5 ? 16 : 0)) * shade);
                    int g = clampByte((208 + t * 14 - fox * 42 - (speck < 5 ? 12 : 0)) * shade);
                    int b = clampByte((168 + t * 10 - fox * 24 - (speck < 5 ? 8 : 0)) * shade);
                    if (speck == 1) {
                        r = clampByte(r - 28);
                        g = clampByte(g - 24);
                        b = clampByte(b - 16);
                    }
                    img.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }
            return img;
        }

        static BufferedImage brass(int w, int h) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Random rng = new Random(0x42524153);
            float[] hair = new float[w];
            for (int x = 0; x < w; x++) {
                hair[x] = rng.nextFloat();
            }
            for (int y = 0; y < h; y++) {
                float v = y / (float) h;
                float band = 0.72f + 0.28f * (float) Math.pow(Math.sin(v * Math.PI), 1.35);
                int yShift = (int) (fbm(v * 8f, 0.2f, null) * 6f);
                for (int x = 0; x < w; x++) {
                    int hx = (x + yShift + w) % w;
                    float brush = hair[hx] * 0.55f + hair[(hx + 1) % w] * 0.30f + hair[(hx + 2) % w] * 0.15f;
                    float spec = (float) Math.pow(Math.max(0f, Math.sin((v + 0.08f) * Math.PI)), 10.0) * 0.22f;
                    float t = 0.42f + brush * 0.22f + spec;
                    t *= band;
                    int r = clampByte(128 + t * 108);
                    int g = clampByte(92 + t * 92);
                    int b = clampByte(36 + t * 40);
                    if (rng.nextFloat() > 0.996f) {
                        r = clampByte(r * 0.78f);
                        g = clampByte(g * 0.90f);
                        b = clampByte(b * 0.74f);
                    }
                    img.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }
            return img;
        }

        static BufferedImage grain(int w, int h, long seed) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Random rng = new Random(seed);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int n = rng.nextInt(256);
                    int a = 28 + (n & 31);
                    img.setRGB(x, y, (a << 24) | (n << 16) | (n << 8) | n);
                }
            }
            return img;
        }

        static BufferedImage leather(int w, int h) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Random rng = new Random(0x4C454154);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    float u = x / (float) w;
                    float v = y / (float) h;
                    float p = fbm(u * 6.5f, v * 6.5f, rng);
                    float crease = (float) Math.sin((u + v) * 18.0) * 0.04f;
                    float t = 0.42f + p * 0.38f + crease;
                    int r = clampByte(28 + t * 46);
                    int g = clampByte(14 + t * 18);
                    int b = clampByte(12 + t * 12);
                    img.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }
            return img;
        }

        static BufferedImage dust(int w, int h) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Random rng = new Random(0x44555354);
            for (int i = 0; i < 900; i++) {
                int x = rng.nextInt(w);
                int y = rng.nextInt(h);
                int a = 20 + rng.nextInt(50);
                int c = 180 + rng.nextInt(70);
                img.setRGB(x, y, (a << 24) | (c << 16) | (c << 8) | (c - 20));
            }
            return img;
        }

        static float fbm(float x, float y, Random rng) {
            // Deterministic-enough value noise from hashed lattice.
            int x0 = (int) Math.floor(x);
            int y0 = (int) Math.floor(y);
            float fx = x - x0;
            float fy = y - y0;
            float v00 = hash(x0, y0);
            float v10 = hash(x0 + 1, y0);
            float v01 = hash(x0, y0 + 1);
            float v11 = hash(x0 + 1, y0 + 1);
            float ix0 = lerp(v00, v10, smooth(fx));
            float ix1 = lerp(v01, v11, smooth(fx));
            return lerp(ix0, ix1, smooth(fy));
        }

        static float hash(int x, int y) {
            int n = x * 374761393 + y * 668265263;
            n = (n ^ (n >> 13)) * 1274126177;
            n = n ^ (n >> 16);
            return (n & 0x7fffffff) / 2147483647f;
        }

        static float dist(float x, float y, float ax, float ay) {
            float dx = x - ax;
            float dy = y - ay;
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        static float lerp(float a, float b, float t) {
            return a + (b - a) * t;
        }

        static float smooth(float t) {
            return t * t * (3f - 2f * t);
        }

        static float clamp01(float v) {
            return v < 0f ? 0f : (v > 1f ? 1f : v);
        }

        static int clampByte(double v) {
            int i = (int) Math.round(v);
            if (i < 0) {
                return 0;
            }
            if (i > 255) {
                return 255;
            }
            return i;
        }
    }

    enum Screen {
        WELCOME,
        FEATURE,
        CALENDAR,
        COMPARE,
        CHANGE,
        FAREWELL
    }

    static final class Mote {
        float x;
        float y;
        float r;
        float a;
        float vx;
        float vy;
        float phase;
    }

    static final class Spark {
        float x;
        float y;
        float vx;
        float vy;
        float life;
        float hue;
    }

    static final class BrassButton extends JComponent {
        String kicker;
        String title;
        final Runnable onClick;
        boolean hover;
        boolean press;
        boolean armed;
        float glow;

        BrassButton(String kicker, String title, Runnable onClick) {
            this.kicker = kicker;
            this.title = title;
            this.onClick = onClick;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusable(true);
            setOpaque(false);
            MouseAdapter mouse = new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hover = false;
                    press = false;
                    repaint();
                }

                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && isEnabled()) {
                        press = true;
                        requestFocusInWindow();
                        repaint();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    boolean fire = press && contains(e.getPoint()) && isEnabled();
                    press = false;
                    repaint();
                    if (fire) {
                        onClick.run();
                    }
                }
            };
            addMouseListener(mouse);
            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                        press = true;
                        repaint();
                    }
                }

                public void keyReleased(KeyEvent e) {
                    if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) && press) {
                        press = false;
                        repaint();
                        if (isEnabled()) {
                            onClick.run();
                        }
                    }
                }
            });
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    armed = true;
                    repaint();
                }

                public void focusLost(FocusEvent e) {
                    armed = false;
                    press = false;
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();
            int lift = press ? 1 : 0;
            float live = isEnabled() ? 1f : 0.42f;
            g2.setComposite(AlphaComposite.SrcOver.derive(live));

            RoundRectangle2D body = new RoundRectangle2D.Float(1.5f, 2.5f + lift, w - 3f, h - 5f, 14, 14);
            g2.setColor(new Color(0, 0, 0, press ? 40 : 70));
            g2.fill(new RoundRectangle2D.Float(3, 6, w - 4, h - 6, 14, 14));

            Paint brass = new LinearGradientPaint(
                    0, lift, 0, h,
                    new float[] {0f, 0.18f, 0.55f, 1f},
                    new Color[] {
                        hover ? Ink.BRASS_LIT : new Color(214, 180, 96),
                        Ink.BRASS,
                        new Color(138, 98, 40),
                        hover ? new Color(176, 132, 58) : Ink.BRASS_DIM
                    });
            g2.setPaint(brass);
            g2.fill(body);

            g2.setPaint(new GradientPaint(0, lift, new Color(255, 236, 180, 90), 0, h * 0.45f, new Color(255, 220, 140, 0)));
            g2.fill(new RoundRectangle2D.Float(3, 3 + lift, w - 6, h * 0.42f, 12, 12));

            g2.setStroke(new BasicStroke(1.4f));
            g2.setColor(new Color(60, 38, 12, 180));
            g2.draw(body);
            g2.setColor(new Color(255, 228, 160, hover ? 140 : 80));
            g2.draw(new RoundRectangle2D.Float(3.2f, 4 + lift, w - 6.4f, h - 9f, 12, 12));

            if (armed) {
                g2.setStroke(new BasicStroke(1.1f));
                g2.setColor(new Color(244, 234, 210, 200));
                g2.draw(new RoundRectangle2D.Float(5, 6 + lift, w - 10, h - 13, 10, 10));
            }

            Font kickerFont = getFont().deriveFont(Font.PLAIN, 9.5f);
            Font titleFont = getFont().deriveFont(Font.PLAIN, Math.max(13f, h * 0.28f));
            g2.setFont(kickerFont);
            g2.setColor(new Color(48, 30, 12, 200));
            drawCentered(g2, kicker, w / 2f + 0.6f, 15 + lift + 0.6f);
            g2.setColor(new Color(255, 236, 196, 210));
            drawCentered(g2, kicker, w / 2f, 15 + lift);

            g2.setFont(titleFont);
            g2.setColor(new Color(32, 18, 8, 160));
            drawCentered(g2, title, w / 2f + 0.8f, h * 0.64f + lift + 0.8f);
            g2.setColor(new Color(28, 18, 8));
            drawCentered(g2, title, w / 2f, h * 0.64f + lift);
            g2.dispose();
        }

        public Dimension getPreferredSize() {
            return new Dimension(168, 52);
        }
    }

    static final class InkField extends JTextField {
        boolean focused;

        InkField() {
            super();
            setOpaque(false);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setForeground(Ink.INK);
            setCaretColor(Ink.VERMILION);
            setSelectionColor(new Color(168, 36, 28, 70));
            setSelectedTextColor(Ink.INK);
            setDisabledTextColor(new Color(90, 74, 52));
            setHorizontalAlignment(JTextField.LEFT);
            ((AbstractDocument) getDocument()).setDocumentFilter(new SignedIntFilter());
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    focused = true;
                    selectAll();
                    repaint();
                }

                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D well = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, 10, 10);
            g2.setColor(new Color(244, 234, 210, 210));
            g2.fill(well);
            g2.setPaint(new GradientPaint(0, 0, new Color(40, 28, 14, 35), 0, 8, new Color(40, 28, 14, 0)));
            g2.fill(well);
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(focused ? Ink.VERMILION : new Color(120, 92, 52, 160));
            g2.draw(well);
            if (focused) {
                g2.setColor(new Color(196, 154, 74, 90));
                g2.setStroke(new BasicStroke(3.2f));
                g2.draw(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 8, 8));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static final class SignedIntFilter extends DocumentFilter {
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = cur.substring(0, offset) + (text == null ? "" : text) + cur.substring(offset + length);
            if (ok(next)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        static boolean ok(String s) {
            if (s.length() == 0 || "-".equals(s)) {
                return true;
            }
            if (s.length() > 11) {
                return false;
            }
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (i == 0 && c == '-') {
                    continue;
                }
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            return true;
        }
    }

    static final class AtelierFrame extends JFrame {
        final Stage stage;

        AtelierFrame() {
            super("Aug14_2026  ·  Nocturne Atelier");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            stage = new Stage();
            setContentPane(stage);
            setMinimumSize(new Dimension(1100, 700));
            setSize(1380, 860);
            setLocationRelativeTo(null);
            setBackground(Ink.VOID);
            addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    stage.start();
                }
            });
        }
    }

    static final class Stage extends JPanel {
        final Type type = new Type();
        final Tex tex = new Tex();
        final List<Mote> motes = new ArrayList<Mote>();
        final List<Spark> sparks = new ArrayList<Spark>();
        final BrassButton yes;
        final BrassButton no;
        final BrassButton cal;
        final BrassButton find;
        final BrassButton act;
        final BrassButton close;
        final InkField field;
        final Timer ticker;

        Screen screen = Screen.WELCOME;
        String banner = "Welcome to the program.";
        String prompt = "Run the program?(Y/N)";
        String result = "";
        String toast = "";
        float toastLife = 0f;
        float time = 0f;
        float intro = 0f;
        float parchmentLift = 0f;
        float orrerySpin = 0f;
        float yearHand = 0f;
        float monthHand = 0f;
        float dayHand = 0f;
        float yearTarget = 0f;
        float monthTarget = 0f;
        float dayTarget = 0f;
        float beam = 0f;
        float beamTarget = 0f;
        float inkFill = 0f;
        float shake = 0f;
        float seal = 0f;
        int lastDays = 0;
        int lastX = 0;
        int lastSign = 0;
        boolean hasResult = false;
        boolean askingContinue = false;
        final Random rng = new Random(14);

        Stage() {
            setLayout(null);
            setBackground(Ink.VOID);
            setFocusable(true);
            yes = new BrassButton("AFFIRM  ·  Y", "Yes", new Runnable() {
                public void run() {
                    onYes();
                }
            });
            no = new BrassButton("DECLINE  ·  N", "No", new Runnable() {
                public void run() {
                    onNo();
                }
            });
            cal = new BrassButton("FEATURE  ·  C", "Calender", new Runnable() {
                public void run() {
                    enterCalendar();
                }
            });
            find = new BrassButton("FEATURE  ·  F", "Magnitude", new Runnable() {
                public void run() {
                    enterCompare();
                }
            });
            act = new BrassButton("COMPUTE", "Convert", new Runnable() {
                public void run() {
                    compute();
                }
            });
            close = new BrassButton("ATELIER", "Close", new Runnable() {
                public void run() {
                    SwingUtilities.getWindowAncestor(Stage.this).dispose();
                    System.exit(0);
                }
            });
            field = new InkField();
            field.setFont(type.numeral.deriveFont(22f));
            field.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    compute();
                }
            });
            yes.setFont(type.label);
            no.setFont(type.label);
            cal.setFont(type.label);
            find.setFont(type.label);
            act.setFont(type.label);
            close.setFont(type.label);
            add(yes);
            add(no);
            add(cal);
            add(find);
            add(act);
            add(close);
            add(field);
            bindKeys();
            addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (screen != Screen.FEATURE) {
                        return;
                    }
                    char c = Character.toUpperCase(e.getKeyChar());
                    if (c == 'C' || c == 'F') {
                        return;
                    }
                    if (Character.isLetter(c)) {
                        showToast("Invalid input!");
                        enterChange();
                    }
                }
            });
            seedMotes();
            ticker = new Timer(16, new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tick();
                }
            });
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    layoutChrome();
                }
            });
            applyScreen();
        }

        void start() {
            ticker.start();
            requestFocusInWindow();
        }

        void bindKeys() {
            javax.swing.InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            javax.swing.ActionMap am = getActionMap();
            im.put(KeyStroke.getKeyStroke("pressed Y"), "yes");
            im.put(KeyStroke.getKeyStroke("pressed N"), "no");
            im.put(KeyStroke.getKeyStroke("pressed C"), "cal");
            im.put(KeyStroke.getKeyStroke("pressed F"), "find");
            im.put(KeyStroke.getKeyStroke("pressed ESCAPE"), "esc");
            am.put("yes", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (yes.isVisible() && yes.isEnabled()) {
                        onYes();
                    }
                }
            });
            am.put("no", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (no.isVisible() && no.isEnabled()) {
                        onNo();
                    }
                }
            });
            am.put("cal", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (screen == Screen.FEATURE) {
                        enterCalendar();
                    }
                }
            });
            am.put("find", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (screen == Screen.FEATURE) {
                        enterCompare();
                    }
                }
            });
            am.put("esc", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (screen != Screen.FAREWELL) {
                        goFarewell();
                    } else {
                        SwingUtilities.getWindowAncestor(Stage.this).dispose();
                        System.exit(0);
                    }
                }
            });
        }

        void seedMotes() {
            for (int i = 0; i < 46; i++) {
                Mote m = new Mote();
                m.x = rng.nextFloat();
                m.y = rng.nextFloat();
                m.r = 0.6f + rng.nextFloat() * 1.8f;
                m.a = 0.08f + rng.nextFloat() * 0.22f;
                m.vx = (rng.nextFloat() - 0.5f) * 0.00025f;
                m.vy = -0.00012f - rng.nextFloat() * 0.00028f;
                m.phase = rng.nextFloat() * 6.28f;
                motes.add(m);
            }
        }

        void tick() {
            time += 0.016f;
            intro = Math.min(1f, intro + 0.018f);
            parchmentLift += (1f - parchmentLift) * 0.06f;
            orrerySpin += 0.0042f;
            yearHand += (yearTarget - yearHand) * 0.08f;
            monthHand += (monthTarget - monthHand) * 0.10f;
            dayHand += (dayTarget - dayHand) * 0.12f;
            beam += (beamTarget - beam) * 0.10f;
            float inkGoal = hasResult ? 1f : 0.15f;
            inkFill += (inkGoal - inkFill) * 0.08f;
            seal += ((screen == Screen.FAREWELL ? 1f : (hasResult ? 0.85f : 0.2f)) - seal) * 0.06f;
            if (shake > 0.01f) {
                shake *= 0.82f;
            } else {
                shake = 0f;
            }
            if (toastLife > 0f) {
                toastLife -= 0.012f;
            }
            for (int i = 0; i < motes.size(); i++) {
                Mote m = motes.get(i);
                m.x += m.vx + (float) Math.sin(time * 0.4 + m.phase) * 0.00008f;
                m.y += m.vy;
                if (m.y < -0.02f) {
                    m.y = 1.02f;
                    m.x = rng.nextFloat();
                }
                if (m.x < -0.02f) {
                    m.x = 1.02f;
                }
                if (m.x > 1.02f) {
                    m.x = -0.02f;
                }
            }
            for (int i = sparks.size() - 1; i >= 0; i--) {
                Spark s = sparks.get(i);
                s.x += s.vx;
                s.y += s.vy;
                s.vy += 0.08f;
                s.life -= 0.02f;
                if (s.life <= 0f) {
                    sparks.remove(i);
                }
            }
            yes.glow += ((yes.hover ? 1f : 0f) - yes.glow) * 0.15f;
            layoutChrome();
            repaint();
        }

        void showToast(String text) {
            toast = text;
            toastLife = 1f;
        }

        void onYes() {
            if (screen == Screen.WELCOME) {
                enterFeature();
            } else if (screen == Screen.CHANGE) {
                enterFeature();
            } else if ((screen == Screen.CALENDAR || screen == Screen.COMPARE) && askingContinue) {
                resetTool();
            }
        }

        void onNo() {
            if (screen == Screen.WELCOME) {
                goFarewell();
            } else if (screen == Screen.CHANGE) {
                goFarewell();
            } else if ((screen == Screen.CALENDAR || screen == Screen.COMPARE) && askingContinue) {
                enterChange();
            }
        }

        void enterFeature() {
            screen = Screen.FEATURE;
            hasResult = false;
            askingContinue = false;
            result = "";
            banner = "Please choose which feature you want to use:";
            prompt = "Enter C for calender converter.   Enter F to find if a number is greater or less than 0.";
            applyScreen();
        }

        void enterCalendar() {
            screen = Screen.CALENDAR;
            resetTool();
            banner = "Calender converter";
            prompt = "Please enter the days you want to convert: ";
            applyScreen();
            field.requestFocusInWindow();
        }

        void enterCompare() {
            screen = Screen.COMPARE;
            resetTool();
            banner = "Magnitude against zero";
            prompt = "Enter x: ";
            applyScreen();
            field.requestFocusInWindow();
        }

        void enterChange() {
            screen = Screen.CHANGE;
            askingContinue = false;
            banner = "Change program feature?(Y/N)";
            prompt = "Return to the cabinet, or close the atelier.";
            applyScreen();
        }

        void goFarewell() {
            screen = Screen.FAREWELL;
            hasResult = false;
            askingContinue = false;
            banner = "Program terminated.";
            prompt = "The lamp is lowered. The instruments sleep.";
            result = "";
            applyScreen();
        }

        void resetTool() {
            hasResult = false;
            askingContinue = false;
            result = "";
            field.setText("");
            beamTarget = 0f;
            if (screen == Screen.CALENDAR) {
                prompt = "Please enter the days you want to convert: ";
            } else if (screen == Screen.COMPARE) {
                prompt = "Enter x: ";
            }
            applyScreen();
            field.requestFocusInWindow();
        }

        void compute() {
            if (screen != Screen.CALENDAR && screen != Screen.COMPARE) {
                return;
            }
            String raw = field.getText().trim();
            if (raw.length() == 0 || "-".equals(raw)) {
                shake = 1f;
                showToast(screen == Screen.CALENDAR
                        ? "Please enter the days you want to convert: "
                        : "Enter x: ");
                return;
            }
            int value;
            try {
                value = Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                shake = 1f;
                showToast("Invalid input!");
                return;
            }
            if (screen == Screen.CALENDAR) {
                int[] parts = convertDays(value);
                lastDays = value;
                yearTarget = (float) (parts[0] % 12) * (float) (Math.PI * 2.0 / 12.0);
                monthTarget = parts[1] * (float) (Math.PI * 2.0 / 12.0);
                dayTarget = parts[2] * (float) (Math.PI * 2.0 / 30.0);
                result = value + " equals to " + parts[0] + " Years " + parts[1] + " Months " + parts[2] + " Days.";
                burst(0.24f, 0.54f, 26, 0);
            } else {
                lastX = value;
                lastSign = value > 0 ? 1 : (value < 0 ? -1 : 0);
                float mag = (float) Math.min(1.0, Math.abs(value) / 40.0);
                beamTarget = lastSign * mag * 0.38f;
                result = compareToZero(value);
                burst(0.24f, 0.58f, 22, lastSign);
            }
            hasResult = true;
            askingContinue = true;
            prompt = "Continue?(Y/N)";
            applyScreen();
        }

        void burst(float nx, float ny, int count, int sign) {
            int w = Math.max(1, getWidth());
            int h = Math.max(1, getHeight());
            for (int i = 0; i < count; i++) {
                Spark s = new Spark();
                s.x = nx * w + (rng.nextFloat() - 0.5f) * 30;
                s.y = ny * h + (rng.nextFloat() - 0.5f) * 18;
                s.vx = (rng.nextFloat() - 0.5f) * 4.2f;
                s.vy = -1.2f - rng.nextFloat() * 3.4f;
                s.life = 0.55f + rng.nextFloat() * 0.4f;
                s.hue = sign < 0 ? 1f : (sign > 0 ? 0f : 0.5f);
                sparks.add(s);
            }
        }

        void applyScreen() {
            boolean welcome = screen == Screen.WELCOME;
            boolean feature = screen == Screen.FEATURE;
            boolean tool = screen == Screen.CALENDAR || screen == Screen.COMPARE;
            boolean change = screen == Screen.CHANGE;
            boolean bye = screen == Screen.FAREWELL;
            yes.setVisible(welcome || change || (tool && askingContinue));
            no.setVisible(welcome || change || (tool && askingContinue));
            cal.setVisible(feature);
            find.setVisible(feature);
            act.setVisible(tool && !askingContinue);
            field.setVisible(tool && !askingContinue);
            close.setVisible(bye);
            if (screen == Screen.CALENDAR) {
                act.title = "Convert";
            } else if (screen == Screen.COMPARE) {
                act.title = "Weigh";
            }
            layoutChrome();
            revalidate();
            repaint();
            if (!field.isVisible()) {
                requestFocusInWindow();
            }
        }

        void layoutChrome() {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                return;
            }
            Rectangle paper = paperRect(w, h);
            int btnW = 168;
            int btnH = 52;
            int gap = 16;
            int baseY = paper.y + paper.height - 78;
            int mid = paper.x + paper.width / 2;
            yes.setBounds(mid - btnW - gap / 2, baseY, btnW, btnH);
            no.setBounds(mid + gap / 2, baseY, btnW, btnH);
            cal.setBounds(mid - btnW - gap / 2, baseY, btnW, btnH);
            find.setBounds(mid + gap / 2, baseY, btnW, btnH);
            close.setBounds(mid - btnW / 2, baseY, btnW, btnH);
            int fieldX = paper.x + 42;
            int fieldW = paper.width - 84;
            field.setBounds(fieldX, paper.y + 258, fieldW, 48);
            act.setBounds(mid - btnW / 2, paper.y + 322, btnW, btnH);
        }

        Rectangle paperRect(int w, int h) {
            int pw = (int) (w * 0.46);
            int ph = (int) (h * 0.78);
            int px = (int) (w * 0.50);
            int py = (int) (h * 0.12);
            return new Rectangle(px, py, pw, ph);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();
            paintAtmosphere(g2, w, h);
            paintDesk(g2, w, h);
            paintInstrument(g2, w, h);
            paintParchment(g2, w, h);
            paintSparks(g2);
            paintGrain(g2, w, h);
            paintVignette(g2, w, h);
            g2.dispose();
        }

        void paintAtmosphere(Graphics2D g, int w, int h) {
            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.28f, h * 0.08f),
                    Math.max(w, h) * 0.95f,
                    new float[] {0f, 0.45f, 1f},
                    new Color[] {new Color(38, 28, 20), Ink.NIGHT, Ink.VOID}));
            g.fillRect(0, 0, w, h);

            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.22f, h * 0.0f),
                    w * 0.55f,
                    new float[] {0f, 1f},
                    new Color[] {new Color(255, 186, 96, 70), new Color(255, 160, 60, 0)}));
            g.fillRect(0, 0, w, h);

            g.setComposite(AlphaComposite.SrcOver.derive(0.18f));
            g.drawImage(tex.leather, 0, 0, w, h, null);
            g.setComposite(AlphaComposite.SrcOver);

            for (int i = 0; i < motes.size(); i++) {
                Mote m = motes.get(i);
                float tw = 0.55f + 0.45f * (float) Math.sin(time * 1.4 + m.phase);
                g.setColor(new Color(255, 220, 170, (int) (m.a * tw * 255)));
                float px = m.x * w;
                float py = m.y * h;
                g.fill(new Ellipse2D.Float(px, py, m.r, m.r));
            }
        }

        void paintDesk(Graphics2D g, int w, int h) {
            int deskY = (int) (h * 0.58);
            Rectangle2D desk = new Rectangle2D.Float(0, deskY, w, h - deskY + 8);
            g.setPaint(new TexturePaint(tex.walnut, new Rectangle2D.Float(0, deskY, 640, 420)));
            g.fill(desk);
            g.setPaint(new GradientPaint(0, deskY, new Color(0, 0, 0, 70), 0, h, new Color(0, 0, 0, 140)));
            g.fill(desk);
            g.setPaint(new GradientPaint(0, deskY, new Color(255, 210, 140, 40), 0, deskY + 28, new Color(255, 210, 140, 0)));
            g.fillRect(0, deskY, w, 36);

            g.setColor(new Color(18, 10, 6, 200));
            g.fillRoundRect((int) (w * 0.035), (int) (h * 0.045), (int) (w * 0.93), (int) (h * 0.078), 18, 18);
            int bx = (int) (w * 0.04);
            int by = (int) (h * 0.05);
            int bw = (int) (w * 0.92);
            int bh = (int) (h * 0.066);
            g.setPaint(new LinearGradientPaint(
                    bx, by, bx, by + bh,
                    new float[] {0f, 0.22f, 0.55f, 1f},
                    new Color[] {
                        new Color(228, 196, 118),
                        new Color(186, 142, 64),
                        new Color(148, 104, 42),
                        new Color(118, 80, 30)
                    }));
            g.fillRoundRect(bx, by, bw, bh, 16, 16);
            g.setComposite(AlphaComposite.SrcOver.derive(0.22f));
            g.setPaint(new TexturePaint(tex.brass, new Rectangle2D.Float(0, 0, 900, 120)));
            g.fillRoundRect(bx, by, bw, bh, 16, 16);
            g.setComposite(AlphaComposite.SrcOver);
            g.setPaint(new GradientPaint(bx, by, new Color(255, 236, 186, 80), bx, by + bh * 0.45f, new Color(255, 220, 150, 0)));
            g.fillRoundRect(bx + 3, by + 2, bw - 6, (int) (bh * 0.45f), 12, 12);
            g.setStroke(new BasicStroke(1.2f));
            g.setColor(new Color(255, 226, 160, 90));
            g.drawRoundRect(bx, by, bw, bh, 16, 16);

            g.setFont(type.label.deriveFont(11f));
            g.setColor(new Color(36, 22, 8));
            String rule = "NOCTURNE ATELIER   ·   XIV AUGUST MMXXVI   ·   CALENDER  &  MAGNITUDE";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(rule, (w - fm.stringWidth(rule)) / 2, (int) (h * 0.05) + (int) (h * 0.042));
            paintLamp(g, w, h);
            paintInkwell(g, w, h);
        }

        void paintLamp(Graphics2D g, int w, int h) {
            float x = w * 0.078f;
            float desk = h * 0.58f;
            float y = desk - 6;
            Graphics2D lg = (Graphics2D) g.create();
            lg.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x, y - 92),
                    170,
                    new float[] {0f, 0.45f, 1f},
                    new Color[] {
                        new Color(255, 198, 112, 80),
                        new Color(255, 150, 70, 24),
                        new Color(255, 140, 50, 0)
                    }));
            lg.fill(new Ellipse2D.Float(x - 150, y - 180, 300, 260));
            lg.setColor(new Color(0, 0, 0, 70));
            lg.fill(new Ellipse2D.Float(x - 34, y + 2, 68, 16));
            lg.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x - 8, y - 4), 40,
                    new float[] {0f, 1f},
                    new Color[] {new Color(214, 174, 86), new Color(92, 60, 24)}));
            lg.fill(new Ellipse2D.Float(x - 30, y - 10, 60, 18));
            lg.setPaint(new LinearGradientPaint(
                    x - 8, y - 10, x + 10, y - 70,
                    new float[] {0f, 1f},
                    new Color[] {new Color(176, 130, 52), new Color(232, 196, 110)}));
            lg.fillRoundRect((int) x - 7, (int) y - 74, 14, 68, 6, 6);
            lg.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x - 6, y - 78), 26,
                    new float[] {0f, 1f},
                    new Color[] {new Color(220, 176, 88), new Color(110, 70, 28)}));
            lg.fill(new Ellipse2D.Float(x - 20, y - 86, 40, 16));
            lg.setPaint(new GradientPaint(x - 12, y - 150, new Color(210, 230, 230, 40), x + 12, y - 80, new Color(180, 210, 210, 70)));
            lg.fillRoundRect((int) x - 11, (int) y - 148, 22, 66, 11, 11);
            lg.setStroke(new BasicStroke(1.1f));
            lg.setColor(new Color(230, 240, 240, 90));
            lg.drawRoundRect((int) x - 11, (int) y - 148, 22, 66, 11, 11);
            float flicker = 0.85f + 0.15f * (float) Math.sin(time * 11.0);
            lg.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x, y - 118), 18,
                    new float[] {0f, 1f},
                    new Color[] {
                        new Color(255, 244, 200, (int) (220 * flicker)),
                        new Color(255, 140, 40, 0)
                    }));
            lg.fill(new Ellipse2D.Float(x - 14, y - 132, 28, 28));
            Path2D flame = new Path2D.Float();
            flame.moveTo(x, y - 132);
            flame.curveTo(x + 7, y - 118, x + 4, y - 108, x, y - 104);
            flame.curveTo(x - 4, y - 108, x - 7, y - 118, x, y - 132);
            lg.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x, y - 116), 12,
                    new float[] {0f, 1f},
                    new Color[] {new Color(255, 248, 220), new Color(232, 92, 24, 180)}));
            lg.fill(flame);
            lg.dispose();
        }

        void paintInkwell(Graphics2D g, int w, int h) {
            float x = w * 0.392f;
            float y = h * 0.675f;
            Graphics2D ig = (Graphics2D) g.create();
            ig.setColor(new Color(0, 0, 0, 80));
            ig.fill(new Ellipse2D.Float(x - 28, y + 26, 78, 20));
            ig.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x + 4, y + 4), 44,
                    new float[] {0f, 1f},
                    new Color[] {new Color(196, 154, 74), new Color(72, 48, 18)}));
            ig.fill(new Ellipse2D.Float(x - 24, y, 66, 36));
            ig.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x + 8, y + 10), 22,
                    new float[] {0f, 1f},
                    new Color[] {new Color(16, 14, 20), new Color(4, 4, 6)}));
            ig.fill(new Ellipse2D.Float(x - 10, y + 6, 38, 18));
            ig.setColor(new Color(48, 18, 16, 200));
            ig.fill(new Ellipse2D.Float(x - 6, y + 10, 30, 10));
            ig.setPaint(new LinearGradientPaint(
                    x + 18, y - 46, x + 40, y + 8,
                    new float[] {0f, 0.7f, 1f},
                    new Color[] {new Color(214, 196, 150), new Color(122, 86, 42), new Color(36, 22, 12)}));
            ig.setStroke(new BasicStroke(3.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D quill = new Path2D.Float();
            quill.moveTo(x + 46, y - 44);
            quill.curveTo(x + 38, y - 18, x + 26, y + 2, x + 14, y + 14);
            ig.draw(quill);
            ig.setColor(new Color(236, 224, 196, 160));
            ig.setStroke(new BasicStroke(1.1f));
            ig.draw(new Line2D.Float(x + 42, y - 40, x + 50, y - 36));
            ig.draw(new Line2D.Float(x + 38, y - 32, x + 47, y - 28));
            ig.dispose();
        }

        void paintInstrument(Graphics2D g, int w, int h) {
            float cx = w * 0.24f;
            float cy = h * 0.54f;
            float radius = Math.min(w, h) * 0.195f;
            Graphics2D ig = (Graphics2D) g.create();
            ig.translate(cx, cy + Math.sin(time * 0.7) * 1.4);

            ig.setColor(new Color(0, 0, 0, 90));
            ig.fill(new Ellipse2D.Float(-radius * 1.05f, radius * 0.72f, radius * 2.1f, radius * 0.38f));
            if (screen == Screen.COMPARE) {
                paintBalance(ig, radius);
            } else {
                paintOrrery(ig, radius);
            }
            ig.dispose();
        }

        void paintOrrery(Graphics2D g, float radius) {
            Ellipse2D plate = new Ellipse2D.Float(-radius, -radius, radius * 2, radius * 2);
            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(-radius * 0.25f, -radius * 0.3f),
                    radius * 1.35f,
                    new float[] {0f, 0.55f, 1f},
                    new Color[] {new Color(232, 198, 112), new Color(156, 112, 42), new Color(72, 48, 18)}));
            g.fill(plate);
            g.setComposite(AlphaComposite.SrcOver.derive(0.16f));
            g.setPaint(new TexturePaint(tex.brass, new Rectangle2D.Float(-radius, -radius, radius * 2.6f, radius * 1.4f)));
            g.fill(plate);
            g.setComposite(AlphaComposite.SrcOver);

            g.setStroke(new BasicStroke(radius * 0.045f));
            g.setColor(new Color(48, 30, 10, 200));
            g.draw(new Ellipse2D.Float(-radius * 0.98f, -radius * 0.98f, radius * 1.96f, radius * 1.96f));
            g.setStroke(new BasicStroke(1.3f));
            g.setColor(new Color(255, 226, 160, 120));
            g.draw(new Ellipse2D.Float(-radius * 0.93f, -radius * 0.93f, radius * 1.86f, radius * 1.86f));

            drawTicks(g, radius * 0.90f, 60, 8, 2.0f, new Color(40, 24, 8, 200));
            drawTicks(g, radius * 0.72f, 12, 11, 2.4f, new Color(90, 36, 18, 210));
            drawTicks(g, radius * 0.52f, 30, 6, 1.6f, new Color(24, 48, 44, 180));
            String[] romans = {"XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI"};
            g.setFont(type.micro.deriveFont(Math.max(8f, radius * 0.075f)));
            g.setColor(new Color(36, 22, 10, 210));
            for (int i = 0; i < 12; i++) {
                double a = (Math.PI * 2.0 * i) / 12.0 - Math.PI / 2.0;
                float tx = (float) Math.cos(a) * radius * 0.80f;
                float ty = (float) Math.sin(a) * radius * 0.80f + 3f;
                drawCentered(g, romans[i], tx, ty);
            }

            g.setColor(new Color(18, 12, 8, 230));
            g.fill(new Ellipse2D.Float(-radius * 0.40f, -radius * 0.40f, radius * 0.80f, radius * 0.80f));
            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(-radius * 0.08f, -radius * 0.10f),
                    radius * 0.42f,
                    new float[] {0f, 1f},
                    new Color[] {new Color(62, 44, 26), new Color(18, 12, 8)}));
            g.fill(new Ellipse2D.Float(-radius * 0.36f, -radius * 0.36f, radius * 0.72f, radius * 0.72f));

            drawHand(g, dayHand + orrerySpin * 2.1f, radius * 0.86f, 2.0f, Ink.VERDIGRIS_LIT);
            drawHand(g, monthHand + orrerySpin * 0.7f, radius * 0.68f, 2.6f, Ink.VERMILION_LIT);
            drawHand(g, yearHand + orrerySpin * 0.18f, radius * 0.48f, 3.2f, Ink.BRASS_LIT);

            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(0, 0),
                    radius * 0.12f,
                    new float[] {0f, 1f},
                    new Color[] {new Color(255, 232, 186), new Color(140, 42, 28)}));
            g.fill(new Ellipse2D.Float(-radius * 0.07f, -radius * 0.07f, radius * 0.14f, radius * 0.14f));
            g.setColor(new Color(255, 236, 190, 160));
            g.setStroke(new BasicStroke(1.1f));
            g.draw(new Ellipse2D.Float(-radius * 0.07f, -radius * 0.07f, radius * 0.14f, radius * 0.14f));

            g.setFont(type.micro);
            g.setColor(new Color(236, 214, 160, 210));
            drawCentered(g, hasResult && screen == Screen.CALENDAR ? lastDays + " DAYS" : "ORRERY", 0, radius * 0.18f);
        }

        void paintBalance(Graphics2D g, float radius) {
            g.rotate(beam);
            float arm = radius * 1.05f;
            g.setPaint(new LinearGradientPaint(
                    -arm, 0, arm, 0,
                    new float[] {0f, 0.5f, 1f},
                    new Color[] {new Color(168, 124, 52), new Color(232, 200, 118), new Color(138, 96, 40)}));
            g.setStroke(new BasicStroke(radius * 0.07f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Float(-arm, 0, arm, 0));
            g.setColor(new Color(40, 24, 10));
            g.setStroke(new BasicStroke(1.4f));
            g.draw(new Line2D.Float(-arm, -radius * 0.03f, arm, -radius * 0.03f));

            paintPan(g, -arm, radius * 0.42f, radius * 0.28f, lastSign < 0, Ink.VERDIGRIS, Ink.VERDIGRIS_LIT);
            paintPan(g, arm, radius * 0.42f, radius * 0.28f, lastSign > 0, Ink.VERMILION, Ink.VERMILION_LIT);
            g.rotate(-beam);

            g.setPaint(new LinearGradientPaint(
                    0, -radius * 0.85f, 0, radius * 0.7f,
                    new float[] {0f, 1f},
                    new Color[] {new Color(214, 178, 88), new Color(72, 46, 18)}));
            GeneralPath pillar = new GeneralPath();
            pillar.moveTo(-radius * 0.08f, radius * 0.72f);
            pillar.lineTo(-radius * 0.05f, -radius * 0.18f);
            pillar.lineTo(radius * 0.05f, -radius * 0.18f);
            pillar.lineTo(radius * 0.08f, radius * 0.72f);
            pillar.closePath();
            g.fill(pillar);
            g.setColor(new Color(36, 22, 10));
            g.fill(new Ellipse2D.Float(-radius * 0.28f, radius * 0.66f, radius * 0.56f, radius * 0.16f));
            g.setPaint(new RadialGradientPaint(new Point2D.Float(0, -radius * 0.22f), radius * 0.12f,
                    new float[] {0f, 1f}, new Color[] {new Color(255, 230, 170), new Color(120, 40, 24)}));
            g.fill(new Ellipse2D.Float(-radius * 0.075f, -radius * 0.30f, radius * 0.15f, radius * 0.15f));

            g.setFont(type.micro);
            g.setColor(new Color(236, 214, 160, 210));
            String tag = hasResult ? Integer.toString(lastX) : "ZERO";
            drawCentered(g, tag, 0, radius * 0.92f);
        }

        void paintPan(Graphics2D g, float x, float drop, float size, boolean heavy, Color ink, Color inkLit) {
            float y = drop + (heavy ? size * 0.18f * inkFill : 0f);
            g.setStroke(new BasicStroke(1.2f));
            g.setColor(new Color(214, 186, 110, 200));
            g.draw(new Line2D.Float(x, 0, x - size * 0.55f, y - size * 0.15f));
            g.draw(new Line2D.Float(x, 0, x + size * 0.55f, y - size * 0.15f));
            g.draw(new Line2D.Float(x, 0, x, y - size * 0.15f));
            Ellipse2D bowl = new Ellipse2D.Float(x - size, y - size * 0.22f, size * 2, size * 0.55f);
            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x, y), size,
                    new float[] {0f, 1f},
                    new Color[] {new Color(210, 176, 92), new Color(92, 62, 24)}));
            g.fill(bowl);
            float fill = heavy ? 0.35f + 0.55f * inkFill : 0.16f;
            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(x, y), size * 0.8f,
                    new float[] {0f, 1f},
                    new Color[] {
                        new Color(inkLit.getRed(), inkLit.getGreen(), inkLit.getBlue(), (int) (220 * fill)),
                        new Color(ink.getRed(), ink.getGreen(), ink.getBlue(), (int) (160 * fill))
                    }));
            g.fill(new Ellipse2D.Float(x - size * 0.72f, y - size * 0.08f, size * 1.44f, size * 0.28f));
        }

        void drawTicks(Graphics2D g, float radius, int count, float length, float weight, Color color) {
            g.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(color);
            for (int i = 0; i < count; i++) {
                double a = (Math.PI * 2.0 * i) / count - Math.PI / 2.0;
                float c = (float) Math.cos(a);
                float s = (float) Math.sin(a);
                float inner = radius - length;
                g.draw(new Line2D.Float(c * inner, s * inner, c * radius, s * radius));
            }
        }

        void drawHand(Graphics2D g, float angle, float length, float weight, Color color) {
            AffineTransform old = g.getTransform();
            g.rotate(angle - Math.PI / 2.0);
            g.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(0, 0, 0, 80));
            g.draw(new Line2D.Float(1.2f, 1.2f, length, 1.2f));
            g.setColor(color);
            g.draw(new Line2D.Float(0, 0, length, 0));
            g.fill(new Ellipse2D.Float(length - 3.5f, -3.5f, 7, 7));
            g.setTransform(old);
        }

        void paintParchment(Graphics2D g, int w, int h) {
            Rectangle r = paperRect(w, h);
            float lift = (1f - parchmentLift) * 18f;
            float jx = (float) Math.sin(time * 37) * shake * 6f;
            Graphics2D p = (Graphics2D) g.create();
            p.translate(r.x + jx, r.y + lift);
            int pw = r.width;
            int ph = r.height;

            p.setColor(new Color(0, 0, 0, 80));
            p.fillRoundRect(10, 16, pw, ph, 18, 18);

            Shape sheet = deckle(0, 0, pw, ph, 18);
            p.setPaint(new TexturePaint(tex.paper, new Rectangle2D.Float(0, 0, pw, ph)));
            p.fill(sheet);
            p.setPaint(new GradientPaint(0, 0, new Color(255, 248, 230, 40), pw, ph, new Color(80, 50, 20, 22)));
            p.fill(sheet);
            p.setStroke(new BasicStroke(1.6f));
            p.setColor(new Color(120, 86, 42, 140));
            p.draw(sheet);
            p.setStroke(new BasicStroke(0.8f));
            p.setColor(new Color(255, 236, 196, 80));
            p.draw(deckle(6, 6, pw - 12, ph - 12, 14));

            p.setPaint(new TexturePaint(tex.brass, new Rectangle2D.Float(0, 0, 220, 40)));
            p.fillRoundRect(28, 22, pw - 56, 8, 8, 8);
            p.setColor(new Color(40, 24, 10, 160));
            p.drawRoundRect(28, 22, pw - 56, 8, 8, 8);

            paintWax(p, pw - 58, 58, 28);

            p.setColor(Ink.INK_SOFT);
            p.setFont(type.label.deriveFont(10.5f));
            p.drawString("INSTRUMENT DOSSIER   ·   AUG14_2026", 40, 54);

            float titleSize = (screen == Screen.FEATURE || screen == Screen.COMPARE) ? 32f : 40f;
            if (screen == Screen.FAREWELL) {
                titleSize = 44f;
            }
            p.setColor(Ink.INK);
            String title = heading();
            drawWrapped(p, title, 40, 108, pw - 80, type.display.deriveFont(titleSize), 1.08f, 3);

            p.setFont(type.body.deriveFont(16.5f));
            p.setColor(Ink.INK_SOFT);
            drawWrapped(p, prompt, 40, 200, pw - 80, type.body.deriveFont(16.5f), 1.35f, 3);

            if (hasResult && result.length() > 0) {
                RoundRectangle2D card = new RoundRectangle2D.Float(36, 250, pw - 72, 150, 16, 16);
                p.setColor(new Color(40, 24, 12, 20));
                p.fill(card);
                p.setStroke(new BasicStroke(1.1f));
                p.setColor(new Color(120, 82, 40, 90));
                p.draw(card);
                p.setFont(type.numeral.deriveFont(22f));
                p.setColor(Ink.VERMILION);
                drawWrapped(p, result, 52, 286, pw - 104, type.numeral.deriveFont(22f), 1.25f, 4);
                if (screen == Screen.CALENDAR) {
                    int[] parts = convertDays(lastDays);
                    paintMiniMeters(p, 52, 360, pw - 104, parts);
                } else if (screen == Screen.COMPARE) {
                    paintSignRibbon(p, 52, 368, pw - 104);
                }
            }

            if (toastLife > 0f && toast.length() > 0) {
                p.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, toastLife * 1.4f)));
                p.setFont(type.body.deriveFont(14f));
                p.setColor(Ink.VERMILION);
                drawWrapped(p, toast, 40, ph - 118, pw - 80, type.body.deriveFont(14f), 1.3f, 2);
                p.setComposite(AlphaComposite.SrcOver);
            }

            p.setFont(type.micro);
            p.setColor(new Color(90, 70, 42, 180));
            p.drawString(footer(), 40, ph - 22);
            p.dispose();
        }

        void paintMiniMeters(Graphics2D g, int x, int y, int w, int[] parts) {
            String[] labels = {"YEARS", "MONTHS", "DAYS"};
            int[] max = {Math.max(1, parts[0] + 1), 12, 30};
            int gap = 10;
            int bw = (w - gap * 2) / 3;
            for (int i = 0; i < 3; i++) {
                int bx = x + i * (bw + gap);
                g.setColor(new Color(40, 24, 12, 30));
                g.fillRoundRect(bx, y, bw, 18, 9, 9);
                float t = Math.min(1f, parts[i] / (float) max[i]);
                g.setPaint(new GradientPaint(bx, y, Ink.BRASS_LIT, bx + bw, y, Ink.VERMILION));
                g.fillRoundRect(bx, y, Math.max(8, (int) (bw * t)), 18, 9, 9);
                g.setFont(type.micro);
                g.setColor(Ink.INK);
                g.drawString(labels[i] + "  " + parts[i], bx + 8, y + 13);
            }
        }

        void paintSignRibbon(Graphics2D g, int x, int y, int w) {
            Color a = lastSign < 0 ? Ink.VERDIGRIS : (lastSign > 0 ? Ink.VERMILION : Ink.GRAPHITE);
            Color b = lastSign < 0 ? Ink.VERDIGRIS_LIT : (lastSign > 0 ? Ink.VERMILION_LIT : new Color(160, 164, 172));
            g.setPaint(new GradientPaint(x, y, a, x + w, y, b));
            g.fillRoundRect(x, y, w, 22, 11, 11);
            g.setFont(type.label.deriveFont(11f));
            g.setColor(Ink.CREAM);
            String lab = lastSign < 0 ? "LESS THAN ZERO" : (lastSign > 0 ? "MORE THAN ZERO" : "EQUAL TO ZERO");
            FontMetrics fm = g.getFontMetrics();
            g.drawString(lab, x + (w - fm.stringWidth(lab)) / 2, y + 15);
        }

        void paintWax(Graphics2D g, float x, float y, float r) {
            Graphics2D w = (Graphics2D) g.create();
            w.translate(x, y);
            w.rotate(-0.12 + Math.sin(time * 0.3) * 0.01);
            float s = 0.75f + 0.25f * seal;
            w.scale(s, s);
            Ellipse2D disc = new Ellipse2D.Float(-r, -r, r * 2, r * 2);
            w.setColor(new Color(0, 0, 0, 50));
            w.fill(new Ellipse2D.Float(-r + 3, -r + 5, r * 2, r * 2));
            w.setPaint(new RadialGradientPaint(
                    new Point2D.Float(-r * 0.2f, -r * 0.25f),
                    r * 1.3f,
                    new float[] {0f, 0.6f, 1f},
                    new Color[] {Ink.WAX_LIT, Ink.WAX, new Color(72, 10, 12)}));
            w.fill(disc);
            w.setStroke(new BasicStroke(1.3f));
            w.setColor(new Color(255, 180, 140, 80));
            w.draw(new Ellipse2D.Float(-r * 0.72f, -r * 0.72f, r * 1.44f, r * 1.44f));
            w.setFont(type.label.deriveFont(9f));
            w.setColor(new Color(255, 220, 190, 210));
            drawCentered(w, "XIV", 0, 3);
            w.dispose();
        }

        void paintSparks(Graphics2D g) {
            for (int i = 0; i < sparks.size(); i++) {
                Spark s = sparks.get(i);
                Color c = s.hue < 0.25f ? Ink.VERMILION_LIT : (s.hue > 0.75f ? Ink.VERDIGRIS_LIT : Ink.BRASS_LIT);
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Tex.clampByte(s.life * 220)));
                g.fill(new Ellipse2D.Float(s.x, s.y, 3.2f, 3.2f));
            }
        }

        void paintGrain(Graphics2D g, int w, int h) {
            g.setComposite(AlphaComposite.SrcOver.derive(0.16f));
            g.setPaint(new TexturePaint(tex.grain, new Rectangle2D.Float(0, 0, 256, 256)));
            g.fillRect(0, 0, w, h);
            g.setComposite(AlphaComposite.SrcOver.derive(0.10f));
            g.drawImage(tex.dust, 0, 0, w, h, null);
            g.setComposite(AlphaComposite.SrcOver);
        }

        void paintVignette(Graphics2D g, int w, int h) {
            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.5f, h * 0.45f),
                    Math.max(w, h) * 0.72f,
                    new float[] {0.55f, 1f},
                    new Color[] {new Color(0, 0, 0, 0), new Color(0, 0, 0, 150)}));
            g.fillRect(0, 0, w, h);
            g.setComposite(AlphaComposite.SrcOver.derive(intro));
            // intro is already applied as the scene builds; keep overlay fade from black
            g.setComposite(AlphaComposite.SrcOver.derive(1f - intro));
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);
            g.setComposite(AlphaComposite.SrcOver);
        }

        String heading() {
            switch (screen) {
                case WELCOME:
                    return "Welcome to the program.";
                case FEATURE:
                    return "Please choose which feature you want to use:";
                case CALENDAR:
                    return "Calender converter.";
                case COMPARE:
                    return "Find if a number is greater or less than zero.";
                case CHANGE:
                    return "Change program feature?(Y/N)";
                case FAREWELL:
                    return "Program terminated.";
                default:
                    return banner;
            }
        }

        String footer() {
            switch (screen) {
                case WELCOME:
                    return "Y run the program   ·   N decline";
                case FEATURE:
                    return "C calender converter   ·   F find if a number is greater or less than 0";
                case CALENDAR:
                    return askingContinue ? "Continue?(Y/N)" : "Enter days, then Convert or Return";
                case COMPARE:
                    return askingContinue ? "Continue?(Y/N)" : "Enter x, then Weigh or Return";
                case CHANGE:
                    return "Change program feature?(Y/N)";
                case FAREWELL:
                    return "Program terminated.";
                default:
                    return "";
            }
        }

        static Shape deckle(float x, float y, float w, float h, float r) {
            Path2D p = new Path2D.Float();
            Random jitter = new Random(20260814);
            int steps = 48;
            p.moveTo(x + r, y);
            for (int i = 0; i <= steps; i++) {
                float t = i / (float) steps;
                p.lineTo(x + r + (w - 2 * r) * t, y + (jitter.nextFloat() - 0.5f) * 2.4f);
            }
            for (int i = 0; i <= steps / 3; i++) {
                float t = i / (float) (steps / 3);
                p.lineTo(x + w + (jitter.nextFloat() - 0.5f) * 2.2f, y + r + (h - 2 * r) * t);
            }
            for (int i = 0; i <= steps; i++) {
                float t = i / (float) steps;
                p.lineTo(x + w - r - (w - 2 * r) * t, y + h + (jitter.nextFloat() - 0.5f) * 2.4f);
            }
            for (int i = 0; i <= steps / 3; i++) {
                float t = i / (float) (steps / 3);
                p.lineTo(x + (jitter.nextFloat() - 0.5f) * 2.2f, y + h - r - (h - 2 * r) * t);
            }
            p.closePath();
            return p;
        }
    }

    static void quality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    }

    static void drawCentered(Graphics2D g, String text, float x, float y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x - fm.stringWidth(text) / 2f, y);
    }

    static void drawWrapped(Graphics2D g, String text, float x, float y, float width, Font font, float leading, int maxLines) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        String line = "";
        int used = 0;
        float cy = y;
        for (int i = 0; i < words.length; i++) {
            String trial = line.length() == 0 ? words[i] : line + " " + words[i];
            if (fm.stringWidth(trial) > width && line.length() > 0) {
                g.drawString(line, x, cy);
                cy += fm.getHeight() * leading;
                line = words[i];
                used++;
                if (used >= maxLines - 1) {
                    String rest = words[i];
                    for (int j = i + 1; j < words.length; j++) {
                        rest += " " + words[j];
                    }
                    String clipped = rest;
                    while (fm.stringWidth(clipped + "…") > width && clipped.length() > 1) {
                        clipped = clipped.substring(0, clipped.length() - 1);
                    }
                    g.drawString(fm.stringWidth(rest) > width ? clipped + "…" : rest, x, cy);
                    return;
                }
            } else {
                line = trial;
            }
        }
        if (line.length() > 0) {
            g.drawString(line, x, cy);
        }
    }
}
