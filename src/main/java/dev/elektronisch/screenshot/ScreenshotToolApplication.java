package dev.elektronisch.screenshot;

import dev.elektronisch.screenshot.util.ImageUploadUtil;
import dev.elektronisch.screenshot.util.ScreenCaptureUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ScreenshotToolApplication {

    private final DefaultListModel<String> urlListModel = new DefaultListModel<>();
    private final int[] emptyIntArray = new int[]{};
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM. HH:mm:ss");

    private JFrame frame;

    public ScreenshotToolApplication() {
        createWindow();
        addWindowComponents();
        packAndShowWindow();
    }

    public static void main(final String[] args) throws Exception {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        new ScreenshotToolApplication();
    }

    private void createWindow() {
        frame = new JFrame("Cytooxien Screenshot-Tool");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            frame.setIconImage(ImageIO.read(new URL("https://wiki.cytooxien.de/assets/img/favicon-32x32.png")));
        } catch (final IOException ignored) {
        }
        frame.setResizable(false);
    }

    private void addWindowComponents() {
        final JButton createScreenshot = new JButton("Screenshot erstellen");
        createScreenshot.addActionListener(event -> {
            frame.setVisible(false);
            try {
                Thread.sleep(200);
                final BufferedImage image = ScreenCaptureUtil.captureScreen();
                urlListModel.add(0, "Hochladen ...");
                frame.setVisible(true);

                new Thread(() -> {
                    final String imageUrl = ImageUploadUtil.uploadPNG(image);
                    if (imageUrl != null) {
                        urlListModel.set(0, imageUrl + " (" + format.format(new Date()) + ")");
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(imageUrl), null);
                    }
                }).start();
            } catch (final InterruptedException ignored) {
            }
        });

        final JSplitPane splitPane = new JSplitPane();
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        splitPane.setLeftComponent(createScreenshot);
        final JList<String> urlList = new JList<>(urlListModel);
        urlList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        urlList.addListSelectionListener(event -> {
            final String selectedValue = urlList.getSelectedValue();
            if (selectedValue == null) return;

            urlList.setSelectedIndices(emptyIntArray);
            browse(selectedValue.substring(0, selectedValue.indexOf("(") - 1));
        });

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(urlList);
        urlList.setLayoutOrientation(JList.VERTICAL);
        splitPane.setRightComponent(scrollPane);
        frame.add(splitPane);
    }

    private void packAndShowWindow() {
        frame.pack();
        frame.setSize(600, 250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void browse(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (final Exception ignored) {
        }
    }
}
