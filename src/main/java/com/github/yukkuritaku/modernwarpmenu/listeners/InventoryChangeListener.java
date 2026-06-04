package com.github.yukkuritaku.modernwarpmenu.listeners;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public record InventoryChangeListener(Consumer<AbstractContainerMenu> callback) implements ContainerListener {


    @Override
    public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {
        this.callback.accept(container);
    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int id, int value) {
    }
}
