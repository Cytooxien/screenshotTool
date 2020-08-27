package dev.elektronisch.screenshot.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ScreenCaptureUtil {

    private static Robot robot;
    private static Rectangle screenRectangle;

    static {
        try {
            robot = new Robot();
            screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } catch (final AWTException ignored) {
        }
    }

    private ScreenCaptureUtil() {
    }

    public static BufferedImage captureScreen() {
        return robot.createScreenCapture(screenRectangle);
    }
}
