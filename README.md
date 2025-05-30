# brackets

![Build](https://github.com/j-d-ha/brackets/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/25793.svg)](https://plugins.jetbrains.com/plugin/25793)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25793.svg)](https://plugins.jetbrains.com/plugin/25793)

## About The Project

<!-- Plugin description -->
`brackets` is a JetBrains plugin to provide matching bracket pair colorization in your code,
just like VSCode does. The plugin supports three levels of colorization with default values set to
match VSCode.

`brackets` supports the following languages:

- C#
- F#

> Note: As of now, I don't plan on adding support for any other languages.

## Settings

Plugin settings can be found in <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>
Brackets</kbd>.

Bellow is a list available settings:

- Matching bracket pair colorization can be disabled for large files. This functionality and the
  line
  count threshold for large files can be updated in settings.

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for
- "brackets"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/25793) and install it
  by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download
  the [latest release](https://plugins.jetbrains.com/plugin/25793/versions) from JetBrains
  Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from
  disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/j-d-ha/brackets/releases/latest) and install it
  manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from
  disk...</kbd>

## Usage

This plugin will work right out of the box with no configuration required. If you would like to
change the colors of the bracket pairs, you can do so in the plugin settings under found under <kbd>
Settings/Preferences</kbd> > <kbd>Editor</kbd> > <kbd>Color Scheme</kbd> > <kbd>Brackets</kbd>.

## Disclaimer

This plugin is developed primarily for my own personal use and I make no promise to support it in
the future, add additional features, fix bugs, etc. With that being said, if you do discover a bug,
please [create an issue](https://github.com/j-d-ha/brackets/issues/new). Also, if you would like to
contribute to the project, you can do so by opening a pull request on GitHub.

## Acknowledgements

This project is based on the lite and open-source version of
the [intellij-rainbow-brackets](https://github.com/izhangzhihao/intellij-rainbow-brackets) JetBrains
plugin, originally authored by [Zhihao Zhang](https://github.com/izhangzhihao) and licensed under
the GPL-3.0.

This plugin is also inspired by the VSCode
plugin [Bracket-Pair-Colorizer-2](https://github.com/CoenraadS/Bracket-Pair-Colorizer-2)
by [CoenraadS](https://github.com/CoenraadS),
which
originally provided similar functionality to VSCode.

Finally, this plugin is also based on
the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
