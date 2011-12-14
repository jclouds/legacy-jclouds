package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;
import org.jclouds.javax.annotation.Nullable;

/**
 * Connection information to connect to a server with VNC.
 * 
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#server_console" />
 */
public class ServerCreated {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String hostname;
      private List<ServerCreatedIp> ips;
      
      public Builder id(String id) {
         this.id = id;
         return this;
      }
      public Builder port(List<ServerCreatedIp> ips) {
         this.ips = ips;
         return this;
      }
      
      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }
      
      public ServerCreated build() {
         return new ServerCreated(id, hostname, ips);
      }
      
      public Builder fromServerCreated(ServerCreated in) {
         return id(in.getId()).hostname(in.getHostname()).port(in.getIps());
      }
   }

   @SerializedName("serverid")
   private final String id;
   private final String hostname;
   @SerializedName("iplist")
   private final List<ServerCreatedIp> ips;

   public ServerCreated(String id, @Nullable String hostname, List<ServerCreatedIp> ips) {
      checkNotNull(id, "id");
      this.id = id;
      this.hostname = hostname;
      this.ips = ips;
   }

   public String getId() {
      return id;
   }

   public String getHostname() {
      return hostname;
   }

   public List<ServerCreatedIp> getIps() {
      return ips;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerCreated) {
         final ServerCreated other = (ServerCreated) object;
         return Objects.equal(id, other.id)
               && Objects.equal(ips, other.ips);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, ips);
   }
   
   @Override
   public String toString() {
      return String.format("[id=%s, hostname=%s, ips=%s]", id, hostname, ips);
   }

}
