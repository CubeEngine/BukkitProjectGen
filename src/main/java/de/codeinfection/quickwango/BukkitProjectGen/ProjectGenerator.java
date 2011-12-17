package de.codeinfection.quickwango.BukkitProjectGen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import static de.codeinfection.quickwango.BukkitProjectGen.BukkitProjectGen.echo;

/**
 *
 * @author CodeInfection
 */
public class ProjectGenerator
{
    private static ProjectGenerator instance = null;

    private File workingDir;

    private Map<String, String> resources;

    private ProjectGenerator() throws IOException
    {
        this.workingDir = new File(System.getProperty("user.dir", "."));

        this.resources = new HashMap<String, String>();
        ClassLoader cLoader = ClassLoader.getSystemClassLoader();

        this.resources.put("CHANGES.md",
                this.readResource(cLoader.getResourceAsStream("resources/bukkit/CHANGES.md")));
        this.resources.put("README.md",
                this.readResource(cLoader.getResourceAsStream("resources/bukkit/README.md")));
        this.resources.put("src:main:resources:config.yml",
                this.readResource(cLoader.getResourceAsStream("resources/bukkit/config.yml")));
        this.resources.put("src:main:resources:plugin.yml",
                this.readResource(cLoader.getResourceAsStream("resources/bukkit/plugin.yml")));
        this.resources.put("src:main:java:{GROUPID}:{NAME}:{NAME}.java",
                this.readResource(cLoader.getResourceAsStream("resources/bukkit/configclass.txt")));
        this.resources.put("src:main:java:{GROUPID}:{NAME}:{NAME}Configuration.java",
                this.readResource(cLoader.getResourceAsStream("resources/bukkit/mainclass.txt")));
        this.resources.put("pom.xml",
                this.readResource(cLoader.getResourceAsStream("resources/bukkit/pom.xml")));
    }

    private String readResource(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = reader.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        reader.close();
        in.close();

        return sb.toString();
    }

    public static ProjectGenerator getInstance() throws IOException
    {
        if (instance == null)
        {
            instance = new ProjectGenerator();
        }
        return instance;
    }

    public Result generate(String name, String groupId, Map<String, String> vars)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name must not be null!");
        }
        if (groupId == null)
        {
            throw new IllegalArgumentException("groupId must not be null!");
        }

        vars.put("NAME", name);
        vars.put("GROUPID", groupId);

        File baseDir = new File(this.workingDir, name);
        if (!baseDir.exists())
        {
            baseDir.mkdirs();

            BufferedWriter writer = null;
            for (Map.Entry<String, String> entry : this.resources.entrySet())
            {
                String path = entry.getKey();
                echo(path + ":");
                path = path
                        .replace("{NAME}", name)
                        .replace("{GROUPID}", groupId.replace(".", File.separator))
                        .replace(":", File.separator);
                echo("\t" + path);
                File file = new File(baseDir, path);
                File dir = file.getParentFile();
                if (dir != null)
                {
                    dir.mkdirs();
                }

                try
                {
                    writer = new BufferedWriter(new FileWriter(file));
                    String content = entry.getValue();
                    for (Map.Entry<String, String> var : vars.entrySet())
                    {
                        content = content.replace("{" + var.getKey() + "}", var.getValue());
                    }
                    writer.write(content);
                }
                catch (IOException e)
                {
                    echo(e.getLocalizedMessage());
                    return Result.IO_ERROR;
                }

                try
                {
                    writer.close();
                }
                catch (Throwable t)
                {
                    echo(t.getLocalizedMessage());
                }
            }
        }
        else
        {
            return Result.ALREADY_EXISTS;
        }
        return Result.SUCCESS;
    }

    public enum Result
    {
        ALREADY_EXISTS,
        IO_ERROR,
        PERMISSION_DENIED,
        SUCCESS
    }
}
