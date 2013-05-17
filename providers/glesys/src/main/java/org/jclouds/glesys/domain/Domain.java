/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Domain data for a Glesys account.
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#domain_list" />
 */
public class Domain {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromDomain(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String domainName;
      protected Date createTime;
      protected int recordCount;
      protected boolean useGlesysNameServer;
      protected String primaryNameServer;
      protected String responsiblePerson;
      protected int ttl;
      protected int refresh;
      protected int retry;
      protected int expire;
      protected int minimum;

      /**
       * @see Domain#getName()
       */
      public T domainName(String domainName) {
         this.domainName = checkNotNull(domainName, "domainName");
         return self();
      }

      /**
       * @see Domain#getCreateTime()
       */
      public T createTime(Date createTime) {
         this.createTime = createTime;
         return self();
      }

      /**
       * @see Domain#getRecordCount()
       */
      public T recordCount(int recordCount) {
         this.recordCount = recordCount;
         return self();
      }

      /**
       * @see Domain#isUseGlesysNameServer()
       */
      public T useGlesysNameServer(boolean useGlesysNameServer) {
         this.useGlesysNameServer = useGlesysNameServer;
         return self();
      }

      /**
       * @see Domain#getPrimaryNameServer()
       */
      public T primaryNameServer(String primaryNameServer) {
         this.primaryNameServer = primaryNameServer;
         return self();
      }

      /**
       * @see Domain#getResponsiblePerson()
       */
      public T responsiblePerson(String responsiblePerson) {
         this.responsiblePerson = responsiblePerson;
         return self();
      }

      /**
       * @see Domain#getTtl()
       */
      public T ttl(int ttl) {
         this.ttl = ttl;
         return self();
      }

      /**
       * @see Domain#getRefresh()
       */
      public T refresh(int refresh) {
         this.refresh = refresh;
         return self();
      }

      /**
       * @see Domain#getRetry()
       */
      public T retry(int retry) {
         this.retry = retry;
         return self();
      }

      /**
       * @see Domain#getExpire()
       */
      public T expire(int expire) {
         this.expire = expire;
         return self();
      }

      /**
       * @see Domain#getMinimum()
       */
      public T minimum(int minimum) {
         this.minimum = minimum;
         return self();
      }

      public Domain build() {
         return new Domain(domainName, createTime, recordCount, new GleSYSBoolean(useGlesysNameServer), primaryNameServer, responsiblePerson, ttl, refresh, retry, expire, minimum);
      }

      public T fromDomain(Domain in) {
         return this.domainName(in.getName())
               .createTime(in.getCreateTime())
               .recordCount(in.getRecordCount())
               .useGlesysNameServer(in.isUseGlesysNameServer())
               .primaryNameServer(in.getPrimaryNameServer())
               .responsiblePerson(in.getResponsiblePerson())
               .ttl(in.getTtl())
               .refresh(in.getRefresh())
               .retry(in.getRetry())
               .expire(in.getExpire());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String domainName;
   private final Date createTime;
   private final int recordCount;
   private final boolean useGlesysNameServer;
   private final String primaryNameServer;
   private final String responsiblePerson;
   private final int ttl;
   private final int refresh;
   private final int retry;
   private final int expire;
   private final int minimum;

   @ConstructorProperties({
         "domainname", "createtime", "recordcount", "usingglesysnameserver", "primarynameserver", "responsibleperson",
         "ttl", "refresh", "retry", "expire", "minimum"
   })
   protected Domain(String domainName, @Nullable Date createTime, int recordCount, GleSYSBoolean useGlesysNameServer,
                    @Nullable String primaryNameServer, @Nullable String responsiblePerson,
                    int ttl, int refresh, int retry, int expire, int minimum) {
      this.domainName = checkNotNull(domainName, "domainName");
      this.createTime = createTime;
      this.recordCount = recordCount;
      this.useGlesysNameServer = checkNotNull(useGlesysNameServer, "useGlesysNameServer").getValue();
      this.primaryNameServer = primaryNameServer;
      this.responsiblePerson = responsiblePerson;
      this.ttl = ttl;
      this.refresh = refresh;
      this.retry = retry;
      this.expire = expire;
      this.minimum = minimum;
   }

   /**
    * @return the domain name, ex. "jclouds.org"
    */
   public String getName() {
      return this.domainName;
   }

   /**
    * @return the date the domain was registered with GleSYS
    */
   public Date getCreateTime() {
      return this.createTime;
   }

   /**
    * @return the number of DNS records for this domain
    */
   public int getRecordCount() {
      return this.recordCount;
   }

   /**
    * @return true if a GleSYS nameserver holds the records
    */
   public boolean isUseGlesysNameServer() {
      return this.useGlesysNameServer;
   }

   @Nullable
   public String getPrimaryNameServer() {
      return primaryNameServer;
   }

   /**
    * The E-mail address of the person responsible for this domain (reformatted with '.' at end).
    */
   @Nullable
   public String getResponsiblePerson() {
      return responsiblePerson;
   }

   /**
    * TTL (time to live). The number of seconds a domain name is cached locally before expiration and return to authoritative nameServers for updates
    */
   public int getTtl() {
      return ttl;
   }

   /**
    * The number of seconds between update requests from secondary and slave name servers
    */
   public int getRefresh() {
      return refresh;
   }


   /**
    * The number of seconds the secondary/slave will wait before retrying when the last attempt failed
    */
   public int getRetry() {
      return retry;
   }

   /**
    * The number of seconds a master or slave will wait before considering the data stale if it cannot reach the primary name server
    */
   public int getExpire() {
      return expire;
   }

   /**
    * The minimum/default TTL if the domain does not specify ttl
    *
    * @see #getTtl()
    */
   public int getMinimum() {
      return minimum;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(domainName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Domain that = Domain.class.cast(obj);
      return Objects.equal(this.domainName, that.domainName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("domainName", domainName).add("createTime", createTime).add("recordCount", recordCount).add("useGlesysNameServer", useGlesysNameServer);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
