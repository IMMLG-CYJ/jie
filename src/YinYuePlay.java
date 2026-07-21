import javax.sound.sampled.*;
import java.io.File;

public class YinYuePlay {
    private static Clip clip;
    private static float currentVolume = 0.7f;   // 默认音量 0.0~1.0

    // 播放音乐（支持循环）
    public static void playMusic(String path, boolean loop) {
        try {
            stopMusic();  // 先停止当前音乐

            File musicFile = new File(path);
            if (!musicFile.exists()) {
                System.out.println("音乐文件不存在：" + path);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(ais);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);//循环
            }
            clip.start();

            // 应用当前音量设置
            applyVolume();
        } catch (Exception e) {
            System.out.println("音乐播放失败，仅支持 wav 格式");
        }
    }

    // 停止音乐
    public static void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    // 设置音量（0.0 ~ 1.0）
    public static void setVolume(float volume) {
        currentVolume = Math.max(0f, Math.min(1f, volume));
        applyVolume();
    }

    // 获取当前音量（0~1）
    public static float getVolume() {
        return currentVolume;
    }

    // 将音量值应用到当前 Clip
    private static void applyVolume() {
        if (clip == null || !clip.isOpen()) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // 将线性 0~1 映射到分贝范围（通常最小 -80dB，最大 6dB）
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float dB = min + (max - min) * currentVolume;
            gainControl.setValue(dB);
        } catch (Exception e) {
            // 某些音频格式可能不支持音量控制，忽略异常
        }
    }
}