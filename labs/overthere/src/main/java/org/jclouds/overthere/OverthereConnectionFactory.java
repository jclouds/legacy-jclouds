package org.jclouds.overthere;

import com.google.inject.Singleton;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.Overthere;
import com.xebialabs.overthere.OverthereConnection;

@Singleton
public class OverthereConnectionFactory {

   public OverthereConnection getConnection(String protocol, ConnectionOptions options) {
      return Overthere.getConnection(protocol, options);
   }
}
