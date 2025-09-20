package com.auto_farming.config.clothconfigextensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class DirtyFlag extends AbstractConfigListEntry<Void> {

    private boolean isDirty;
    private List<ClickableWidget> widgets;

    public DirtyFlag(boolean isDirty) {
        super(Text.of(""), false);
        this.isDirty = isDirty;

        widgets = new ArrayList<>();
    }

    @Override
    public void render(DrawContext context, int index, int y, int x,
            int entryWidth, int entryHeight,
            int mouseX, int mouseY, boolean hovered, float delta) {
    }

    @Override
    public boolean isEdited() {
        return isDirty;
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
    }

    @Override
    public Optional<Void> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public List<? extends Selectable> narratables() {
        return widgets;
    }
}
