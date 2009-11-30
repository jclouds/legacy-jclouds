package org.jclouds.compute;

import com.google.common.base.Service;

import java.util.SortedSet;

/**
 * @author Ivan Meredith
 */
public interface Server  {
   public String getId();

   public Platform createPlatform(String id/*, Archive archive  , mount? */);

   public Platform getPlatform(String id);

   public SortedSet<Platform> listPlatforms();

   public SortedSet<Instance> listInstances(/* platform("mybilling-1.0.1").tags("production"  */);

   public Boolean destroyServer();
}
