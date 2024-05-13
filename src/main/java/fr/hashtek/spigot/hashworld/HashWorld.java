package fr.hashtek.spigot.hashworld;

import fr.hashtek.hashconfig.HashConfig;
import fr.hashtek.hashlogger.HashLoggable;
import fr.hashtek.hashlogger.HashLogger;
import fr.hashtek.spigot.hashworld.manager.WorldManager;
import fr.hashtek.tekore.bukkit.Tekore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;

/**
 * A plugin that load and unload worlds based on a template.
 */
public class HashWorld extends JavaPlugin implements HashLoggable
{

    private static HashWorld instance = null;
    private HashLogger logger = null;
    private World[] clones = null;
    private int clonesAmount = 0;
    private HashConfig configFile = null;

    @Override
    public void onEnable()
    {
        instance = this;
        Tekore core = null;

        try {
            core = Tekore.getInstance();
        } catch (NullPointerException exception) {
            System.err.println("Tekore failed to load. Stopping.");
            this.getServer().shutdown();
            return;
        }

        this.logger = core.getHashLogger();

        try {
            logger.debug(this, "Loading the configuration file.");
            configFile = new HashConfig(
                this.getClass(),
                "config.yml",
                this.getDataFolder().getPath() + "/" + "config.yml",
                false
            );
        } catch (IOException e) {
            logger.fatal(this, "An error occurred while reading the configuration file: Shutting down the server...", e);
            Bukkit.shutdown();
        }

        clonesAmount = this.configFile.getYaml().getInt("worlds.clones.amount");
        this.loadClones();
    }

    @Override
    public void onDisable()
    {
        this.unloadClones();
    }

    /**
     * Reload the configuration file.
     */
    public void reloadConfig()
    {
        try {
            this.configFile.reload();
            this.clonesAmount = this.configFile.getYaml().getInt("worlds.clones.amount");
            this.reloadClones();
        } catch (IOException e) {
            this.logger.error(this, "An error occurred while reloading the configuration file.", e);
        }
    }

    /**
     * Get a loaded and cloned world.
     *
     * @param id The id of the cloned world.
     * @return The cloned world if the id is correct AND the clones are loaded, otherwise {@code null}.
     */
    public World getClone(int id)
    {
        if (this.clones != null && id < clonesAmount)
            return this.clones[id];
        return null;
    }

    /**
     * Reload a cloned world.
     *
     * @param id The id of the cloned world to reload.
     */
    public void reloadClone(int id)
    {
        if (this.clones != null && id < clonesAmount)
            this.clones[id] = WorldManager.reloadWorld(this.clones[id].getName(), false);
    }

    /**
     * Get the last created instance of HashWorld.
     *
     * @return the last created instance of HashWorld.
     */
    public static HashWorld getInstance()
    {
        return instance;
    }

    /**
     * Load the clone worlds.
     */
    private void loadClones()
    {
        final YamlFile yaml = this.configFile.getYaml();
        final String templateName = yaml.getString("worlds.template.name");
        World templateWorld = null;
        String formatName = yaml.getString("worlds.clones.format-name");
        String cloneName = null;

        templateWorld = Bukkit.createWorld(new WorldCreator(templateName));

        if (templateWorld == null) {
            this.logger.fatal(this, "Cannot found the template named \"" + templateName + "\". Does the world folder exists ?");
            this.getServer().shutdown();
            return;
        }

        if (!formatName.contains("%id%"))
            formatName += "-%id%";

        this.clones = new World[this.clonesAmount];
        for (int i = 0; i < this.clonesAmount; i++) {
            cloneName = formatName.replace("%id%", String.valueOf(i));
            this.logger.debug(this, "Cloning \"" + templateName + "\" to \"" + cloneName + "\".");
            this.clones[i] = WorldManager.duplicateAndLoadWorld(templateWorld.getName(), cloneName);
        }
    }

    /**
     * Unload the cloned worlds.
     */
    private void unloadClones()
    {
        boolean status = false;

        if (this.clones == null)
            return;
        for (int i = 0; i < this.clonesAmount; i++) {
            if (this.clones[i] == null)
                this.logger.warning(this, "Cannot unload world with id \"" + i + "\" because the World is \"null\".");
            else {
                this.logger.debug(this, "Unloading world \"" + this.clones[i].getName() + "\".");
                status = WorldManager.unloadWorld(this.clones[i].getName(), false);
                if (!status)
                    this.logger.warning(this, "Cannot unload \"" + this.clones[i].getName() + "\".");
            }
        }
    }

    /**
     * Reload the cloned worlds.
     */
    private void reloadClones()
    {
        this.unloadClones();
        this.loadClones();
    }

    /**
     * Get the {@link HashLogger} instance.
     *
     * @return The {@link HashLogger} instance.
     */
    public HashLogger getHashLogger()
    {
        return this.logger;
    }

}
