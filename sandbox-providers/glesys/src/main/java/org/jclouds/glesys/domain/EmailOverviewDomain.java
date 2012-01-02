package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
public class EmailOverviewDomain {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String domain;
      private int accounts;
      private int aliases;

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder accounts(int accounts) {
         this.accounts = accounts;
         return this;
      }
      
      public Builder aliases(int aliases) {
         this.aliases = aliases;
         return this;
      }
      
      public EmailOverviewDomain build() {
         return new EmailOverviewDomain(domain, accounts, aliases);
      }
      
      public Builder fromEmailOverview(EmailOverviewDomain in) {
         return domain(domain).accounts(in.getAccounts()).aliases(in.getAliases());
      }
   }

   private final String domain;
   private final int accounts;
   private final int aliases;

   public EmailOverviewDomain(String domain, int accounts, int aliases) {
      this.domain = domain;
      this.accounts = accounts;
      this.aliases = aliases;
   }

   public String getDomain() {
      return domain;
   }

   public int getAccounts() {
      return accounts;
   }

   public int getAliases() {
      return aliases;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(domain);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }
      if (object instanceof EmailOverviewDomain) {
         EmailOverviewDomain other = (EmailOverviewDomain) object;
         return Objects.equal(domain, other.domain);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("domain=%s, accounts=%d, aliases=%d", domain, accounts, aliases);
   }

}
