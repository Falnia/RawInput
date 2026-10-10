package dev.rawinput;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DirectAndRawInputEnvironmentPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Polls the mouse through JInput on a separate thread and collects
 * the raw movement, so windows pointer acceleration never touches it.
 */
public final class RawMouse {

    private static final Object LOCK = new Object();
    private static float accX;
    private static float accY;

    private static volatile List<Controller> mice = new ArrayList<Controller>();
    private static ControllerEnvironment env;

    private RawMouse() {
    }

    public static boolean start() {
        try {
            try {
                // raw input plugin is windows only
                env = new DirectAndRawInputEnvironmentPlugin();
            } catch (Throwable t) {
                env = ControllerEnvironment.getDefaultEnvironment();
            }
            scan();
            if (mice.isEmpty()) {
                System.out.println("[RawInput] no mouse found, using vanilla input");
                return false;
            }
        } catch (Throwable t) {
            System.out.println("[RawInput] failed to start: " + t);
            return false;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        }, "RawInput mouse poller");
        thread.setDaemon(true);
        thread.start();
        System.out.println("[RawInput] started, mice found: " + mice.size());
        return true;
    }

    private static void scan() {
        List<Controller> found = new ArrayList<Controller>();
        for (Controller c : env.getControllers()) {
            if (c.getType() == Controller.Type.MOUSE
                    && c.getComponent(Component.Identifier.Axis.X) != null
                    && c.getComponent(Component.Identifier.Axis.Y) != null) {
                found.add(c);
            }
        }
        mice = found;
    }

    private static void loop() {
        while (true) {
            float dx = 0;
            float dy = 0;
            boolean lost = false;

            for (Controller c : mice) {
                try {
                    if (!c.poll()) {
                        lost = true;
                        continue;
                    }
                    dx += c.getComponent(Component.Identifier.Axis.X).getPollData();
                    dy += c.getComponent(Component.Identifier.Axis.Y).getPollData();
                } catch (Throwable t) {
                    lost = true;
                }
            }

            if (dx != 0 || dy != 0) {
                synchronized (LOCK) {
                    accX += dx;
                    accY += dy;
                }
            }

            try {
                if (lost) {
                    // mouse got unplugged or replaced, look for it again
                    Thread.sleep(1000);
                    scan();
                } else {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /** Returns the movement since the last call. Fractions stay for the next frame. */
    public static int[] take() {
        synchronized (LOCK) {
            int x = (int) accX;
            int y = (int) accY;
            accX -= x;
            accY -= y;
            return new int[]{x, y};
        }
    }

    public static void reset() {
        synchronized (LOCK) {
            accX = 0;
            accY = 0;
        }
    }
}
