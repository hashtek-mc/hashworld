package fr.hashtek.spigot.hashworld.files;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that allow you to clone some files or some folders.
 */
public class FileCloner
{

    /**
     * Copy a Bukkit World folder.
     *
     * @param source The world folder to copy.
     * @param target The world folder destination.
     * @throws IOException If a copy of a file or a folder cannot be done.
     */
    public static void copyWorldFolder(File source, File target)
        throws IOException
    {
        ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
        if (!ignore.contains(source.getName())) {
            if (source.isDirectory())
                copyFolder(source, target);
            else
                copyFile(source, target);
        }
    }

    /**
     * Copy a file.
     *
     * @param source The source of the file to copy.
     * @param target The file destination.
     * @throws IOException If a stream failed on creation or on closure.
     */
    public static void copyFile(File source, File target)
        throws IOException
    {
        InputStream in = Files.newInputStream(source.toPath());
        OutputStream out = Files.newOutputStream(target.toPath());
        byte[] buffer = new byte[1024];
        int length;

        while ((length = in.read(buffer)) > 0)
            out.write(buffer, 0, length);
        in.close();
        out.close();
    }

    /**
     * Copy a folder.
     *
     * @param source The source of the folder to copy.
     * @param target The folder destination.
     * @throws IOException If a folder failed to create.
     */
    public static void copyFolder(File source, File target)
        throws IOException
    {
        String[] files = null;
        File srcFile = null;
        File destFile = null;

        if (!target.exists())
            if (!target.mkdirs())
                throw new IOException("Couldn't create the directory \"" + target.getAbsolutePath() + "\".");
        files = source.list();
        if (files != null)
            for (String file : files) {
                srcFile = new File(source, file);
                destFile = new File(target, file);
                copyWorldFolder(srcFile, destFile);
            }
    }

}
