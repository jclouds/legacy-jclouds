package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Connection information to connect to a server with VNC.
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#server_console" />
 */
public class ServerConsole {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String host;
      private int port;
      private String password;

      public Builder host(String host) {
         this.host = host;
         return this;
      }

      public Builder port(int port) {
         this.port = port;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public ServerConsole build() {
         return new ServerConsole(host, port, password);
      }
      
      public Builder fromServerConsole(ServerConsole in) {
         return host(in.getHost()).port(in.getPort()).password(in.getPassword());
      }

   }

   private final String host;
   private final int port;
   private final String password;

   public ServerConsole(String host, int port, String password) {
      this.host = host;
      this.port = port;
      this.password = password;
   }

   /**
    * @return the host name to use to connect to the server
    */
   public String getHost() {
      return host;
   }

   /**
    * @return the port to use to connect to the server
    */
   public int getPort() {
      return port;
   }

   /**
    * @return the password to use to connect to the server
    */
   public String getPassword() {
      return password;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerConsole) {
         final ServerConsole other = (ServerConsole) object;
         return Objects.equal(host, other.host)
               && Objects.equal(port, other.port)
               && Objects.equal(password, other.password);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(host, port, password);
   }

   @Override
   public String toString() {
      return String.format("[host=%s, port=%s, password=%s]", host, port, password);
   }

}
