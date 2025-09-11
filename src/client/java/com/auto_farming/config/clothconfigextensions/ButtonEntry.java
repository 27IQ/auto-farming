package com.auto_farming.config.clothconfigextensions;

import java.util.List;
import java.util.Optional;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.Element;

public class ButtonEntry extends AbstractConfigListEntry<Void> {
    private final ButtonWidget button;

    public ButtonEntry(Text label, Runnable onPress) {
        super(label, false);
        this.button = ButtonWidget.builder(label, b -> onPress.run())
                .dimensions(0, 0, 100, 20).build();
    }

    @Override
    public List<? extends Element> children() {
        return List.of(button);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x,
            int entryWidth, int entryHeight, int mouseX,
            int mouseY, boolean hovered, float tickDelta) {
        button.setX(x);
        button.setY(y);
        button.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public Optional<Void> getDefaultValue() {
        throw new UnsupportedOperationException("Unimplemented method 'getDefaultValue'");
    }

    @Override
    public List<? extends Selectable> narratables() {
        throw new UnsupportedOperationException("Unimplemented method 'narratables'");
    }
}
