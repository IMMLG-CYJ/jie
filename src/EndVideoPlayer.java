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

public class EndVideoPlayer extends JFrame {
    private MediaPlayer mediaPlayer;
    private Runnable onVideoEnd;
    private Timer forceExitTimer;
    private boolean isEnded = false;

    public EndVideoPlayer(String videoPath, Runnable onEnd) {
        this.onVideoEnd = onEnd;
        setTitle("结束视频");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 700);
        setLocationRelativeTo(null);

        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> initFX(fxPanel, videoPath));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopAndDispose();
                triggerEnd();
            }
        });
    }

    private void initFX(JFXPanel fxPanel, String videoPath) {
        try {
            File file = new File(videoPath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "视频文件不存在，程序将退出", "提示", JOptionPane.WARNING_MESSAGE);
                triggerEnd();
                return;
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0.8);

            mediaPlayer.statusProperty().addListener((obs, old, newStatus) -> {
                System.out.println("结束视频状态: " + newStatus);
                if (newStatus == MediaPlayer.Status.PLAYING) {
                    //startForceExitTimer();
                }
            });

            mediaPlayer.setOnReady(() -> {
                System.out.println("结束视频已就绪，开始播放");
                mediaPlayer.play();
                // 强制刷新 Swing 容器
                SwingUtilities.invokeLater(() -> {
                    fxPanel.repaint();
                    fxPanel.requestFocus();
                    revalidate();
                });
                // 再延迟刷新一次
                new Timer(200, e -> fxPanel.repaint()).start();
            });

            mediaPlayer.setOnError(() -> {
                System.err.println("结束视频错误: " + mediaPlayer.getError());
                JOptionPane.showMessageDialog(this, "可惜了出bug了，程序将退出", "错误", JOptionPane.ERROR_MESSAGE);
                triggerEnd();
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("结束视频播放完毕");
                Platform.runLater(this::triggerEnd);
            });

            // 在 initFX 方法内，创建 MediaView 后：
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(false);   // 拉伸铺满
            mediaView.setFitWidth(fxPanel.getWidth());
            mediaView.setFitHeight(fxPanel.getHeight());

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
            JOptionPane.showMessageDialog(this, "初始化视频失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            triggerEnd();
        }
    }


    private void triggerEnd() {
        if (isEnded) return;
        isEnded = true;
        if (forceExitTimer != null) forceExitTimer.stop();
        stopAndDispose();
        if (onVideoEnd != null) {
            SwingUtilities.invokeLater(onVideoEnd);
        }
    }

    private void stopAndDispose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        // 清理 JFXPanel 场景
        Component[] comps = getContentPane().getComponents();
        for (Component comp : comps) {
            if (comp instanceof JFXPanel) {
                ((JFXPanel) comp).setScene(null);
                getContentPane().remove(comp);
            }
        }
        dispose();
        Platform.runLater(() -> {});
    }
}