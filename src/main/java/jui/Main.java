package jui;

import first.gaussian.GaussianDao;
import first.histogram.HistogramDao;
import org.jetbrains.annotations.NotNull;
import second.edgeDetection.EdgeDetectionDao;
import second.hough.HoughDao;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public JFrame jf = new JFrame("图像处理");

    JMenuBar menuBar;

    private JSplitPane jSplitPane;
    private JPanel myPanel1 = new JPanel();//面板1
    private JPanel myPanel2 = new JPanel();//面板2
    private JLabel imageJLabel;
    private JLabel editImageJLabel;

    private ImageIcon imageIcon;
    private String imagePath;

    private ImageIcon editImageIcon;
    private BufferedImage editImage;

    private boolean isEdge = false;

    public JMenuBar getJMenuBar() {

        /*
         * 创建一个菜单栏
         */
        menuBar = new JMenuBar();
        /*
         * 创建一级菜单
         */
        JMenu fileMenu = new JMenu("文件");
        JMenu editMenu = new JMenu("编辑");
        JMenu bowMenu = new JMenu("物体识别");
        JMenu ldaMenu = new JMenu("人脸图像识别");
        JMenu numberMenu = new JMenu("手写数字识别");
        JMenu viewMenu = new JMenu("图像");
        JMenu aboutMenu = new JMenu("关于");
        // 一级菜单添加到菜单栏
        menuBar.add(fileMenu);
        initFileMenu(fileMenu);

        menuBar.add(editMenu);
        initEditMenu(editMenu);

        menuBar.add(bowMenu);
        initBowMenu(bowMenu);

        menuBar.add(ldaMenu);
        initLdaMenu(ldaMenu);

        menuBar.add(numberMenu);
        initNumberMenu(numberMenu);

        menuBar.add(viewMenu);
        menuBar.add(aboutMenu);


        /*
         * 创建 "视图" 一级菜单的子菜单
         */
        final JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem("复选框子菜单");
        final JRadioButtonMenuItem radioButtonMenuItem01 = new JRadioButtonMenuItem("单选按钮子菜单01");
        final JRadioButtonMenuItem radioButtonMenuItem02 = new JRadioButtonMenuItem("单选按钮子菜单02");
        // 子菜单添加到一级菜单
        viewMenu.add(checkBoxMenuItem);
        viewMenu.addSeparator();                // 添加一个分割线
        viewMenu.add(radioButtonMenuItem01);
        viewMenu.add(radioButtonMenuItem02);

        // 其中两个 单选按钮子菜单，要实现单选按钮的效果，需要将它们放到一个按钮组中
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(radioButtonMenuItem01);
        btnGroup.add(radioButtonMenuItem02);

        // 默认第一个单选按钮子菜单选中
        radioButtonMenuItem01.setSelected(true);


        // 设置 复选框子菜单 状态改变 监听器
        checkBoxMenuItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("复选框是否被选中: " + checkBoxMenuItem.isSelected());
            }
        });

        // 设置 单选按钮子菜单 状态改变 监听器
        radioButtonMenuItem01.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("单选按钮01 是否被选中: " + radioButtonMenuItem01.isSelected());
            }
        });

        return menuBar;
    }

    /**
     * 文件菜单初始化
     *
     * @param fileMenu
     */
    public void initFileMenu(@NotNull JMenu fileMenu) {
        /*
         * 创建 "文件" 一级菜单的子菜单
         */
        //JMenuItem newMenuItem = new JMenuItem("新建");
        JMenu openMenu = new JMenu("打开");
        JMenuItem exitMenuItem = new JMenuItem("关闭");
        JMenuItem saveMenuItem = new JMenuItem("另存为");
        // 子菜单添加到一级菜单
        //fileMenu.add(newMenuItem);
        fileMenu.add(openMenu);
        fileMenu.addSeparator();       // 添加一条分割线
        fileMenu.add(exitMenuItem);
        fileMenu.add(saveMenuItem);

        JMenuItem imageMenuItem = new JMenuItem("图片");    //创建子菜单
        JMenuItem trainMenuItem = new JMenuItem("训练集");
        //JMenuItem trainMenuItem=new JMenuItem("包");
        //JMenuItem jmi4=new JMenuItem("类");
        openMenu.add(imageMenuItem);
        openMenu.add(trainMenuItem);

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */

        // 设置 "打开图片" 子菜单被点击的监听器
        imageMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("打开图片  被点击");

                FileDialog fd = new FileDialog(jf, "选择图片");
                fd.setVisible(true);

                imagePath = fd.getDirectory() + fd.getFile();
                System.out.println(imagePath);

                imageIcon = new ImageIcon(imagePath);

                setImageIcon(imageIcon, imageJLabel, myPanel1);
            }
        });
        // 设置 "打开" 子菜单被点击的监听器
        trainMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("打开训练集  被点击");

