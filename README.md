# Day Counter

A lightweight and customizable Fabric client-side HUD mod that displays the current Minecraft day directly on your screen.

---

## Features

* Displays the current in-game day in real time
* Lightweight and client-side only
* Draggable HUD positioning
* Position automatically saves between sessions
* Resolution-independent positioning using ratio-based coordinates
* Minimal and clean design
* Works in singleplayer and multiplayer

---

## Screenshots

### HUD Display

![HUD Demo](images/HUD%20Demo.png)

### HUD Editor

![Edit Demo](images/Edit%20Demo.png)

---

## Controls

| Action          | Default Key |
| --------------- | ----------- |
| Open HUD Editor |      M      |

The HUD editor allows you to drag the Day Counter anywhere on your screen.

---

## Installation

### Requirements

* Minecraft Fabric
* Fabric API

### Steps

1. Install Fabric Loader
2. Install Fabric API
3. Place the mod `.jar` into your `mods` folder
4. Launch Minecraft

---

## Compatibility

| Minecraft Version | Status    |
| ----------------- | --------- |
| 1.21.11           | Supported |
| 26.1.x            | Supported |

---

## Technical Notes

The HUD position uses ratio-based scaling rather than fixed pixel coordinates. This helps maintain proper placement across:

* Different monitor resolutions
* Ultrawide displays
* Window resizing
* Fullscreen changes
* UI scale adjustments

The mod is designed to remain extremely lightweight with minimal per-tick overhead.

---

## Planned Features

* Configurable text color
* Scale adjustment
* Optional shadow/background
* Toggleable formatting styles
* Anchor support
* Additional HUD customization options

---

## License

MIT License

You are free to use, modify, and distribute this project under the terms of the MIT license.

---

## Support

If you encounter bugs or have suggestions:

* Open an issue on GitHub

---

## Downloads

* Modrinth: [Add link here](https://modrinth.com/mod/day-counter-overlay)

---

## Development

Built using:

* Fabric Loader
* Fabric API
* Java
* IntelliJ IDEA

---

## Why Day Counter?

Minecraft tracks time internally, but the vanilla game does not provide a simple persistent day display on-screen.

Day Counter was designed to solve that with a clean, lightweight HUD that feels native to the game without cluttering the interface.
