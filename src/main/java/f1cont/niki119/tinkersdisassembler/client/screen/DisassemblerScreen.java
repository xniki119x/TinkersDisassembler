package f1cont.niki119.tinkersdisassembler.client.screen;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import f1cont.niki119.tinkersdisassembler.common.DisassemblerLogic;
import f1cont.niki119.tinkersdisassembler.common.TinkersDisassembler;
import f1cont.niki119.tinkersdisassembler.common.menu.DisassemblerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Экран Разборщика. Разметка соответствует textures/gui/disassembler_gui.png:
 * слот инструмента (75,9) 26x26, сетка предпросмотра 9x3 от (7,83) с шагом 18,
 * кнопка (34,141) 108x19; состояния кнопки и значки опыта лежат в той же текстуре.
 */
public class DisassemblerScreen extends AbstractContainerScreen<DisassemblerMenu> {

    private static final ResourceLocation TEXTURE = TinkersDisassembler.prefix("textures/gui/disassembler_gui.png");

    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 3;
    private static final int GRID_X = 7;
    private static final int GRID_Y = 83;
    private static final int CELL = 18;

    private static final int BUTTON_X = 34;
    private static final int BUTTON_Y = 141;
    private static final int BUTTON_WIDTH = 108;
    private static final int BUTTON_HEIGHT = 19;
    private static final int BUTTON_ENABLED_V = 166;
    private static final int BUTTON_DISABLED_V = 185;
    private static final int BUTTON_HOVER_V = 204;

    private static final int ICON_U = 3;
    private static final int ICON_V_OK = 226;
    private static final int ICON_V_FAIL = 243;
    private static final int ICON_SIZE = 9;
    // тёмная плашка под слотом инструмента: (65,38) 47x15, внутренняя заливка (66..110, 39..51)
    private static final int ICON_X = 67;
    private static final int ICON_Y = 41;
    private static final int XP_TEXT_X = 78;
    private static final int XP_TEXT_Y = 42;
    private static final int XP_TEXT_MAX_WIDTH = 32;

    private final DisassembleButton disassembleButton;
    private List<ItemStack> preview = Collections.emptyList();
    private ItemStack lastTool = ItemStack.EMPTY;

    public DisassemblerScreen(DisassemblerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.disassembleButton = new DisassembleButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        this.disassembleButton.x = this.leftPos + BUTTON_X;
        this.disassembleButton.y = this.topPos + BUTTON_Y;
        this.addRenderableWidget(this.disassembleButton);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        ItemStack tool = this.menu.getSlot(0).getItem();
        if (!ItemStack.matches(tool, this.lastTool)) {
            this.lastTool = tool.copy();
            this.preview = tool.isEmpty() || this.minecraft == null || this.minecraft.level == null
                    ? Collections.emptyList()
                    : DisassemblerLogic.computeResult(this.minecraft.level, tool);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.disassembleButton.active = this.menu.isEnoughXP();
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderGhostItems(poseStack);
        renderXpInfo(poseStack);
        renderGhostTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        bindTexture();
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        // только заголовок: подпись "Инвентарь" в этом GUI не нужна
        this.font.drawShadow(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 0x404040);
    }

    private static void bindTexture() {
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    /** Полупрозрачный предпросмотр результатов разбора в ячейках сетки. */
    private void renderGhostItems(PoseStack poseStack) {
        if (this.preview.isEmpty()) {
            return;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.45F);
        int max = GRID_COLS * GRID_ROWS;
        for (int i = 0; i < this.preview.size() && i < max; i++) {
            ItemStack stack = this.preview.get(i);
            int x = this.leftPos + GRID_X + (i % GRID_COLS) * CELL + 1;
            int y = this.topPos + GRID_Y + (i / GRID_COLS) * CELL + 1;
            this.itemRenderer.renderGuiItem(stack, x, y);
            if (stack.getCount() > 1) {
                // ванильные декорации (число, полоска прочности): рисуются с правильной z-глубиной,
                // ручной drawShadow проваливается за приподнятый рендер предмета
                this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y, null);
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderXpInfo(PoseStack poseStack) {
        boolean enough = this.menu.isEnoughXP();
        bindTexture();
        blit(poseStack, this.leftPos + ICON_X, this.topPos + ICON_Y, ICON_U, enough ? ICON_V_OK : ICON_V_FAIL,
                ICON_SIZE, ICON_SIZE);
        Component text = new TranslatableComponent("gui.tinkersdisassembler.xp_cost", this.menu.getXPCost());
        int color = enough ? 0x55FF55 : 0xFF5555;
        float x = this.leftPos + XP_TEXT_X;
        float y = this.topPos + XP_TEXT_Y;
        float width = this.font.width(text);
        if (width > XP_TEXT_MAX_WIDTH) {
            // уменьшаем текст, чтобы он уложился в плашку
            float scale = XP_TEXT_MAX_WIDTH / width;
            poseStack.pushPose();
            poseStack.translate(x, y, 0.0F);
            poseStack.scale(scale, scale, 1.0F);
            this.font.drawShadow(poseStack, text, 0.0F, 0.0F, color);
            poseStack.popPose();
        } else {
            this.font.drawShadow(poseStack, text, x, y, color);
        }
    }

    private void renderGhostTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        int relX = mouseX - this.leftPos - GRID_X;
        int relY = mouseY - this.topPos - GRID_Y;
        if (relX < 0 || relY < 0) {
            return;
        }
        int col = relX / CELL;
        int row = relY / CELL;
        if (col >= GRID_COLS || row >= GRID_ROWS) {
            return;
        }
        int index = row * GRID_COLS + col;
        if (index < this.preview.size()) {
            this.renderTooltip(poseStack, this.preview.get(index), mouseX, mouseY);
        }
    }

    private class DisassembleButton extends AbstractButton {

        private DisassembleButton(int x, int y, int width, int height) {
            super(x, y, width, height, new TranslatableComponent("gui.tinkersdisassembler.disassemble"));
        }

        @Override
        public void onPress() {
            Minecraft.getInstance().getSoundManager()
                    .play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (minecraft != null && minecraft.gameMode != null && menu.containerId >= 0) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, DisassemblerMenu.DISASSEMBLE_BUTTON);
            }
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            int v = !this.active ? BUTTON_DISABLED_V : (this.isHoveredOrFocused() ? BUTTON_HOVER_V : BUTTON_ENABLED_V);
            bindTexture();
            blit(poseStack, this.x, this.y, 0, v, this.width, this.height);
            drawCenteredString(poseStack, minecraft.font, this.getMessage(),
                    this.x + this.width / 2, this.y + (this.height - 8) / 2, this.active ? 0xFFFFFF : 0xA0A0A0);
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, this.getMessage());
        }
    }
}
