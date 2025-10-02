# SlimefunTimeit

SlimefunTimeit is a plugin for monitoring and visualizing Slimefun machine performance. It measures the execution time of the appointed Slimefun machine block and displays performance data through holographic display.

## Features

- 📊 **Real-time Performance Monitoring** - Monitor execution time of each Slimefun machine block
- 📈 **Multi-dimensional Data Display** - Show minimum (min), average (avg), maximum (max), and current (cur) execution times
- 🎯 **Visual Interface** - Provides TimeitVisualizer block for intuitive viewing of machine performance data
- 🧹 **Cache Clearing** - Support for clearing monitoring data cache in TimeitVisualizer GUI
- 🎨 **Holographic Display** - Uses dual-line holographic display technology to show performance data

## Requirements

- Minecraft 1.17+
- Spigot/Paper server

## Installation

1. Download the `SlimefunTimeit.jar` file
2. Place the plugin file in the server's `plugins` directory
3. Restart the server
4. The plugin will automatically register the `Timeit Visualizer` item to Slimefun

## Usage

### Timeit Visualizer

1. **Get Item** - Find the "Timeit Visualizer" item (glass material) in Slimefun
2. **Place Block** - Place the Timeit Visualizer near the machine you want to monitor
3. **Configure Monitoring** - Right-click the TimeitVisualizer to open the GUI and select the direction of the machine to monitor
4. **View Data** - Holographic performance data will be displayed above the block

### Data Explanation

The holographic display contains two lines of data:
- First line: Metric names (min/avg/max/cur)
- Second line: Corresponding time values (unit: milliseconds)

Color meanings:
- 🟩 **Green(min)** - Minimum execution time
- 🟨 **Yellow(avg)** - Average execution time
- 🟥 **Red(max)** - Maximum execution time
- 🟦 **Blue(cur)** - Current execution time

## License

This project is licensed under the MIT License.
