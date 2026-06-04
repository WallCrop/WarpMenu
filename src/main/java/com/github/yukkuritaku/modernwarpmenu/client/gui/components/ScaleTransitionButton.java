package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import com.github.yukkuritaku.modernwarpmenu.client.gui.BlitExtension;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.GridRectangle;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.transition.ScaleTransition;
import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class ScaleTransitionButton extends CustomContainerButton{
    private static final float HOVERED_BRIGHTNESS = 1f;
    private static final float UN_HOVERED_BRIGHTNESS = 0.9F;

    /** This rectangle determines the button's placement on its {@code GuiScreen}'s {@code ScaledGrid} */
    protected GridRectangle buttonRectangle;
    protected ScaleTransition transition;
    protected float scaledXPosition;
    protected float scaledYPosition;
    protected float scaledWidth;
    protected float scaledHeight;

    public ScaleTransitionButton(int x, int y, int width, int height, Component message, LayoutTexture backgroundTexture, LayoutTexture foregroundTexture, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, backgroundTexture, foregroundTexture, onPress, createNarration);
    }

    /**
     * Button hover calculations adapted for float values instead of int
     */
    public void calculateHoverState(int mouseX, int mouseY) {
        this.isHovered =
                mouseX >= this.scaledXPosition &&
                        mouseY >= this.scaledYPosition &&
                        mouseX <= this.scaledXPosition + this.scaledWidth &&
                        mouseY <= this.scaledYPosition + this.scaledHeight;
    }

    /**
     * Recalculates the progress of {@code transition} towards its end time and reverses the direction of transition if
     * this button's hover state changes
     *
     * @param scaleTransitionDuration duration from transition start to finish
     * @param hoveredScale final scale when the transition when the button is hovered is finished
     */
    public void transitionStep(long scaleTransitionDuration, float hoveredScale) {
        this.transition.step();
        if (this.isHoveredOrFocused()) {
            if (this.transition.getEndScale() == 1) {
                this.transition = new ScaleTransition((long) (this.transition.getProgress() * scaleTransitionDuration), this.transition.getCurrentScale(), hoveredScale);
            }
        } else {
            if (this.transition.getEndScale() == hoveredScale) {
                this.transition = new ScaleTransition((long) (this.transition.getProgress() * scaleTransitionDuration), this.transition.getCurrentScale(), 1);
            }
        }
    }

    /**
     * Draw a border around this button with the given color. This stutters due to using int instead of float.
     *
     * @param color color of the border
     */
    public void renderBorder(GuiGraphicsExtractor graphics, int color) {
        Matrix3x2fStack stack = graphics.pose();
        stack.pushMatrix();
        stack.translate(0, 0); // z is ignored in 2D
        graphics.horizontalLine((int) this.scaledXPosition, (int) (this.scaledXPosition + this.scaledWidth), (int) this.scaledYPosition, color);
        graphics.verticalLine((int) this.scaledXPosition, (int) this.scaledYPosition, (int) (this.scaledYPosition + this.scaledHeight), color);
        graphics.horizontalLine((int) this.scaledXPosition, (int) (this.scaledXPosition + this.scaledWidth), (int) (this.scaledYPosition + this.scaledHeight), color);
        graphics.verticalLine((int) (this.scaledXPosition + this.scaledWidth), (int) this.scaledYPosition, (int) (this.scaledYPosition + this.scaledHeight), color);
        stack.popMatrix();
    }
    /**
     * Draws the provided texture at ({@code this.scaledXPosition}, {@code this.scaledYPosition}, {@code this.zLevel}) at a size of ({@code this.scaledWidth})x({@code this.scaledHeight})
     *
     * @param texture location of texture to draw
     */
    protected void renderButtonTexture(GuiGraphicsExtractor guiGraphics, Identifier texture) {
        int color;
        if (this.isHoveredOrFocused()) {
            color = new Color(HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, 1f).getRGB();
        } else {
            color = new Color(UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, 1f).getRGB();
        }

        // Draw the texture using the new pipeline
        BlitExtension.blit(guiGraphics,
                net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                texture,
                this.scaledXPosition,
                this.scaledYPosition,
                0.0F, 0.0F, // u, v
                this.scaledWidth,
                this.scaledHeight,
                this.scaledWidth,
                this.scaledHeight,
                color
        );
    }


    /**
     * Draws the display string for this button aligned to centre. The centre of the string is given by {@code xOffset}
     * and {@code yOffset} relative to its top-left corner. The offsets should be pre-scaled. This method does not scale
     * the offsets.
     *
     * @param xOffset x-offset from button left
     * @param yOffset y-offset from button top
     */
    public void renderMessageString(GuiGraphicsExtractor graphics, float xOffset, float yOffset, Color textColor) {

        String[] lines = this.getMessage().getString().split("\n");
        Matrix3x2fStack stack = graphics.pose();
        Color color;
        if (this.isHoveredOrFocused()){
            color = new Color((int) (textColor.getRed() * HOVERED_BRIGHTNESS),
                    (int) (textColor.getGreen() * HOVERED_BRIGHTNESS),
                    (int) (textColor.getBlue() * HOVERED_BRIGHTNESS), 255);
        }else {
            color = new Color(
                    (int) (textColor.getRed() * UN_HOVERED_BRIGHTNESS),
                    (int) (textColor.getGreen() * UN_HOVERED_BRIGHTNESS),
                    (int) (textColor.getBlue() * UN_HOVERED_BRIGHTNESS), 255);
        }
        stack.pushMatrix();
        stack.translate(this.scaledXPosition + xOffset, this.scaledYPosition + yOffset);
        stack.scale(this.transition.getCurrentScale(), this.transition.getCurrentScale());
        for (int i = 0; i < lines.length; i++) {
            graphics.centeredText(Minecraft.getInstance().font, lines[i], 0, Minecraft.getInstance().font.lineHeight * i, color.getRGB());
        }
        stack.popMatrix();
    }

    protected void renderForegroundLayer(GuiGraphicsExtractor guiGraphics, Identifier foregroundTexture){
        if (foregroundTexture != null)
            renderButtonTexture(guiGraphics, foregroundTexture);
    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            this.extractContents(guiGraphics, mouseX, mouseY, partialTick);
            this.tooltip.refreshTooltipForNextRenderPass(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    this.isHovered(),
                    this.isFocused(),
                    this.getRectangle()
            );
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible){
            this.buttonRectangle.scale(this.transition.getCurrentScale());
            this.scaledXPosition = this.buttonRectangle.getXPosition();
            this.scaledYPosition = this.buttonRectangle.getYPosition();
            this.scaledWidth = this.buttonRectangle.getWidth();
            this.scaledHeight = this.buttonRectangle.getHeight();
            renderButtonTexture(graphics, this.backgroundTexture.location());
            if (SettingsManager.get().debug.debugModeEnabled && SettingsManager.get().debug.drawBorders) {
                renderBorder(graphics, ARGB.white(1.0f));
            }
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible &&
                mouseX >= this.scaledXPosition &&
                mouseY >= this.scaledYPosition &&
                mouseX <= this.scaledXPosition + this.scaledWidth &&
                mouseY <= this.scaledYPosition + this.scaledHeight;
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return !this.isHovered ? super.nextFocusPath(event) : null;
    }
}