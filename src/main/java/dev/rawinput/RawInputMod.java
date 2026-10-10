package dev.rawinput;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "rawinput", name = "RawInput", version = "1.0.1", clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class RawInputMod {

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (RawMouse.start()) {
            install();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    // some mods swap the mouse helper back, so check again whenever a gui opens
    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        install();
    }

    private static void install() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && !(mc.mouseHelper instanceof RawMouseHelper)) {
            mc.mouseHelper = new RawMouseHelper();
        }
    }
}
