package dev.rawinput;

import net.minecraft.util.MouseHelper;

public class RawMouseHelper extends MouseHelper {

    @Override
    public void grabMouseCursor() {
        super.grabMouseCursor();
        // drop whatever moved while the cursor was free
        RawMouse.reset();
    }

    @Override
    public void mouseXYChange() {
        int[] d = RawMouse.take();
        this.deltaX = d[0];
        this.deltaY = -d[1];
    }
}
