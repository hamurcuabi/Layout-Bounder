# Layout Bounder

This plugin simplifies the usage of layout debug mode's.
\
![Layout Bounder](https://user-images.githubusercontent.com/23655824/233925249-1edf1757-e054-4459-8ef5-1820fd6d16f6.gif)  

## Usage
1. Connect an Android device.
2. Open the "Layout Bounder" Tool Window (in the right-bottom corner).
3. Click the "Bound / Un Bound" button.
4. Now you should see debug options enabled or disabled.

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Layout Bounder"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/hamurcuabi/Layout-Bounder/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## How to run code
Open project in Intellij IDEA.

Available gradle tasks:
* `runIde` - launches a new instance of IDE with the plugin installed:
  * Execute `./gradlew runIde` command in terminal  
OR
  * Press `Ctrl` twice to open the Run Anything window and execute `gradle runIde` command
* `check` - runs linters and tests
* `buildPlugin` packages installable zip file  
  Distribution zip file will be available under `./build/distributions/`

You can choose which version of IDE `runIde` task launches by adding `runIde` configuration `./build.gradle.kts`:  
```
tasks {
    ...

    runIde {
        ideDir.set(file("/Applications/Android Studio.app/Contents"))
    }
}
```
