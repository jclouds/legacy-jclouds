/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.glesys.domain;

import java.util.Date;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Detailed information on an Email Account
 * 
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_list" />
 */
public class EmailAccount implements Comparable<EmailAccount> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String account;
      private String quota;
      private String usedQuota;
      private int antispamLevel;
      private boolean antiVirus;
      private boolean autoRespond;
      private String autoRespondMessage;
      private boolean autoRespondSaveEmail;
      private Date created;
      private Date modified;

      public Builder account(String account) {
         this.account = account;
         return this;
      }
      
      public Builder quota(String quota) {
         this.quota = quota;
         return this;
      }

      public Builder usedQuota(String usedQuota) {
         this.usedQuota = usedQuota;
         return this;
      }
      
      public Builder antispamLevel(int antispamLevel) {
         this.antispamLevel = antispamLevel;
         return this;
      }

      public Builder antiVirus(boolean antiVirus) {
         this.antiVirus = antiVirus;
         return this;
      }
      
      public Builder autoRespond(boolean autoRespond) {
         this.autoRespond = autoRespond;
         return this;
      }
      
      public Builder autoRespondMessage(String autoRespondMessage) {
         this.autoRespondMessage = autoRespondMessage;
         return this;
      }

      public Builder autoRespondSaveEmail(boolean autoRespondSaveEmail) {
         this.autoRespondSaveEmail = autoRespondSaveEmail;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder modified(Date modified) {
         this.modified = modified;
         return this;
      }

      public EmailAccount build() {
         return new EmailAccount(account, quota, usedQuota, antispamLevel, antiVirus, autoRespond, autoRespondMessage,
               autoRespondSaveEmail, created, modified);
      }

      public Builder fromEmail(EmailAccount in) {
         return account(in.getAccount()).quota(in.getQuota()).usedQuota(in.getUsedQuota()).antispamLevel(in.getAntispamLevel()).
               antiVirus(in.getAntiVirus()).autoRespond(in.getAutoRespond()).autoRespondMessage(in.getAutoRespondMessage()).
               autoRespondSaveEmail(in.getAutoRespondSaveEmail()).created(in.getCreated()).modified(in.getModified());
      }
   }

   @SerializedName("emailaccount")
   private final String account;
   private final String quota;
   @SerializedName("usedquota")
   private final String usedQuota;
   @SerializedName("antispamlevel")
   private final int antispamLevel;
   @SerializedName("antivirus")
   private final boolean antiVirus;
   @SerializedName("autorespond")
   private final boolean autoRespond;
   @SerializedName("autorespondmessage")
   private final String autoRespondMessage;
   @SerializedName("autorespondsaveemail")
   private final boolean autoRespondSaveEmail;
   private final Date created;
   private final Date modified;

   public EmailAccount(String account, String quota, String usedQuota, int antispamLevel, boolean antiVirus, boolean autoRespond, String autoRespondMessage, boolean autoRespondSaveEmail, Date created, Date modified) {
      this.account = account;
      this.quota = quota;
      this.usedQuota = usedQuota;
      this.antispamLevel = antispamLevel;
      this.antiVirus = antiVirus;
      this.autoRespond = autoRespond;
      this.autoRespondMessage = autoRespondMessage;
      this.autoRespondSaveEmail = autoRespondSaveEmail;
      this.created = created;
      this.modified = modified;
   }

   /** @return the e-mail address for this e-mail account */
   public String getAccount() {
      return account;
   }

   /** @return the quota for this e-mail account */
   public String getQuota() {
      return quota;
   }

   /** @return the amount of quota currently in use */
   public String getUsedQuota() {
      return usedQuota;
   }

   /** @return the antispam level of the e-mail account */
   public int getAntispamLevel() {
      return antispamLevel;
   }

   /** @return true if antivirus is enabled for this e-mail account */
   public boolean getAntiVirus() {
      return antiVirus;
   }

   /** @return true if auto-respond is enabled for this e-mail account */
   public boolean getAutoRespond() {
      return autoRespond;
   }

   public String getAutoRespondMessage() {
      return autoRespondMessage;
   }

   /** @return true if saving is enabled for auto-respond e-mails */
   public boolean getAutoRespondSaveEmail() {
      return autoRespondSaveEmail;
   }

   /** @return when this account was created */
   public Date getCreated() {
      return created;
   }

   /** @return when this account was last modified */
   public Date getModified() {
      return modified;
   }
   
   @Override
   public int compareTo(EmailAccount other) {
      return account.compareTo(other.getAccount());
   }
   
   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof EmailAccount) {
         EmailAccount other = (EmailAccount) object;
         return Objects.equal(account, other.account);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(account);
   }
   
   @Override
   public String toString() {
      return String.format("account=%s, quota=%s, usedquota=%s, antispamLevel=%d, " +
            "antiVirus=%b, autoRespond=%b, autoRespondMessage=%s, autoRespondSaveEmail=%b, " +
            "created=%s, modified=%s", account, quota, usedQuota, antispamLevel, antiVirus, autoRespond, autoRespondMessage,
            autoRespondSaveEmail, created.toString(), modified.toString());
   }

}
