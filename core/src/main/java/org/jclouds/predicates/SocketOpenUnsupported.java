package org.jclouds.predicates;

import javax.inject.Singleton;

import org.jclouds.net.IPSocket;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class SocketOpenUnsupported implements SocketOpen {

   @Override
   public boolean apply(IPSocket socketA) {
      throw new UnsupportedOperationException("socket testing not configured");
   }

}
