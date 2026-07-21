import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VideoPlayer extends JFrame {
    private MediaPlayer mediaPlayer;
    private Runnable onVideoEnd;

    public VideoPlayer(String videoPath, Runnable onEnd) {
        this.onVideoEnd = onEnd;
        setTitle("欢迎视频");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
                final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        // 在 JavaFX 线程中初始化
        Platform.runLater(() -> initFX(fxPanel, videoPath));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopAndDispose();
                if (onVideoEnd != null) onVideoEnd.run();
            }
        });
    }

    private void initFX(JFXPanel fxPanel, String videoPath) {
        try {
            File file = new File(videoPath);
            System.out.println("视频路径: " + file.getAbsolutePath());
            if (!file.exists()) {
                System.err.println("文件不存在，跳过视频");
                closeAndCallback();
                return;
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0.8); // 音量 > 0

            // 监听状态
            mediaPlayer.statusProperty().addListener((obs, old, newStatus) -> {
                System.out.println("播放状态: " + newStatus);
            });
            mediaPlayer.setOnReady(() -> {
                System.out.println("媒体已就绪，开始播放");
                mediaPlayer.play();
            });
            mediaPlayer.setOnPlaying(() -> System.out.println("正在播放..."));
            mediaPlayer.setOnError(() -> {
                System.err.println("播放错误: " + mediaPlayer.getError());
                JOptionPane.showMessageDialog(this, "视频播放出错，跳过", "提示", JOptionPane.WARNING_MESSAGE);
                closeAndCallback();
            });
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("播放结束");
                Platform.runLater(() -> {
                    stopAndDispose();
                    closeAndCallback();
                });
            });
            MediaView mediaView = new MediaView(mediaPlayer);
            // 铺满窗口
            mediaView.setPreserveRatio(false);
            mediaView.setFitWidth(fxPanel.getWidth());
            mediaView.setFitHeight(fxPanel.getHeight());
            // 添加监听器，当面板大小变化时更新视频尺寸
            fxPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    mediaView.setFitWidth(fxPanel.getWidth());
                    mediaView.setFitHeight(fxPanel.getHeight());
                }
            });
            StackPane root = new StackPane(mediaView);
            fxPanel.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            closeAndCallback();
        }
    }
    private void stopAndDispose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        Component[] comps = getContentPane().getComponents();
        for (Component comp : comps) {
            if (comp instanceof JFXPanel) {
                ((JFXPanel) comp).setScene(null);
                getContentPane().remove(comp);
            }
        }
        dispose();
    }
    private void closeAndCallback() {
        if (onVideoEnd != null) onVideoEnd.run();
    }}