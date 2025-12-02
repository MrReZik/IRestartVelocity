# iRestart for Velocity

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Velocity Support](https://img.shields.io/badge/Velocity-3.4.0+-informational)
![Java Version](https://img.shields.io/badge/Java-21-red.svg)
![Build Tool](https://img.shields.io/badge/Built%20with-Gradle-02303A.svg)
[![Author](https://img.shields.io/badge/Author-MrReZik-1abc9c.svg)](https://github.com/MrReZik)

### Description
**iRestart** is a lightweight and reliable automatic restart scheduler designed specifically for **Velocity Proxy**. The plugin allows you to configure a precise time for the proxy shutdown (or command execution) with flexible notifications for all connected players.

### ✨ Key Features
* **Automatic Scheduling:** Set a precise time for the restart action (HH:mm).
* **Timezone Support:** Ensures correct operation based on the server's time, regardless of your location.
* **Flexible Alerts:** Send warnings via **Chat**, **Titles**, and **ActionBars** with a customizable countdown.
* **Operation Modes:** Supports **`RESTART`** (shuts down the proxy, kicking players) and **`RELOAD`** (executes a predefined console command).
* **Pre-Warning Commands:** Execute console commands before the restart (e.g., `/save-all` or clearing PvP tags).

### ⚙️ Installation
1.  Ensure you are using **Velocity Proxy (3.4.0+)** and **Java 21**.
2.  Download the latest JAR file of the plugin.
3.  Place the `iRestart-[version].jar` file into the `plugins` folder on your Velocity server.
4.  Restart the proxy.
5.  Edit the generated configuration file at `plugins/iRestart/config.yml`.

### 🛠️ Commands and Permissions
| Command | Description | Permission |
| :--- | :--- | :--- |
| `/irestart help` | Shows the help menu. | `irestart.admin` |
| `/irestart reload` | Reloads the plugin configuration. | `irestart.admin` |
| `/irestart now` | Immediately executes the restart/action, according to config settings. | `irestart.admin` |
| `/irestart time set <HH:mm>` | Sets the restart time (e.g., `02:00`). | `irestart.admin` |
| `/irestart timezone <ID>` | Sets the timezone (e.g., `Europe/Moscow`). | `irestart.admin` |
| `/irestart type <RESTART/RELOAD>` | Sets the action mode. | `irestart.admin` |
© 2024 MrReZik. Licensed under the MIT License.
