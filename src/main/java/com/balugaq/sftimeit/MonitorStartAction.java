package com.balugaq.sftimeit;

import org.bukkit.Location;

@FunctionalInterface
public interface MonitorStartAction {
    void onStart(Location location);
}
