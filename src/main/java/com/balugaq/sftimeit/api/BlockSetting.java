package com.balugaq.sftimeit.api;

import lombok.Data;
import org.jspecify.annotations.NullMarked;

@NullMarked
public @Data class BlockSetting {
    public long lastTickedAt = 0;
    public long timingNanosMin = Long.MAX_VALUE;
    public long timingNanosAverage = 0;
    public long timingNanosMax = Long.MIN_VALUE;
    public long tickedTimes = 0;
    public MonitorStartAction startAction = location -> {
    };
    public MonitorEndAction endAction = (location, timeNanos) -> {
    };
}
