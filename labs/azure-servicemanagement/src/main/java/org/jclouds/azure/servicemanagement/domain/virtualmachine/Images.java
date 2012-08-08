package org.jclouds.azure.servicemanagement.domain.virtualmachine;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Images")
public class Images {

	@XmlElement(name = "OSImage")
	private List<OSImage> osImages = new ArrayList<OSImage>(0);

	public Images() {
		super();
	}

	public List<OSImage> getOsImages() {
		return osImages;
	}

	public void setOsImages(List<OSImage> osImages) {
		this.osImages = osImages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((osImages == null) ? 0 : osImages.hashCode());
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
		Images other = (Images) obj;
		if (osImages == null) {
			if (other.osImages != null)
				return false;
		} else if (!osImages.equals(other.osImages))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Images [osImages=" + osImages + "]";
	}
	
	
}
