# DynamicData

DynamicData is a library mod used to generate datapack resources at runtime (recipes, loot tables, etc.).
A list of all versions of DynamicData can be found on [Maven](https://maven.matyrobbrt.com/#/releases/com/matyrobbrt/dynamicdata).

## Maven dependency

First add the repository in the `repositories` block:

```gradle
repositories {
    maven {
        name = "Matyrobbrt's maven"
        url = 'https://maven.matyrobbrt.com/releases'
        content {
            includeGroup 'com.matyrobbrt'
        }
    }
}
```

The dependencies for different loaders / gradle plugins can be seen below:

### Forge with ForgeGradle:

```gradle
dependencies {
    implementation fg.deobf("com.matyrobbrt.dynamicdata:dynamicdata-${mc_version}-forge:${dynamicdata_version}")
}
```

### Fabric / Quilt with Loom:

```gradle
dependencies {
    modImplementation "com.matyrobbrt.dynamicdata:dynamicdata-${mc_version}-fabric:${dynamicdata_version}"
}
```

### Multiloader:

Common project:

```gradle
dependencies {
    compileOnly "com.matyrobbrt.dynamicdata:dynamicdata-${mc_version}:${dynamicdata_version}"
}
```

Forge project:

```gradle
dependencies {
    runtimeOnly fg.deobf("com.matyrobbrt.dynamicdata:dynamicdata-${mc_version}-forge:${dynamicdata_version}")
}
```

Fabric project:

```gradle
dependencies {
    modRuntimeOnly "com.matyrobbrt.dynamicdata:dynamicdata-${mc_version}-fabric:${dynamicdata_version}"
}
```
## Usage:
In order to start mutating data at runtime you will register a plugin for your mod, as follows:
```java
import com.matyrobbrt.dynamicdata.api.annotation.RegisterPlugin;

@RegisterPlugin
public class MyModPlugin implements DynamicDataPlugin {
    @Override // This method is invoked when mutator listeners are collected for your plugin. Register any mutations here
    public void collectMutatorListeners(MutatorCollector collector) {
        collector.accept(RecipeMutator.class, mutator -> {}); // Add a mutator for mutating recipes
        collector.accept(AdvancementMutator.class, mutator -> {}); // Add a mutator for mutating advancements
        collector.acceptTagMutator(Registries.ITEM, mutator -> {}); // Add a mutator for mutating item tags
        collector.acceptDatapackRegistryMutator(Registries.BIOME, mutator -> {}); // Add a mutator for ADDING new biomes
    }
}
```
The `@RegisterPlugin` annotation will find your plugin and register it automatically, by creating it using a
no-arg constructor. You may as well use `DynamicDataAPI#registerPlugin` during mod init to do it explicitly.

The `TestPlugin` has examples for most mutators.

## Jar in Jar
DynamicData is not published on Modrinth or CurseForge, so you will need to use Jar-in-Jar to include it.  
The setup differs based on your loader / setup, documentation on how to do it can be found on your loader's documentation.