package org.jclouds.servers;

import java.util.SortedSet;
import java.util.concurrent.Future;

/**
 * @author Ivan Meredith
 */
public interface Platform {
   public String getId();

   public Future<Instance> createInstance();

   public Instance createInstanceAndWait();

   public Instance getInstance(String id);

   public SortedSet<Instance> listInstances();
}
