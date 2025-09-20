package com.auto_farming.config.clothconfigextensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.Element;

public class ButtonEntry extends AbstractConfigListEntry<Void> {
    private final ButtonWidget button;
    private final List<ClickableWidget> widgets;
    private boolean dirty = false;

    public ButtonEntry(Text label, Runnable onPress) {
        super(label, false);
        this.button = ButtonWidget.builder(label, b -> {
            onPress.run();
            dirty = true;
        })
                .dimensions(0, 0, 100, 20).build();

        widgets = new ArrayList<>(List.of(button));
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
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
        return Optional.empty();
    }

    @Override
    public boolean isEdited() {
        return dirty;
    }

    @Override
    public List<? extends Selectable> narratables() {
        return widgets;
    }
}
