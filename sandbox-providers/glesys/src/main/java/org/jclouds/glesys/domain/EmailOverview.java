package org.jclouds.glesys.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Structure containing all information about e-mail addresses for a GleSYS account
 * 
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
public class EmailOverview {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private EmailOverviewSummary summary;
      private Set<EmailOverviewDomain> domains;

      public Builder summary(EmailOverviewSummary summary) {
         this.summary = summary;
         return this;
      }

      public Builder domains(Set<EmailOverviewDomain> domains) {
         this.domains = domains;
         return this;
      }

      public Builder domains(EmailOverviewDomain... domains) {
         return domains(ImmutableSet.copyOf(domains));
      }

      public EmailOverview build() {
         return new EmailOverview(summary, domains);
      }
      
      public Builder fromEmailOverview(EmailOverview in) {
         return summary(in.getSummary()).domains(in.getDomains());
      }
   }

   private EmailOverviewSummary summary;
   private Set<EmailOverviewDomain> domains;

   public EmailOverview(EmailOverviewSummary summary,  Set<EmailOverviewDomain> domains) {
      this.summary = summary; 
      this.domains = domains;
   }

   /** @return summary information about the account */
   public EmailOverviewSummary getSummary() {
      return summary;
   }

   /** @return the set of detailed information about the e-mail addresses and aliases for each domain */
   public Set<EmailOverviewDomain> getDomains() {
      return domains == null ? ImmutableSet.<EmailOverviewDomain>of() : domains;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(summary, domains);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }
      if (object instanceof EmailOverview) {
         EmailOverview other = (EmailOverview) object;
         return Objects.equal(summary, other.summary)
               && Objects.equal(domains, other.domains);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      Joiner commaJoiner = Joiner.on(", ");
      return String.format("summary=%s, domains=[%s]", summary, commaJoiner.join(getDomains()));
   }

}
