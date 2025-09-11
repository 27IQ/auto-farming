package com.auto_farming.config.clothconfigextensions;

import java.util.function.Consumer;
import java.util.function.Supplier;

import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class WideStringListEntry extends TextFieldListEntry<String> {

    private boolean first = true;

    public WideStringListEntry(String value,
            Supplier<String> defaultValue, Consumer<String> saveConsumer) {

        super(Text.of(""), value, Text.of("Reset"), defaultValue, null, false);
        this.saveCallback = saveConsumer;

    }

    @Override
    public void render(DrawContext context, int index, int y, int x,
            int entryWidth, int entryHeight,
            int mouseX, int mouseY, boolean hovered, float delta) {

        this.textFieldWidget.setX(x);
        this.textFieldWidget.setY(y);
        this.textFieldWidget.setWidth(entryWidth);

        if (first) {
            this.textFieldWidget.setCursorToEnd(false);
            this.textFieldWidget.setCursorToStart(false);
            first = false;
        }

        this.textFieldWidget.render(context, mouseX, mouseY, delta);
    }

    @Override
    public String getValue() {
        return this.textFieldWidget.getText();
    }
}
