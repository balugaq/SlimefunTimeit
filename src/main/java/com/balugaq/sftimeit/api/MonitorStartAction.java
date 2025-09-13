package com.balugaq.sftimeit.api;

import org.bukkit.Location;

@FunctionalInterface
public interface MonitorStartAction {
    void onStart(Location location);
}
