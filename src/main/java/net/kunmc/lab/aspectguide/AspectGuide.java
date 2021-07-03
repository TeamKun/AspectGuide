package net.kunmc.lab.aspectguide;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("aspectguide")
public class AspectGuide {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public AspectGuide() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(FMLClientSetupEvent event) {
        Bindings.bind();
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        Bindings.onPressed();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        Renderer.onRender(event);
    }

    public static class Bindings {
        private static final KeyBinding TOGGLE = new KeyBinding("key.aspectguide.toggle", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_F4, "key.categories.ui");
        public static boolean IsEnabled;

        public static void bind() {
            ClientRegistry.registerKeyBinding(TOGGLE);
        }

        public static void onPressed() {
            if (Bindings.TOGGLE.isPressed())
                IsEnabled = !IsEnabled;
        }
    }

    public static class Renderer {
        public static void onRender(RenderGameOverlayEvent.Post event) {
            if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
                return;

            if (!Bindings.IsEnabled)
                return;

            MatrixStack matrixStack = event.getMatrixStack();
            Matrix4f matrix = matrixStack.getLast().getMatrix();
            MainWindow window = event.getWindow();

            float margin = 2.0f;
            float aspect = 16.0f / 9.0f;
            float alpha = 0.5f;

            float width = window.getScaledWidth();
            float height = window.getScaledHeight();
            float minWidth = width;
            float minHeight = height;
            if ((width * aspect) / height < 1)
                minHeight = width * aspect;
            else
                minWidth = height / aspect;

            RenderSystem.disableTexture();
            RenderSystem.lineWidth(1);

            Tessellator t = Tessellator.getInstance();
            BufferBuilder b = t.getBuffer();
            b.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            b.pos(matrix, width / 2 - (minWidth / 2 + margin), height / 2 - (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(matrix, width / 2 - (minWidth / 2 + margin), height / 2 + (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(matrix, width / 2 + (minWidth / 2 + margin), height / 2 + (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(matrix, width / 2 + (minWidth / 2 + margin), height / 2 - (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            t.draw();

            RenderSystem.enableTexture();
        }
    }
}
