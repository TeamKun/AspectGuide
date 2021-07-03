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

/**
 * AspectGuide Modクラス
 */
@Mod("aspectguide")
public class AspectGuide {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * コンストラクタ
     * イベントを追加する
     */
    public AspectGuide() {
        // Modイベント登録
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Forgeイベント登録
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * クライアントの初期化処理
     *
     * @param event クライアント初期化イベント
     */
    private void doClientStuff(FMLClientSetupEvent event) {
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
        private static final KeyBinding TOGGLE = new KeyBinding("key.aspectguide.toggle", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_F4, "key.categories.ui");
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

            // 行列を取得
            MatrixStack matrixStack = event.getMatrixStack();
            Matrix4f matrix = matrixStack.getLast().getMatrix();
            // ウィンドウのサイズ
            MainWindow window = event.getWindow();

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
            RenderSystem.disableTexture();
            RenderSystem.lineWidth(1);

            // 四角形を描画する
            Tessellator t = Tessellator.getInstance();
            BufferBuilder b = t.getBuffer();
            b.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            b.pos(matrix, width / 2 - (minWidth / 2 + margin), height / 2 - (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(matrix, width / 2 - (minWidth / 2 + margin), height / 2 + (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(matrix, width / 2 + (minWidth / 2 + margin), height / 2 + (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            b.pos(matrix, width / 2 + (minWidth / 2 + margin), height / 2 - (minHeight / 2 + margin), 0).color(1, 1, 1, alpha).endVertex();
            t.draw();

            // 描画設定を戻す
            RenderSystem.enableTexture();
        }
    }
}
