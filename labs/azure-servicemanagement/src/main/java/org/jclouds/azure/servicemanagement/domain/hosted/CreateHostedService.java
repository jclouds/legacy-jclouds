package org.jclouds.azure.servicemanagement.domain.hosted;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CreateHostedService")
public class CreateHostedService {

	/**
	 * A name for the hosted service that is unique within Windows Azure. This
	 * name is the DNS prefix name and can be used to access the hosted service.
	 */
	@XmlElement(required = true, name = "ServiceName")
	private String serviceName;

	/**
	 * A name for the hosted service that is base-64 encoded. The name can be up
	 * to 100 characters in length. The name can be used identify the storage
	 * account for your tracking purposes.
	 */
	@XmlElement(required = true, name = "Label")
	private String label;

	/**
	 * A description for the hosted service. The description can be up to 1024
	 * characters in length.
	 */
	@XmlElement(name = "Description")
	private String description;

	/**
	 * Required if AffinityGroup is not specified. The location where the hosted
	 * service will be created.
	 * 
	 * Specify either Location or AffinityGroup, but not both. To list available
	 * locations, use the List Locations operation.
	 */
	@XmlElement(name = "Location")
	private String location;

	/**
	 * Required if Location is not specified. The name of an existing affinity
	 * group associated with this subscription. This name is a GUID and can be
	 * retrieved by examining the name element of the response body returned by
	 * the List Affinity Groups operation
	 * 
	 * Specify either Location or AffinityGroup, but not both. To list available
	 * affinity groups, use the List Affinity Groups operation.
	 */
	@XmlElement(required = true, name = "AffinityGroup")
	private String affinityGroup;

	
	@XmlElementWrapper(name = "ExtendedProperties")
	@XmlElement(required = true, name = "ExtendedProperty")
	private List<ExtendedProperty> extendedProperties = new ArrayList<ExtendedProperty>();
	
	public CreateHostedService(){
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAffinityGroup() {
		return affinityGroup;
	}

	public void setAffinityGroup(String affinityGroup) {
		this.affinityGroup = affinityGroup;
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
		result = prime * result
				+ ((affinityGroup == null) ? 0 : affinityGroup.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime
				* result
				+ ((extendedProperties == null) ? 0 : extendedProperties
						.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
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
		CreateHostedService other = (CreateHostedService) obj;
		if (affinityGroup == null) {
			if (other.affinityGroup != null)
				return false;
		} else if (!affinityGroup.equals(other.affinityGroup))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CreateHostedService [serviceName=" + serviceName + ", label="
				+ label + ", description=" + description + ", location="
				+ location + ", affinityGroup=" + affinityGroup
				+ ", extendedProperties=" + extendedProperties + "]";
	}
	
}
