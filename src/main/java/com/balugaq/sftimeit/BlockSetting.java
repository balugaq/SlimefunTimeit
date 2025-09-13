package com.balugaq.sftimeit;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class BlockSetting {
    public long lastTickedAt = 0;
    public long timingNanosMin = Long.MAX_VALUE;
    public long timingNanosAverage = 0;
    public long timingNanosMax = Long.MIN_VALUE;
    public long tickedTimes = 0;
    public MonitorStartAction startAction = location -> {};
    public MonitorEndAction endAction = (location, timeNanos) -> {};
}
