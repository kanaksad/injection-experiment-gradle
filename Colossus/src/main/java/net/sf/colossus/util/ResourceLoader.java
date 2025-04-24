package net.sf.colossus.util;


import java.util.logging.Logger;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


public class ResourceLoader
{
    private static final Logger LOGGER = Logger.getLogger(ResourceLoader.class
        .getName());

    private final @RUntainted String host;
    private final @RUntainted int port;

    public ResourceLoader(@RUntainted String hostArg, @RUntainted int portArg)
    {
        this.host = hostArg;
        this.port = portArg;

        LOGGER.finest("ResourceLoader created for host " + host + " port "
            + port);

        StaticResourceLoader.setDataServer(host, port);

    }

    @Override
    public @RUntainted String toString()
    {
        return "ResourceLoader with DataServer " + host + ":" + port;
    }
}
