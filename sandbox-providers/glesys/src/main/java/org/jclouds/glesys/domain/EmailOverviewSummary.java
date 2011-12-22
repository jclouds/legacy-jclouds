package org.jclouds.glesys.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
public class EmailOverviewSummary {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int accounts;
      private int maxAccounts;
      private int aliases;
      private int maxAliases;

      public Builder accounts(int accounts) {
         this.accounts = accounts;
         return this;
      }
      
      public Builder maxAccounts(int maxAccounts) {
         this.maxAccounts = maxAccounts;
         return this;
      }
      
      public Builder aliases(int aliases) {
         this.aliases = aliases;
         return this;
      }
      
      public Builder maxAliases(int maxAliases) {
         this.maxAliases = maxAliases;
         return this;
      }
      
      public EmailOverviewSummary build() {
         return new EmailOverviewSummary(accounts, maxAccounts, aliases, maxAliases);
      }
      
      public Builder fromEmailOverview(EmailOverviewSummary in) {
         return accounts(in.getAccounts()).maxAccounts(in.getMaxAccounts()).aliases(in.getAliases()).maxAliases(in.getMaxAliases());
      }
   }

   private final int accounts;
   @SerializedName("maxaccounts")
   private final int maxAccounts;
   private final int aliases;
   @SerializedName("maxaliases")
   private final int maxAliases;

   public EmailOverviewSummary(int accounts, int maxAccounts, int aliases, int maxAliases) {
      this.accounts = accounts;
      this.maxAccounts = maxAccounts;
      this.aliases = aliases;
      this.maxAliases = maxAliases;
   }

   public int getAccounts() {
      return accounts;
   }

   public int getMaxAccounts() {
      return maxAccounts;
   }

   public int getAliases() {
      return aliases;
   }

   public int getMaxAliases() {
      return maxAliases;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accounts, maxAccounts, aliases, maxAliases);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }
      if (object instanceof EmailOverviewSummary) {
         EmailOverviewSummary other = (EmailOverviewSummary) object;
         return Objects.equal(accounts, other.accounts)
               && Objects.equal(maxAccounts, other.maxAccounts)
               && Objects.equal(aliases, other.aliases)
               && Objects.equal(maxAliases, other.maxAliases);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      Joiner commaJoiner = Joiner.on(", ");
      return String.format("accounts=%d, maxAccounts=%d, aliases=%d, maxAliases=%d", accounts, maxAccounts, aliases, maxAliases);
   }

}
