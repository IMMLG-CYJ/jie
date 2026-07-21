import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class ImageData {
    private BufferedImage image;//tp
    private String description;//简介
    private String musicPath;

    public ImageData(BufferedImage image, String description, String musicPath) {
        this.image = image;
        this.description = description;
        this.musicPath = musicPath;
    }

    public BufferedImage getImage() { return image; }
    public String getDescription() { return description; }
    public String getMusicPath() { return musicPath; }
}

public class JiaZhai {
    private static final String IMAG_DIR = "tp";
    private static final String DESC_FILE = "jj.txt";

    // 加载图片入口方法
    public static List<ImageData> loadtp() {
        File dir = new File(IMAG_DIR);
        List<ImageData> result = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            result = loadFromDirectory(dir);
        }
        return result;
    }

    // 从tp文件夹加载图片
    private static List<ImageData> loadFromDirectory(File dir) {
        List<ImageData> list = new ArrayList<>();
        File[] imageFiles = dir.listFiles((d, name) ->
                name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".gif")
        );
        if (imageFiles == null || imageFiles.length == 0) return list;

        // 在jj.txt读取简介
        Properties descProps = new Properties();
        File descFile = new File(dir, "jj.txt");
        if (descFile.exists()) {
            try (FileInputStream fis = new FileInputStream(descFile);
                 InputStreamReader isr = new InputStreamReader(fis, "UTF-8")) {//用java里的utf-8加载
                descProps.load(isr);
            } catch (IOException ignored) {}
        }

        for (File file : imageFiles) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img == null) continue;//读取失败

                String name = file.getName();
                String desc = descProps.getProperty(name, "");

                // 音乐与照片文件同名.wav
                String baseName = name.substring(0, name.lastIndexOf('.'));
                File musicFile = new File(dir, baseName + ".wav");
                String musicPath = musicFile.exists() ? musicFile.getAbsolutePath() : null;

                list.add(new ImageData(img, desc, musicPath));
            } catch (IOException ignored) {}
        }
        return list;
    }}

