package de.klenze_kk.lingling;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

public final class Main {

    private static final Logger LOGGER = Logger.getLogger("[Lingling-System]");

    private static JFrame WINDOW;
    private static JPanel currentDisplay;

    public static void main(String[] args) {}

    public static JPanel getCurrentDisplay() {
        return currentDisplay;
    }

    public static void setCurrentDisplay(JPanel currentDisplay) {
        Main.currentDisplay = currentDisplay;
    }

    public static void log(Level logLevel, String message) {
        synchronized (LOGGER) {
            LOGGER.log(logLevel, message);
        }
    }

    public static void log(Level logLevel, String message, Throwable error) {
        synchronized (LOGGER) {
            LOGGER.log(logLevel, message, error);
        }
    }

}
