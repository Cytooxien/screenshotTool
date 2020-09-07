package dev.elektronisch.screenshot;

import dev.elektronisch.screenshot.util.ImageUploadUtil;
import dev.elektronisch.screenshot.util.ScreenCaptureUtil;
import dev.elektronisch.screenshot.util.UpdateUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ScreenshotToolApplication {

    private static final double CURRENT_VERSION = 1.2;

    private final DefaultListModel<String> urlListModel = new DefaultListModel<>();
    private final int[] emptyIntArray = new int[]{};
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM. HH:mm:ss");

    private JFrame frame;

    public ScreenshotToolApplication() {
        createWindow();
        addWindowComponents();
        packAndShowWindow();

        final String updateDownloadUrl = UpdateUtil.checkForUpdate(CURRENT_VERSION);
        if (updateDownloadUrl != null) {
            showUpdateDialog(updateDownloadUrl);
        }
    }

    public static void main(final String[] args) throws Exception {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        new ScreenshotToolApplication();
    }

    private void createWindow() {
        frame = new JFrame("Cytooxien Screenshot-Tool v" + CURRENT_VERSION);
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
                final BufferedImage image = ScreenCaptureUtil.captureScreen(frame.getX(), frame.getY());
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

    private void showUpdateDialog(final String downloadUrl) {
        final JDialog updateDialog = new JDialog(frame, "Neue Version verfügbar!");
        final JSplitPane splitPane = new JSplitPane();
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        splitPane.setDividerLocation(150);

        final JLabel comp = new JLabel("<html><body>Es ist eine neue Version des ScreenshotTools verfügbar. <br><br>Klicke um diese direkt herunterzuladen.</body></html>");
        splitPane.setLeftComponent(comp);
        final JButton downloadButton = new JButton("Herunterladen");
        downloadButton.addActionListener(event -> new Thread(() -> {
            downloadButton.setText("Herunterladen ...");
            UpdateUtil.downloadUpdate(downloadUrl, new File(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)));
            downloadButton.setText("<html><body>Abgeschlossen. <br><br>Führe die neue Version aus!</body></html>");
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException ignored) {
            }
            System.exit(0);
        }).start());
        splitPane.setRightComponent(downloadButton);

        updateDialog.add(splitPane);
        updateDialog.setSize(300, 200);
        updateDialog.setLocation(frame.getX() + ((frame.getWidth() - updateDialog.getWidth()) / 2), frame.getY() + ((frame.getHeight() - updateDialog.getHeight()) / 2));
        updateDialog.setResizable(false);
        updateDialog.setVisible(true);
    }

    private void browse(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (final Exception ignored) {
        }
    }
}
