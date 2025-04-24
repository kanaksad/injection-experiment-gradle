package net.sf.colossus.util;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;



/**
 * Helper class to provide information about operating system
 * and Java version (and provider).
 */
public class SystemInfo
{

    private static @RUntainted String osName;
    private static @RUntainted String osVersion;

    private static @RUntainted String runtimeName;
    private static @RUntainted String vmName;
    private static @RUntainted String javaVersion;

    static
    {
        osName = System.getProperty("os.name", "unknown OS");
        osVersion = System.getProperty("os.version", "unknown version");

        runtimeName = System.getProperty("java.runtime.name", "unknown");
        vmName = System.getProperty("java.vm.name", "unknown");
        javaVersion = System.getProperty("java.version", "unknown");
    }

    public static @RUntainted String getOsInfo()
    {
        return osName + " " + osVersion;
    }

    public static @RUntainted String getFullJavaInfo()
    {
        return runtimeName + "/" + vmName + " " + javaVersion;
    }

    public static String getDisplayJavaInfo()
    {
        return runtimeName + " " + javaVersion;
    }

    public static boolean isOracleJava7()
    {
        if (javaVersion.startsWith("1.7.0")
            && (runtimeName.startsWith("Java(TM)") || vmName.startsWith("Java HotSpot(TM)")))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
