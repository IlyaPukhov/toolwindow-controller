<div align="center">
    <img src="./src/main/resources/META-INF/pluginIcon.svg" width="160" height="160" alt="logo"/>
</div>

<h1 align="center">🔧 ToolWindow Controller for Intellij-based IDEs</h1> 

**Inspired by the outdated [ToolWindow Manager](https://plugins.jetbrains.com/plugin/1489-toolwindow-manager)**  
**✅ Supported on IntelliJ-based IDEs starting from version 2026.2**

This plugin lets you configure visibility preferences for well-known tool windows like *Bookmarks*, *Notifications*,
etc., and ensures they automatically show or hide depending on the project.

---

## ✨ Features

- Set preferred availability for any tool window per project
- Supports both global and project-level preferences
- Automatically restores visibility states on project open
- Includes "Reset to Defaults" option

## 🧾 Notes

- Version `1.0.0` was written in **Java**
- From version `1.1.0` onward, the plugin is written in **Kotlin**
- **Project-level settings** are stored in: `.idea/toolwindow-controller-settings.xml`
- **Global settings** are stored in:
- On *Windows*: `%APPDATA%\JetBrains\IntelliJIdea<version>\options`
- On *macOS*: `~/Library/Application Support/JetBrains/IntelliJIdea<version>/options`
- On *Linux*: `~/.config/JetBrains/IntelliJIdea<version>/options`

## 🧭 Usage

To access the configuration:

- Open `Tools` → `Tool Window Management` → `Configure Preferred Availabilities`

<p align="center">
  <img src="images/menu.png" alt="Menu screenshot" width="400"/>
</p>

<p align="center">
  <img src="images/preferences.png" alt="Preferences screenshot" width="700"/>
</p>

---

## 🛠️ Installation

### ✅ Method 1: Pre-built Package (Recommended)

1. Download the latest release from
   the [Releases section](https://github.com/IlyaPukhov/toolwindow-controller/releases)
2. Install the plugin in IntelliJ IDEA:
    - Go to `Settings` → `Plugins`
    - Click the ⚙️ icon (gear) → `Install Plugin from Disk...`
    - Select the downloaded `.zip` file
3. Restart the IDE if prompted

### 🧪 Method 2: Build from Source

If you want to build the plugin yourself:

1. Clone the repository:
   ```bash
   git clone https://github.com/IlyaPukhov/toolwindow-controller.git
   cd toolwindow-controller
   ```

2. Open the project in IntelliJ IDEA — it will be automatically recognized as a plugin project

3. Build the plugin using the following command:
   ```bash
   ./gradlew clean buildPlugin
   ```

4. The packaged plugin will be located at:
   ```
   build/distributions/toolwindow-controller-{version}.zip
   ```

5. Install the plugin in IntelliJ IDEA:
    - Go to `Settings` → `Plugins`
    - Click the ⚙️ icon → `Install Plugin from Disk...`
    - Select the generated `.zip` file
6. Restart the IDE if prompted


## Development and availability semantics

- Build with JDK 25: `./gradlew test buildPlugin verifyPluginStructure verifyPlugin`.
- Use an installed IDE for development: `./gradlew test -PidePath="/path/to/idea"`.
- `Show`/`Hide` control tool-window availability, including its stripe button; `Show` does not activate or focus the window.
- `Default` restores the availability observed before this plugin first changed that window in the current project session, and removes its saved override.
- `Restore Default Availabilities` removes all overrides from the selected scope, including windows not currently registered. Restoring availability does not reopen previously visible content.
- Global preferences are shared; applying changes updates the current project. Other projects pick them up on startup, registration of a configured window, or `Reapply Preferred Availabilities`.
- Late tool-window registration is handled by an event listener. There is no polling or continuous enforcement against other plugins changing availability.
- Opening, resetting or cancelling the settings dialog does not save preferences or change tool-window availability.
