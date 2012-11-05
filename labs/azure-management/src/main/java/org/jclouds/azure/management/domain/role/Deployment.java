package org.jclouds.azure.management.domain.role;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

@XmlRootElement(name = "Deployment")
public class Deployment {

   /**
    * A name for the deployment. The deployment name must be unique among other deployments for the
    * hosted service.
    */
   @XmlElement(required = true, name = "Name")
   private String name;

   /**
    * Specifies the environment in which to deploy the virtual machine.
    * 
    * Possible values are: Staging Production
    */
   @XmlElement(required = true, name = "DeploymentSlot")
   private String deploymentSlot;

   /**
    * A name for the hosted service that is base-64 encoded. The name can be up to 100 characters in
    * length.
    * 
    * It is recommended that the label be unique within the subscription. The name can be used
    * identify the hosted service for tracking purposes.
    */
   @XmlElement(required = true, name = "Label")
   private String label;

   @XmlElementWrapper(required = true, name = "RoleList")
   @XmlElement(required = true, name = "Role")
   private List<Role> roleList = Lists.newArrayList();

   /**
    * Specifies the name of an existing virtual network to which the deployment will belong.
    * 
    * Virtual networks are created by calling the Set Network Configuration operation.
    */
   @XmlElement(required = true, name = "VirtualNetworkName")
   private String virtualNetworkName;

   /**
    * Contains a list of DNS servers to associate with the machine.
    */
   @XmlElement(required = true, name = "Dns")
   private DNS dns;

   public Deployment() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDeploymentSlot() {
      return deploymentSlot;
   }

   public void setDeploymentSlot(String deploymentSlot) {
      this.deploymentSlot = deploymentSlot;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public List<Role> getRoleList() {
      return roleList;
   }

   public void setRoleList(List<Role> roleList) {
      this.roleList = roleList;
   }

   public String getVirtualNetworkName() {
      return virtualNetworkName;
   }

   public void setVirtualNetworkName(String virtualNetworkName) {
      this.virtualNetworkName = virtualNetworkName;
   }

   public DNS getDns() {
      return dns;
   }

   public void setDns(DNS dns) {
      this.dns = dns;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((deploymentSlot == null) ? 0 : deploymentSlot.hashCode());
      result = prime * result + ((dns == null) ? 0 : dns.hashCode());
      result = prime * result + ((label == null) ? 0 : label.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((roleList == null) ? 0 : roleList.hashCode());
      result = prime * result + ((virtualNetworkName == null) ? 0 : virtualNetworkName.hashCode());
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
      Deployment other = (Deployment) obj;
      if (deploymentSlot == null) {
         if (other.deploymentSlot != null)
            return false;
      } else if (!deploymentSlot.equals(other.deploymentSlot))
         return false;
      if (dns == null) {
         if (other.dns != null)
            return false;
      } else if (!dns.equals(other.dns))
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
      if (roleList == null) {
         if (other.roleList != null)
            return false;
      } else if (!roleList.equals(other.roleList))
         return false;
      if (virtualNetworkName == null) {
         if (other.virtualNetworkName != null)
            return false;
      } else if (!virtualNetworkName.equals(other.virtualNetworkName))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Deployment [name=" + name + ", deploymentSlot=" + deploymentSlot + ", label=" + label + ", roleList="
               + roleList + ", virtualNetworkName=" + virtualNetworkName + ", dns=" + dns + "]";
   }

}
