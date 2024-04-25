package fr.hashtek.spigot.hashworld.loader;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

import fr.hashtek.spigot.hashworld.files.FileCloner;

/**
 * A class that allow you to load and unload worlds.
 */
public class WorldLoader
{

    /**
     * Load a world.
     *
     * @param worldName The world to load.
     * @return The object of the world loaded, or {@code null} if it failed.
     */
    public static World loadWorld(String worldName)
    {
        World world = Bukkit.getWorld(worldName);

        if (world == null)
            world = Bukkit.createWorld(new WorldCreator(worldName));
        return world;
    }

    /**
     * Unload a world.
     *
     * @param worldName The world to unload.
     * @param save {@code true} if you want to save the world, {@code false} otherwise.
     * @return {@code true} if the world successfully unloaded, {@code false} otherwise.
     */
    public static boolean unloadWorld(String worldName, boolean save)
    {
        return Bukkit.unloadWorld(worldName, save);
    }

    /**
     * Reload a world.
     *
     * @param worldName The name of the world to reload.
     * @return The new world that has been reloaded.
     */
    public static World reloadWorld(String worldName, boolean save)
    {
        WorldLoader.unloadWorld(worldName, false);
        return WorldLoader.loadWorld(worldName);
    }

    /**
     * Copy a Bukkit World and load it.
     *
     * @param originalWorld The original world to copy.
     * @param newWorldName The new world name.
     * @throws IOException If the copy of the world failed.
     * @return The clone of the original world.
     */
    public static World cloneWorld(World originalWorld, String newWorldName)
        throws IOException
    {
        File copiedFile = new File(Bukkit.getWorldContainer(), newWorldName);

        FileCloner.copyWorldFolder(originalWorld.getWorldFolder(), copiedFile);
        return Bukkit.createWorld(new WorldCreator(newWorldName));
    }

}
