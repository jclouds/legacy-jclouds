/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.demo.tweetstore.domain;

import java.io.Serializable;

/**
 * 
 * @author Adrian Cole
 */
public class StoredTweetStatus implements Comparable<StoredTweetStatus>, Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = -3257496189689220018L;
   private final String service;
   private final String host;
   private final String container;
   private final String id;
   private final String from;
   private final String tweet;
   private final String status;

   @Override
   public String toString() {
      return "StoredTweetStatus [container=" + container + ", from=" + from + ", host=" + host
               + ", id=" + id + ", service=" + service + ", status=" + status + ", tweet=" + tweet
               + "]";
   }

   public StoredTweetStatus(String service, String host, String container, String id, String from,
            String tweet, String status) {
      this.service = service;
      this.host = host;
      this.container = container;
      this.id = id;
      this.from = from;
      this.tweet = tweet;
      this.status = status;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((container == null) ? 0 : container.hashCode());
      result = prime * result + ((from == null) ? 0 : from.hashCode());
      result = prime * result + ((host == null) ? 0 : host.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((service == null) ? 0 : service.hashCode());
      result = prime * result + ((tweet == null) ? 0 : tweet.hashCode());
      return result;
   }


   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      StoredTweetStatus other = (StoredTweetStatus) obj;
      if (container == null) {
         if (other.container != null)
            return false;
      } else if (!container.equals(other.container))
         return false;
      if (from == null) {
         if (other.from != null)
            return false;
      } else if (!from.equals(other.from))
         return false;
      if (host == null) {
         if (other.host != null)
            return false;
      } else if (!host.equals(other.host))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (service == null) {
         if (other.service != null)
            return false;
      } else if (!service.equals(other.service))
         return false;
      if (tweet == null) {
         if (other.tweet != null)
            return false;
      } else if (!tweet.equals(other.tweet))
         return false;
      return true;
   }


   public String getService() {
      return service;
   }

   public String getHost() {
      return host;
   }

   public String getContainer() {
      return container;
   }

   public String getFrom() {
      return from;
   }

   public String getTweet() {
      return tweet;
   }

   public String getStatus() {
      return status;
   }

   public int compareTo(StoredTweetStatus o) {
      if (id == null)
         return -1;
      return (int) ((this == o) ? 0 : id.compareTo(o.id));
   }


   public String getId() {
      return id;
   }

}
