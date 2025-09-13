package com.balugaq.sftimeit;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

@NullMarked
public class Monitor {
    private static final Object2ObjectOpenHashMap<Location, BlockSetting> blockSettings = new Object2ObjectOpenHashMap<>();

    public BlockSetting getData(Location location) {
        if (!blockSettings.containsKey(location)) blockSettings.put(location, new BlockSetting());
        return blockSettings.get(location);
    }

    @CanIgnoreReturnValue
    public BlockSetting editData(Location location, Consumer<BlockSetting> consumer) {
        BlockSetting data = getData(location);
        consumer.accept(data);
        return data;
    }

    public void onTickStart(Location location) {
        editData(location, data -> {
            data.lastTickedAt = System.nanoTime();
            data.tickedTimes += 1;
        });

    }

    public void onTickEnd(Location location) {
        editData(location, data -> {
            long timeNanos = System.nanoTime() - data.lastTickedAt;
            data.timingNanosMax = Math.max(timeNanos, data.timingNanosMax);
            data.timingNanosMin = Math.min(timeNanos, data.timingNanosMin);
            data.timingNanosAverage = (data.timingNanosAverage * data.tickedTimes + timeNanos) / (data.tickedTimes + 1);
            data.endAction.onEnd(location, timeNanos);
        });
    }

    public void listen(Location location, @Nullable MonitorStartAction startAction, @Nullable MonitorEndAction endAction) {
        if (startAction != null) getData(location).startAction = startAction;
        if (endAction != null) getData(location).endAction = endAction;
    }

    public void unlisten(Location location) {
        getData(location).startAction = l -> {};
        getData(location).endAction = (l, timeNanos) -> {};
    }

    public static void initialize() {
        Field tickerField = Arrays.stream(SlimefunItem.class.getDeclaredFields()).filter(field -> field.getName().equals("blockTicker")).findFirst().get();
        tickerField.setAccessible(true);

        SlimefunTimeit.runTaskLaterAsynchronously(() -> {
            for (SlimefunItem item : Slimefun.getRegistry().getAllSlimefunItems()) {
                BlockTicker ticker = item.getBlockTicker();
                if (ticker != null) {
                    try {
                        tickerField.set(item, MonitoringBlockTicker.warp(ticker));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 1L);
    }
}
