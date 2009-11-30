package org.jclouds.compute;

import java.util.SortedSet;
import java.util.concurrent.Future;

/**
 * TODO: better name?
 *
 * @author Ivan Meredith
 */
public interface ComputeService {
   public Server createServerAndWait(String name, String profile, String image);

   public Future<Server> createServer(String name, String profile, String image);

   public SortedSet<Server> listServers();

   public Server getServer(String id);
   
}
