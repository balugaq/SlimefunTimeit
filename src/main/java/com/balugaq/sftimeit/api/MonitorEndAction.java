package com.balugaq.sftimeit.api;

import org.bukkit.Location;

@FunctionalInterface
public interface MonitorEndAction {
    void onEnd(Location location, long timeNanos);
}
