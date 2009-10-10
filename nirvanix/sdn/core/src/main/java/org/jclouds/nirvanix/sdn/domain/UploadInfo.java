package org.jclouds.nirvanix.sdn.domain;

import java.net.URI;

public class UploadInfo {
   private final String token;
   private final URI host;

   public UploadInfo(String token, URI host) {
      this.token = token;
      this.host = host;
   }

   public String getToken() {
      return token;
   }

   public URI getHost() {
      return host;
   }

}
