package org.jclouds.servers;

import java.util.SortedSet;
import java.util.concurrent.Future;

/**
 * TODO: better name?
 *
 * @author Ivan Meredith
 */
public interface ServerService {
   public Server createServerAndWait(String name, String profile, String image);

   public Future<Server> createServer(String name, String profile, String image);

   public SortedSet<Server> listServers();

   public Server getServer(String id);
   
}
