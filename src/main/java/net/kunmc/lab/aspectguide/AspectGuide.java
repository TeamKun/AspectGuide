package net.kunmc.lab.aspectguide;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * AspectGuide Modクラス
 */
@Mod(
        modid = AspectGuide.MOD_ID,
        name = AspectGuide.MOD_NAME,
        clientSideOnly = true,
        version = AspectGuide.VERSION
)
public class AspectGuide {

    public static final String MOD_ID = "aspectguide";
    public static final String MOD_NAME = "AspectGuide";
    public static final String VERSION = "1.0-SNAPSHOT";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static AspectGuide INSTANCE;

    /**
     * イベントを追加する
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        // Forgeイベント登録
        MinecraftForge.EVENT_BUS.register(this);
        // キーをバインドする
        Bindings.bind();
    }

    /**
     * キー押下イベント
     *
     * @param event キー入力イベント
     */
    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        // キーが押された
        Bindings.onPressed();
    }

    /**
     * 描画イベント
     *
     * @param event 描画イベント
     */
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        // 描画を行う
        Renderer.onRender(event);
    }

    /**
     * キーバインド処理クラス
     */
    public static class Bindings {
        // キー
        private static final KeyBinding TOGGLE = new KeyBinding("key.aspectguide.toggle", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_F4, "key.categories.ui");
        // 機能が有効であるフラグ
        public static boolean IsEnabled;

        /**
         * キー登録を行う
         */
        public static void bind() {
            // FMLにキーを登録する
            ClientRegistry.registerKeyBinding(TOGGLE);
        }

        /**
         * キー押下イベント
         */
        public static void onPressed() {
            // TOGGLEキーが押されているかチェックする
            if (Bindings.TOGGLE.isPressed())
                // 機能有効フラグを切り替え
                IsEnabled = !IsEnabled;
        }
    }

    /**
     * 描画クラス
     */
    public static class Renderer {
        /**
         * 描画を行う
         *
         * @param event 描画イベント
         */
        public static void onRender(RenderGameOverlayEvent.Post event) {
            // 十字マークを表示するタイミングで描画する (色が反転する描画になる)
            if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
                return;

            // 機能が有効であるかチェックする
            if (!Bindings.IsEnabled)
                return;

            // ウィンドウのサイズ
            ScaledResolution window = event.getResolution();

            // 位置の計算
            float margin = 2.0f;
            float aspect = 16.0f / 9.0f;
            float alpha = 0.5f;

            // アスペクト計算
            float width = window.getScaledWidth();
            float height = window.getScaledHeight();
            float minWidth = width;
            float minHeight = height;
            if ((width * aspect) / height < 1)
                minHeight = width * aspect;
            else
                minWidth = height / aspect;

            // 描画設定する
            GlStateManager.disableTexture2D();
            GlStateManager.glLineWidth(1);

            // 四角形を描画する
            Tessellator t = Tessellator.getInstance();
            BufferBuilder b = t.getBuffer();
            b.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            b.pos(width / 2 - (minWidth / 2 + margin), height / 2 - (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(width / 2 - (minWidth / 2 + margin), height / 2 + (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(width / 2 + (minWidth / 2 + margin), height / 2 + (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(width / 2 + (minWidth / 2 + margin), height / 2 - (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            t.draw();

            // 描画設定を戻す
            GlStateManager.enableTexture2D();
        }
    }
}
