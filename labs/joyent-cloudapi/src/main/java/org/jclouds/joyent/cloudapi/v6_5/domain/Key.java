package org.jclouds.joyent.cloudapi.v6_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.Objects;

/**
 * Keys are the means by which you operate on your SSH/signing keys. Currently
 * CloudAPI supports uploads of public keys in the OpenSSH format.
 * 
 * @author Adrian Cole
 * @see <a href="http://apidocs.joyent.com/cloudApiapidoc/cloudapi/#keys" >docs</a>
 */
public class Key implements Comparable<Key> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromKey(this);
   }
   
   public static class Builder {
      private String name;
      private String key;
      private Date created;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder key(String key) {
         this.key = key;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Key build() {
         return new Key(name, key, created);
      }

      public Builder fromKey(Key in) {
         return name(in.getName()).key(in.get()).created(in.getCreated());
      }
   }

   protected final String name;
   protected final String key;
   protected final Date created;

   public Key(String name, String key, Date created) {
      this.name = checkNotNull(name, "name");
      this.key = checkNotNull(key, "key: OpenSSH formatted public key");
      this.created = created;
   }

   /**
    * Name for this key
    */
   public String getName() {
      return name;
   }

   /**
    * OpenSSH formatted public key
    */
   public String get() {
      return key;
   }

   public Date getCreated() {
      return created;
   }

   @Override
   public int compareTo(Key other) {
      return name.compareTo(other.getName());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Key) {
         return Objects.equal(name, ((Key) object).name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public String toString() {
      return String.format("[name=%s, key=%s, created=%s]", name, key, created);
   }
}
