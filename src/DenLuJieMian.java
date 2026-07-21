import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class DenLuJieMian extends JDialog {//对话框
    private JTextField yongHuMing;
    private JPasswordField miMa;
    private boolean denLuJieGuo = false;

    public DenLuJieMian() {
        setTitle("登录");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(Color.PINK);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false); // 透明背景
        try {
            File imgFile = new File("logo.jpg");
            if (imgFile.exists()) {
                BufferedImage original = ImageIO.read(imgFile);
                int targetSize = 400;//像素400*400
                Image scaled = original.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaled));
                logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
                headerPanel.add(logoLabel);
            }
        } catch (Exception e) {
            System.out.println("未找到 logo.jpg，不显示左上角图片");
        }
        add(headerPanel, BorderLayout.NORTH);//如果没有图片也不会占用上面位置

        int fontSize = 60;
        Font f = new Font("serif", Font.PLAIN, fontSize);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));

        JLabel lblUser = new JLabel("用户名：");
        lblUser.setFont(f);
        inputPanel.add(lblUser);

        yongHuMing = new JTextField();//不隐藏
        yongHuMing.setFont(f);
        inputPanel.add(yongHuMing);

        JLabel lblPwd = new JLabel("密码：");
        lblPwd.setFont(f);
        inputPanel.add(lblPwd);

        miMa = new JPasswordField();//隐藏
        miMa.setFont(f);
        inputPanel.add(miMa);

        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("登录");
        JButton canceButton = new JButton("取消");
        loginButton.setFont(f);
        canceButton.setFont(f);
        buttonPanel.add(loginButton);
        buttonPanel.add(canceButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            String user = yongHuMing.getText();
            String pass = new String(miMa.getPassword());
            if ("CYJ".equals(user) && "123".equals(pass)) {
                denLuJieGuo = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        canceButton.addActionListener(e -> dispose());

        pack();//自动调整大小
        setLocationRelativeTo(null);//居中
    }

    public boolean isDenLuJieGuo() {
        return denLuJieGuo;
    }
}