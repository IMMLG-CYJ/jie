import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage image;

    public ImagePanel() {
        setBackground(Color.LIGHT_GRAY);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();


            double scale = Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);


            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;
            Graphics2D g2d = (Graphics2D) g;// Graphics2D有正常的缩放比，保证图片完整显示且不变形
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(image, x, y, scaledWidth, scaledHeight, this); // 绘制缩放后的图片，用Graphics2D可让图片更容易缩放
        } else {
            g.setColor(Color.GRAY);
            g.drawString("暂无图片", getWidth() / 2 - 30, getHeight() / 2);
        }
    }
}