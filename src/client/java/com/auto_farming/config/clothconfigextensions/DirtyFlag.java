package com.auto_farming.config.clothconfigextensions;

import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class DirtyFlag extends TextFieldListEntry<String> {

    private boolean isDirty;

    public DirtyFlag(boolean isDirty) {
        super(Text.of(""), "", Text.of("Reset"), ()->"", null, false);
        this.isDirty=isDirty;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x,
            int entryWidth, int entryHeight,
            int mouseX, int mouseY, boolean hovered, float delta) {}

    @Override
    public String getValue() {
        return this.textFieldWidget.getText();
    }

    @Override
    public boolean isEdited() {
        return isDirty;
    }
}