//                String dir = null;
//                FileDialog fd = new FileDialog(jf, "选择图片");
//                fd.setVisible(true);

                String filePath = null;


                JFileChooser fileChooser = new JFileChooser("data");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(fileChooser);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filePath = fileChooser.getSelectedFile().getAbsolutePath();//这个就是你选择的文件夹的路径
                }
                String dataFolder = filePath;
                System.out.println(dataFolder);

            }
        });
        // 设置 "关闭" 子菜单被点击的监听器
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("关闭  被点击");
                editImageIcon = null;
                editImageJLabel.setIcon(null);
                isEdge = false;
                editImage = null;
            }
        });

        // 设置 "另存为" 子菜单被点击的监听器
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("另存为  被点击");

                FileDialog fd2 = new FileDialog(jf, "储存图片");
                fd2.setMode(FileDialog.SAVE);
                fd2.setVisible(true);
                try {
                    ImageIO.write(editImage, "jpg", new File(fd2.getDirectory(), fd2.getFile()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * 编辑菜单初始化
     *
     * @param editMenu
     */
    public void initEditMenu(@NotNull JMenu editMenu) {
        /*
         * 创建 "编辑" 一级菜单的子菜单
         */
        JMenuItem DrynessMenuItem = new JMenuItem("去燥");
        JMenuItem HistogramEqualizationMenuItem = new JMenuItem("直方图均衡化");
        JMenuItem edgeDetectionMenuItem = new JMenuItem("边缘检测");
        JMenuItem houghLineMenuItem = new JMenuItem("直线检测");
        // 子菜单添加到一级菜单
        editMenu.add(DrynessMenuItem);
        editMenu.add(HistogramEqualizationMenuItem);
        editMenu.add(edgeDetectionMenuItem);
        editMenu.add(houghLineMenuItem);

        // 设置 "去燥" 子菜单被点击的监听器
        DrynessMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("去燥  被点击");
                editImage = GaussianDao.INSTANCE.gaussianEdit(imagePath);
                editImageIcon = new ImageIcon(editImage);
                setImageIcon(editImageIcon, editImageJLabel, myPanel2);
            }
        });

        // 设置 "直方图均衡化" 子菜单被点击的监听器
        HistogramEqualizationMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("直方图均衡化  被点击");
                editImage = HistogramDao.INSTANCE.histogramEdit(imagePath);
                editImageIcon = new ImageIcon(editImage);
                setImageIcon(editImageIcon, editImageJLabel, myPanel2);
            }
        });

        // 设置 "边缘检测" 子菜单被点击的监听器
        edgeDetectionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("边缘检测  被点击");
                Object[] options = {"sobel1", "sobel2", "sobel3"};
                int response = JOptionPane.showOptionDialog(null,
                        "这是个选项对话框，用户可以选择自己的按钮的个数",
                        "选项对话框标题",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (response == 0) {
                    if (editImage != null) {
                        editImage = EdgeDetectionDao.INSTANCE.sobel1ByImage(editImage);
                    } else {
                        editImage = EdgeDetectionDao.INSTANCE.sobel1(imagePath);
                    }
                } else if (response == 1) {
                    if (editImage != null) {
                        editImage = EdgeDetectionDao.INSTANCE.sobel2ByImage(editImage);
                    } else {
                        editImage = EdgeDetectionDao.INSTANCE.sobel2(imagePath);
                    }
                } else if (response == 2) {
                    if (editImage != null) {
                        editImage = EdgeDetectionDao.INSTANCE.sobel3ByImage(editImage);
                    } else {
                        editImage = EdgeDetectionDao.INSTANCE.sobel3(imagePath);
                    }
                }

                editImageIcon = new ImageIcon(editImage);
                setImageIcon(editImageIcon, editImageJLabel, myPanel2);
                isEdge = true;
            }
        });

        // 设置 "直线检测" 子菜单被点击的监听器
        houghLineMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("直线检测  被点击");
                //editImage = HoughDao.INSTANCE.houghEdit(imagePath);
                Object[] options = {"方案1", "方案2", "方案3"};
                int response = JOptionPane.showOptionDialog(null,
                        "这是个选项对话框，用户可以选择自己的按钮的个数",
                        "选项对话框标题",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (isEdge) {
                    if (response == 0) {
                        editImage = HoughDao.INSTANCE.houghEditByImage(1, editImage);
                    } else if (response == 1) {
                        editImage = HoughDao.INSTANCE.houghEditByImage(2, editImage);
                    } else if (response == 2) {
                        editImage = HoughDao.INSTANCE.houghEditByImage(3, editImage);
                    }
                    editImageIcon = new ImageIcon(editImage);
                    setImageIcon(editImageIcon, editImageJLabel, myPanel2);
                } else {
                    if (response == 0) {
                        editImage = HoughDao.INSTANCE.houghEdit(1, imagePath);
                    } else if (response == 1) {
                        editImage = HoughDao.INSTANCE.houghEdit(2, imagePath);
                    } else if (response == 2) {
                        editImage = HoughDao.INSTANCE.houghEdit(3, imagePath);
                    }
                    editImageIcon = new ImageIcon(editImage);
                    setImageIcon(editImageIcon, editImageJLabel, myPanel2);
                }
            }
        });
    }

    /**
     * 图像检索菜单初始化
     *
     * @param bowMenu
     */
    public void initBowMenu(@NotNull JMenu bowMenu) {
        /*
         * 创建 "文件" 一级菜单的子菜单
         */
        //JMenuItem newMenuItem = new JMenuItem("新建");
        JMenuItem trainMenuItem = new JMenuItem("训练");
        JMenuItem testMenuItem = new JMenuItem("测试");
        JMenuItem categoryMenuItem = new JMenuItem("识别");
        // 子菜单添加到一级菜单
        bowMenu.add(trainMenuItem);
        bowMenu.addSeparator();       // 添加一条分割线
        bowMenu.add(testMenuItem);
        bowMenu.add(categoryMenuItem);

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */

        // 设置 "训练" 子菜单被点击的监听器
        trainMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("训练  被点击");

            }
        });
        // 设置 "测试" 子菜单被点击的监听器
        testMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("测试  被点击");

            }
        });
        // 设置 "识别" 子菜单被点击的监听器
        categoryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("识别  被点击");

            }
        });
    }

    /**
     * 人脸图像识别菜单初始化
     *
     * @param ldaMenu
     */
    public void initLdaMenu(@NotNull JMenu ldaMenu) {
        /*
         * 创建 "文件" 一级菜单的子菜单
         */
        JMenuItem trainMenuItem = new JMenuItem("训练");
        JMenuItem testMenuItem = new JMenuItem("测试");
        JMenuItem categoryMenuItem = new JMenuItem("识别");
        // 子菜单添加到一级菜单
        ldaMenu.add(trainMenuItem);
        ldaMenu.addSeparator();       // 添加一条分割线
        ldaMenu.add(testMenuItem);
        ldaMenu.add(categoryMenuItem);

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */

        // 设置 "训练" 子菜单被点击的监听器
        trainMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("训练  被点击");

            }
        });
        // 设置 "测试" 子菜单被点击的监听器
        testMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("测试  被点击");

            }
        });
        // 设置 "识别" 子菜单被点击的监听器
        categoryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("识别  被点击");

            }
        });
    }

    /**
     * 图像检索菜单初始化
     *
     * @param numberMenu
     */
    public void initNumberMenu(@NotNull JMenu numberMenu) {
        /*
         * 创建 "文件" 一级菜单的子菜单
         */
        //JMenuItem newMenuItem = new JMenuItem("新建");
        JMenuItem trainMenuItem = new JMenuItem("训练");
        JMenuItem testMenuItem = new JMenuItem("测试");
        JMenuItem categoryMenuItem = new JMenuItem("识别");
        // 子菜单添加到一级菜单
        numberMenu.add(trainMenuItem);
        numberMenu.addSeparator();       // 添加一条分割线
        numberMenu.add(testMenuItem);
        numberMenu.add(categoryMenuItem);

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */

        // 设置 "训练" 子菜单被点击的监听器
        trainMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("训练  被点击");

            }
        });
        // 设置 "测试" 子菜单被点击的监听器
        testMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("测试  被点击");

            }
        });
        // 设置 "识别" 子菜单被点击的监听器
        categoryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("识别  被点击");

            }
        });
    }

    public JSplitPane getJSplitPane() {
        jSplitPane = new JSplitPane();

        jSplitPane.setOneTouchExpandable(true);//让分割线显示出箭头
        jSplitPane.setContinuousLayout(true);//操作箭头，重绘图形
        //jSplitPane.setPreferredSize(new Dimension (100,200));
        jSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);//设置分割线方向


        myPanel1.setSize(jf.getWidth()/2, jf.getHeight());
        imageJLabel = new JLabel();
        myPanel1.add(imageJLabel);

        myPanel2.setSize(jf.getWidth()/2, jf.getHeight());

        editImageJLabel = new JLabel();
        myPanel2.add(editImageJLabel);

        jSplitPane.setLeftComponent(myPanel1);//布局中添加组件 ，面板1
        jSplitPane.setRightComponent(myPanel2);//添加面板2
        jSplitPane.setDividerSize(1);//设置分割线的宽度
        //jSplitPane.setDividerLocation(100);//设置分割线位于中央
        jSplitPane.setDividerLocation(jf.getWidth()/2);//设定分割线的距离左边的位置

        return jSplitPane;
    }

    private void setImageIcon(ImageIcon icon, @NotNull JLabel jLabel, JPanel panel) {

        jLabel.setIcon(new ImageIcon((resize(icon, panel))));
        panel.setVisible(true);
        panel.updateUI();
    }

    /**
     * 窗口变化监听器
     */
    public void resizeListener() {
        jf.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int jfWidth = jf.getWidth();
                int jfHeight = jf.getHeight();
                myPanel1.setSize(jfWidth / 2, jfHeight - menuBar.getHeight());
                myPanel2.setSize(jfWidth / 2, jfHeight - menuBar.getHeight());
                jSplitPane.setDividerLocation(jfWidth / 2);
//                System.out.println("jfHeight " + jfHeight);
//                System.out.println("myPanel1 " + myPanel1.getHeight());
                if (imageIcon != null) {
//                    System.out.println("imageIcon "+imageIcon.getIconHeight());
                    imageJLabel.setIcon(new ImageIcon((resize(imageIcon, myPanel1))));
                }
                if (editImageIcon != null) {
//                    System.out.println("imageIcon "+imageIcon.getIconHeight());
                    editImageJLabel.setIcon(new ImageIcon((resize(editImageIcon, myPanel2))));
                }
            }
        });
    }

    /**
     * 图片跟随窗口大小发生改变
     *
     * @param img
     * @param jp
     * @return
     */
    private Image resize(@NotNull ImageIcon img, @NotNull JPanel jp) {

        int imgWidth = img.getIconWidth();
        int imgHeight = img.getIconHeight();
        int conWidth = jp.getWidth();
        int conHeight = jp.getHeight();
        int reImgWidth;
        int reImgHeight;

        if ((double) imgWidth / (double) imgHeight >= (double) conWidth / (double) conHeight) {
            if (imgWidth > conWidth) {
                reImgWidth = conWidth;
                reImgHeight = imgHeight * reImgWidth / imgWidth;
            } else {
                reImgWidth = imgWidth;
                reImgHeight = imgHeight;
            }
        } else {
            if (imgWidth > conWidth) {
                reImgHeight = conHeight;
                reImgWidth = imgWidth * reImgHeight / imgHeight;
            } else {
                reImgWidth = imgWidth;
                reImgHeight = imgHeight;
            }
        }
        Image icon = img.getImage().getScaledInstance(reImgWidth, reImgHeight, Image.SCALE_DEFAULT);

        return icon;
    }

    public static void main(String[] args) throws Exception {
        Main t = new Main();

        //t.jf.setLayout(new GridLayout());

        t.jf.setSize(800, 800);
        t.jf.setLocationRelativeTo(null);
        t.jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        t.resizeListener();

        /*
         * 最后 把菜单栏设置到窗口
         */
        t.jf.setJMenuBar(t.getJMenuBar());

        t.jf.setContentPane(t.getJSplitPane());

        t.jf.setVisible(true);
    }
}
