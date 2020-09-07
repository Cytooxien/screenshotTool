package dev.elektronisch.screenshot.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ScreenCaptureUtil {

    private static Robot robot;
    private static GraphicsEnvironment environment;

    static {
        try {
            robot = new Robot();
            environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        } catch (final AWTException ignored) {
        }
    }

    private ScreenCaptureUtil() {
    }

    public static BufferedImage captureScreen(final int x, final int y) {
        for (final GraphicsDevice device : environment.getScreenDevices()) {
            final GraphicsConfiguration configuration = device.getConfigurations()[0];
            final Rectangle bounds = configuration.getBounds();
            if (x >= bounds.getMinX() && x <= bounds.getMaxX() && y >= bounds.getMinY() && y <= bounds.getMaxY())
                return robot.createScreenCapture(bounds);
        }
        return null;
    }
}
