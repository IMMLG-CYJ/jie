import javax.swing.*;
import java.io.File;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class QiDong {
    public static void main(String[] args) {
        new JFXPanel();
        Platform.setImplicitExit(false); // 确保 JavaFX 平台启动且不自动关闭

        DenLuJieMian denLuJieMian = new DenLuJieMian();
        denLuJieMian.setVisible(true);
        if (denLuJieMian.isDenLuJieGuo()) {// 登录成功，在事件分发线程中执行后续 UI 操作
            SwingUtilities.invokeLater(() -> {
                String welcomeVideoPath = "welcome.mp4";
                VideoPlayer welcomePlayer = new VideoPlayer(welcomeVideoPath, () -> {
                    SwingUtilities.invokeLater(() -> {
                        ImageBrowserFrame frame = new ImageBrowserFrame();


                        frame.setOnCloseListener(() -> {
                            String endVideoPath = "goodbye.mp4";
                            File endFile = new File(endVideoPath);
                            if (!endFile.exists()) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "结束视频文件（" + endVideoPath + "）不存在，程序将退出。",
                                        "提示",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                System.exit(0);
                                return;
                            }

                            Timer delayTimer = new Timer(1500, e -> {
                                EndVideoPlayer endPlayer = new EndVideoPlayer(endVideoPath, () -> {
                                    System.exit(0);
                                });
                                endPlayer.setVisible(true);
                            });
                            delayTimer.setRepeats(false);
                            delayTimer.start();
                        });
                        frame.setVisible(true);
                    });
                });
                welcomePlayer.setVisible(true);
            });
        } else {
            System.exit(0);
        }
    }
}
