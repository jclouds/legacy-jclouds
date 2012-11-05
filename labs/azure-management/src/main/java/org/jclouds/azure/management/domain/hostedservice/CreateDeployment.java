package org.jclouds.azure.management.domain.hostedservice;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

@XmlRootElement(name = "CreateDeployment")
public class CreateDeployment {

   /**
    * The name for the deployment. The deployment name must be unique among other deployments for
    * the hosted service.
    */
   @XmlElement(required = true, name = "Name")
   private String name;

   /**
    * A URL that refers to the location of the service package in the Blob service. The service
    * package can be located either in a storage account beneath the same subscription or a Shared
    * Access Signature (SAS) URI from any storage account.
    */
   @XmlElement(required = true, name = "PackageUrl")
   private String packageUrl;

   /**
    * The base-64 encoded service configuration file for the deployment.
    */
   @XmlElement(required = true, name = "Configuration")
   private String configuration;

   /**
    * A name for the hosted service that is base-64 encoded. The name can be up to 100 characters in
    * length.
    * 
    * It is recommended that the label be unique within the subscription. The name can be used
    * identify the hosted service for your tracking purposes.
    */
   @XmlElement(required = true, name = "Label")
   private String label;

   /**
    * 
    * Indicates whether to start the deployment immediately after it is created. The default value
    * is false.
    * 
    * If false, the service model is still deployed to the virtual machines but the code is not run
    * immediately. Instead, the service is Suspended until you call Update Deployment Status and set
    * the status to Running, at which time the service will be started. A deployed service still
    * incurs charges, even if it is suspended.
    */
   @XmlElement(name = "StartDeployment")
   private Boolean startDeployment;

   /**
    * 
    * Optional. Indicates whether to treat package validation warnings as errors. The default value
    * is false. If set to true, the Created Deployment operation fails if there are validation
    * warnings on the service package.
    */
   @XmlElement(name = "TreatWarningsAsError")
   private Boolean treatWarningsAsError;

   @XmlElementWrapper(name = "ExtendedProperties")
   @XmlElement(required = true, name = "ExtendedProperty")
   private List<ExtendedProperty> extendedProperties = Lists.newArrayList();

   public CreateDeployment() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPackageUrl() {
      return packageUrl;
   }

   public void setPackageUrl(String packageUrl) {
      this.packageUrl = packageUrl;
   }

   public String getConfiguration() {
      return configuration;
   }

   public void setConfiguration(String configuration) {
      this.configuration = configuration;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public Boolean getStartDeployment() {
      return startDeployment;
   }

   public void setStartDeployment(Boolean startDeployment) {
      this.startDeployment = startDeployment;
   }

   public Boolean getTreatWarningsAsError() {
      return treatWarningsAsError;
   }

   public void setTreatWarningsAsError(Boolean treatWarningsAsError) {
      this.treatWarningsAsError = treatWarningsAsError;
   }

   public List<ExtendedProperty> getExtendedProperties() {
      return extendedProperties;
   }

   public void setExtendedProperties(List<ExtendedProperty> extendedProperties) {
      this.extendedProperties = extendedProperties;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
      result = prime * result + ((extendedProperties == null) ? 0 : extendedProperties.hashCode());
      result = prime * result + ((label == null) ? 0 : label.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((packageUrl == null) ? 0 : packageUrl.hashCode());
      result = prime * result + ((startDeployment == null) ? 0 : startDeployment.hashCode());
      result = prime * result + ((treatWarningsAsError == null) ? 0 : treatWarningsAsError.hashCode());
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
      CreateDeployment other = (CreateDeployment) obj;
      if (configuration == null) {
         if (other.configuration != null)
            return false;
      } else if (!configuration.equals(other.configuration))
         return false;
      if (extendedProperties == null) {
         if (other.extendedProperties != null)
            return false;
      } else if (!extendedProperties.equals(other.extendedProperties))
         return false;
      if (label == null) {
         if (other.label != null)
            return false;
      } else if (!label.equals(other.label))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (packageUrl == null) {
         if (other.packageUrl != null)
            return false;
      } else if (!packageUrl.equals(other.packageUrl))
         return false;
      if (startDeployment == null) {
         if (other.startDeployment != null)
            return false;
      } else if (!startDeployment.equals(other.startDeployment))
         return false;
      if (treatWarningsAsError == null) {
         if (other.treatWarningsAsError != null)
            return false;
      } else if (!treatWarningsAsError.equals(other.treatWarningsAsError))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "CreateDeployment [name=" + name + ", packageUrl=" + packageUrl + ", configuration=" + configuration
               + ", label=" + label + ", startDeployment=" + startDeployment + ", treatWarningsAsError="
               + treatWarningsAsError + ", extendedProperties=" + extendedProperties + "]";
   }

}
