import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;

public class ImageBrowserFrame extends JFrame {
    private List<ImageData> imageList;
    private int currentIndex = 0;

    private ImagePanel imagePanel;
    private JTextArea descriptionArea;
    private JButton prevButton, nextButton;
    private JList<String> thumbnailList;
    private DefaultListModel<String> listModel;
    private JScrollPane listScroll;

    private Runnable onCloseListener;

    public void setOnCloseListener(Runnable listener) {  // 新增
        this.onCloseListener = listener;
    }


    public ImageBrowserFrame() {
        setTitle("图片浏览器");
        setDefaultCloseOperation(HIDE_ON_CLOSE);//窗口关闭后程序不结束
        setSize(1600, 1000);
        setLocationRelativeTo(null);

        imageList = JiaZhai.loadtp();

        if (imageList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可显示的图片！", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        updateDisplay();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                YinYuePlay.stopMusic();
                dispose();

                if (onCloseListener != null) {
                    onCloseListener.run();
                }
            }
        });
    }

    private void initComponents() {
        listModel = new DefaultListModel<>();
        for (ImageData data : imageList) {
            String item = data.getDescription().isEmpty() ?
                    "图片 " + (listModel.size() + 1) :
                    data.getDescription().substring(0, Math.min(8, data.getDescription().length())) + "…";
            listModel.addElement(item);
        }
        thumbnailList = new JList<>(listModel);
        thumbnailList.setFont(new Font("微软雅黑", Font.PLAIN, 34));
        thumbnailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        thumbnailList.setSelectedIndex(0);
        thumbnailList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selected = thumbnailList.getSelectedIndex();
                if (selected != -1 && selected != currentIndex) {
                    currentIndex = selected;
                    updateDisplay();
                }
            }
        });
        listScroll = new JScrollPane(thumbnailList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        listScroll.setOpaque(false);
        listScroll.getViewport().setOpaque(false);

        imagePanel = new ImagePanel();
        imagePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                imagePanel.repaint();
            }
        });

        descriptionArea = new JTextArea(3, 40);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 34));
        descriptionArea.setOpaque(false);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);

        prevButton = new JButton("上一张");
        prevButton.setFont(new Font("微软雅黑", Font.PLAIN, 34));
        nextButton = new JButton("下一张");
        nextButton.setFont(new Font("微软雅黑", Font.PLAIN, 34));
        prevButton.addActionListener(e -> {
            if (currentIndex > 0) {
                currentIndex--;
                thumbnailList.setSelectedIndex(currentIndex);
                updateDisplay();
            }
        });
        nextButton.addActionListener(e -> {
            if (currentIndex < imageList.size() - 1) {
                currentIndex++;
                thumbnailList.setSelectedIndex(currentIndex);
                updateDisplay();
            }
        });

        // 音量控制
        JPanel volumePanel = createVolumePanel();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setOpaque(false);


        JLabel emojiLabel = new JLabel();
        try {
            File emojiFile = new File("emoji.jpg");
            if (emojiFile.exists()) {
                BufferedImage emojiImg = ImageIO.read(emojiFile);
                Image scaled = emojiImg.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                emojiLabel.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) {
            System.out.println(" ");
        }
        buttonPanel.add(emojiLabel);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        // 底部总面板（透明）
        JPanel southPanel = new JPanel(new BorderLayout(10, 5));
        southPanel.setOpaque(false);
        southPanel.add(volumePanel, BorderLayout.WEST);
        southPanel.add(descScroll, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.EAST);

        // 组装
        setLayout(new BorderLayout());
        add(listScroll, BorderLayout.WEST);
        add(imagePanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    // 音量滑块（透明）
    private JPanel createVolumePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        JLabel volumeIcon = new JLabel("🔊");
        volumeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        JSlider volumeSlider = new JSlider(0, 100, (int)(YinYuePlay.getVolume() * 100));
        volumeSlider.setPreferredSize(new Dimension(120, 40));
        JLabel volumeLabel = new JLabel(String.format("%d%%", volumeSlider.getValue()));
        volumeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        volumeLabel.setPreferredSize(new Dimension(45, 30));

        volumeSlider.addChangeListener(e -> {
            int val = volumeSlider.getValue();
            YinYuePlay.setVolume(val / 100f);
            volumeLabel.setText(String.format("%d%%", val));
        });

        panel.add(volumeIcon);
        panel.add(volumeSlider);
        panel.add(volumeLabel);
        return panel;
    }

    // 刷新显示与音乐
    private void updateDisplay() {
        if (imageList.isEmpty()) return;
        ImageData data = imageList.get(currentIndex);
        imagePanel.setImage(data.getImage());
        descriptionArea.setText(data.getDescription().isEmpty() ? "（无简介）" : data.getDescription());

        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < imageList.size() - 1);

        if (thumbnailList.getSelectedIndex() != currentIndex) {
            thumbnailList.setSelectedIndex(currentIndex);
        }

        String musicPath = data.getMusicPath();
        if (musicPath != null && new java.io.File(musicPath).exists()) {
            YinYuePlay.playMusic(musicPath, true);
        } else {
            YinYuePlay.stopMusic();
        }
    }
}