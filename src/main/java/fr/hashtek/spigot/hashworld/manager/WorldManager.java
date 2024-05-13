package fr.hashtek.spigot.hashworld.manager;

import fr.hashtek.hashlogger.HashLoggable;
import fr.hashtek.hashlogger.HashLogger;
import fr.hashtek.spigot.hashworld.HashWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.*;

public class WorldManager implements HashLoggable
{

    /**
     * Unload a world.
     *
     * @param worldName The world's name to unload.
     * @param save {@code true} to save the world, {@code false} otherwise.
     * @return {@code true} if the world has been unloaded, {@code false} otherwise.
     */
    public static boolean unloadWorld(String worldName, boolean save)
    {
        return Bukkit.unloadWorld(worldName, save);
    }

    /**
     * Reload a world.
     *
     * @param worldName The world's name to unload.
     * @param save {@code true} to save the world, {@code false} otherwise.
     * @return The new {@link World} instance of the world.
     */
    public static World reloadWorld(String worldName, boolean save)
    {
        WorldManager.unloadWorld(worldName, save);
        return WorldManager.loadWorld(worldName);
    }

    /**
     * Load a world.
     *
     * @param worldName The world's name to load.
     * @return The new {@link World} instance of the loaded world.
     */
    public static World loadWorld(String worldName)
    {
        return Bukkit.createWorld(new WorldCreator(worldName));
    }

    /**
     * Duplicate and load the duplication of a world.
     *
     * @param sourceWorldName The source world name to duplicate.
     * @param newWorldName The destination world name.
     * @return The {@link World} instance of the duplicated world.
     */
    public static World duplicateAndLoadWorld(String sourceWorldName, String newWorldName) {
        HashWorld plugin = HashWorld.getInstance();
        HashLogger logger = plugin.getHashLogger();
        Path sourceWorldPath = Paths.get(Bukkit.getWorldContainer().getPath(), sourceWorldName);
        Path newWorldPath = Paths.get(Bukkit.getWorldContainer().getPath(), newWorldName);

        try {
            Files.createDirectories(newWorldPath);
        } catch (IOException e) {
            logger.error(plugin, "An error occurred while creating folder \"" + newWorldPath + "\": ", e);
            return null;
        }

        if (!WorldManager.copyDirectory(plugin, logger, sourceWorldPath, newWorldPath))
            return null;

        World world = WorldManager.loadWorld(newWorldName);

        if (world == null) {
            logger.warning(plugin, "Failed to load the world: " + newWorldName);
            return null;
        }
        return world;
    }

    /**
     * Copy a directory.
     *
     * @param plugin The plugin.
     * @param logger The {@link HashLogger} instance.
     * @param sourceWorldPath The source world path to copy.
     * @param newWorldPath The destination world path.
     * @return {@code true} if the directory has been successfully copied, {@code false} otherwise.
     */
    private static boolean copyDirectory(HashWorld plugin, HashLogger logger, Path sourceWorldPath, Path newWorldPath)
    {
        try {
            Files.walk(sourceWorldPath).forEach(source -> {
                ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
                Path destination = newWorldPath.resolve(sourceWorldPath.relativize(source));

                try {
                    if (!destination.toFile().exists() && !ignore.contains(destination.getFileName().toString()))
                        Files.copy(source, destination, REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.error(plugin, "An error occurred while copying file from \"" + source + "\" to \"" + destination + "\": ", e);
                }
            });
        } catch (IOException e) {
            logger.error(plugin, "An error occurred while listing files from \"" + sourceWorldPath + "\": ", e);
            return false;
        }
        return true;
    }

}
