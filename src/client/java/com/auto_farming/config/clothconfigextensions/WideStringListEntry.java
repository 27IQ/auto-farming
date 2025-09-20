package com.auto_farming.config.clothconfigextensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class WideStringListEntry extends AbstractConfigListEntry<String> {

    private boolean first = true;
    private TextFieldWidget textFieldWidget;
    private Supplier<String> defaultValue;
    private List<ClickableWidget> widgets;

    public WideStringListEntry(Supplier<String> defaultValue, Consumer<String> saveConsumer) {

        super(Text.of(""), false);

        this.defaultValue=defaultValue;

        textFieldWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 148, 18, Text.empty());
        textFieldWidget.setMaxLength(999999);
        textFieldWidget.setText(defaultValue.get());

        widgets=new ArrayList<>(List.of(textFieldWidget));

        this.saveCallback = saveConsumer;

    }

    @Override
    public void render(DrawContext context, int index, int y, int x,
            int entryWidth, int entryHeight,
            int mouseX, int mouseY, boolean hovered, float delta) {

        textFieldWidget.setX(x);
        textFieldWidget.setY(y);
        textFieldWidget.setWidth(entryWidth);
        textFieldWidget.setHeight(entryHeight);

        if (first) {
            textFieldWidget.setCursorToEnd(false);
            textFieldWidget.setCursorToStart(false);
            first = false;
        }

        textFieldWidget.render(context, mouseX, mouseY, delta);
    }

    @Override
    public String getValue() {
        return this.textFieldWidget.getText();
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
    }

    @Override
    public Optional<String> getDefaultValue() {
        return defaultValue != null ? Optional.of(defaultValue.get()) : Optional.empty();
    }

    @Override
    public List<? extends Selectable> narratables() {
        return widgets;
    }

    @Override
    public boolean isEdited() {
        return !defaultValue.get().equals(getValue());
    }
}
