package com.balugaq.sftimeit;

import org.bukkit.Location;

@FunctionalInterface
public interface MonitorEndAction {
    void onEnd(Location location, long timeNanos);
}
