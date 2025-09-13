package com.balugaq.sftimeit;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.block.Block;

import java.util.Optional;

public class MonitoringBlockTicker extends BlockTicker {
    private static final Monitor monitor = SlimefunTimeit.monitor();
    private final BlockTicker ticker;

    public MonitoringBlockTicker(BlockTicker ticker) {
        this.ticker = ticker;
    }

    public static MonitoringBlockTicker warp(BlockTicker ticker) {
        return new MonitoringBlockTicker(ticker);
    }

    public BlockTicker originTicker() {
        return ticker;
    }

    @Override
    public void update() {
        ticker.update();
    }

    @Override
    public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        return ticker.validate(item);
    }

    @Override
    public boolean isSynchronized() {
        return ticker.isSynchronized();
    }

    @Override
    public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        monitor.onTickStart(b.getLocation());
        ticker.tick(b, item, data);
        monitor.onTickEnd(b.getLocation());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(Block b, SlimefunItem item, Config data) {
        monitor.onTickStart(b.getLocation());
        ticker.tick(b, item, data);
        monitor.onTickEnd(b.getLocation());
    }

    @Override
    public void uniqueTick() {
        ticker.uniqueTick();
    }

    public Class<? extends ItemHandler> getIdentifier() {
        return ticker.getIdentifier();
    }

    public void startNewTick() {
        ticker.startNewTick();
    }
}