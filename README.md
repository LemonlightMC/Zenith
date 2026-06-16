# Zenith

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/)
[![Gradle](https://img.shields.io/badge/Gradle-8.14.2-02303A)](https://gradle.org/)

> Elevate your Minecraft Server plugins with Zenith – a comprehensive suite of utilities, APIs, and integrations designed for modern Bukkit/Spigot development.

A powerful, modular library that provides developers with essential tools to build feature-rich plugins with performance in mind for Minecraft servers. Zenith streamlines plugin development and enhances server capabilities.

## ✨ Features

- **🛠️ Core Utilities**: Comprehensive collection of helper classes for common tasks
- **🎯 Command Framework**: Robust command handling with argument parsing and validation
- **📦 Item Management**: Advanced item creation, modification, and manipulation tools
- **🔧 Custom Content**: Support for custom items and recipes
- **💾 Database Integration**: Easy-to-use database utilities for data persistence
- **🔗 Plugin Integrations**: Seamless integration with popular plugins like LuckPerms, Vault, and more
- **⚡ Performance Optimized**: Built with efficiency in mind for high-performance servers
- **🔄 Modular Design**: Use only what you need with our modular architecture

<br>

> **Be aware, this Framework is currently in early stages, so expect big changes and improvements!! Nevertheless it is already very developed, highly usable, and we are working hard to make it even better!**

## 📦 Installation

### For Plugin Developers

Add Zenith as a dependency to your Gradle project:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.LemonlightMC:Zenith:0.1.0")
}
```

### Building from Source

1. Clone the repository:

   ```bash
   git clone https://github.com/LemonlightMC/Zenith.git
   cd Zenith
   ```

2. Build with Gradle:

   ```bash
   ./gradlew build
   ```

3. The built JARs will be available in each module's `build/libs/` directory.

## 🚀 Usage

Zenith is designed to be easy to integrate into your existing plugins. Here's a quick example:

```java
import com.lemonlightmc.zenith.items.ItemStackBuilder;
import com.lemonlightmc.zenith.utils.PlayerUtils;

// Create a custom item
ItemStack customItem = new ItemStackBuilder(Material.DIAMOND_SWORD)
    .name("&bLegendary Sword")
    .lore("&7A sword of legendary power")
    .enchant(Enchantment.DAMAGE_ALL, 5)
    .build();

// Give it to a player
PlayerUtils.giveItem(player, customItem);
```

## 📚 Modules

Zenith is organized into several modules, each focusing on specific functionality:

### Core (`:core`)

The foundation module containing essential utilities:

- Event handling and scheduling
- Configuration management
- Data structures and utilities
- Math and string helpers
- Player and world utilities
- Permission helpers

### Commands (`:commands`)

Advanced command framework:

- Command registration and handling
- Argument parsing and validation
- Tab completion support
- Permission checking

### Items (`:items`)

Comprehensive item management:

- ItemStack builders and modifiers
- Enchantment utilities
- Potion effect management
- Banner and firework creation

### Custom (`:custom`)

Custom content creation:

- Custom item definitions
- Recipe creation and management

### Database (`:database`)

Database integration utilities:

- Connection management
- Query builders
- Data serialization

### Integrations (`:integrations`)

Third-party plugin integrations:

- **LuckPerms**: Permission management
- **Vault**: Economy and permission APIs
- **TabAPI**: Tab list management
- **Updater**: Automatic plugin updating

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on how to get started.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- **GitHub Repository**: [LemonlightMC/Zenith](https://github.com/LemonlightMC/Zenith)
- **Issues**: [Report bugs or request features](https://github.com/LemonlightMC/Zenith/issues)
- **Discussions**: [Join the community discussions](https://github.com/LemonlightMC/Zenith/discussions)
- **Wiki**: [Documentation and Guides](https://github.com/LemonlightMC/Zenith/wiki)

## 🙏 Acknowledgments

Zenith builds upon and is inspired by several excellent open-source projects:

- [EcoLib](https://github.com/Auxilor/eco)
- [Lucko Helper](https://github.com/lucko/helper)
- [CommandAPI](https://github.com/CommandAPI/CommandAPI)
- And many more listed in our [sources](todo.md)

---

**Made with ❤️ for the Minecraft community**
