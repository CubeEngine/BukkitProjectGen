package de.codeinfection.quickwango.BukkitProjectGen;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * Hello world!
 *
 */
public class BukkitProjectGen
{
    

    public static void main(String[] args)
    {
        MainFrame.main(args);
    }

    public static void popup(String message, String title, int type)
    {
        JOptionPane.showMessageDialog(null, message, title, type);
    }

    public static void popup(String message, String title, int type, Icon icon)
    {
        JOptionPane.showMessageDialog(null, message, title, type, icon);
    }

    public static void echo(String message)
    {
        System.out.println(message);
    }
}
