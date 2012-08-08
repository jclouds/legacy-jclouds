package org.jclouds.azure.servicemanagement.domain.virtualmachine;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Warning the documentation (http://msdn.microsoft.com/en-us/library/jj157191) LIES !!!
 * @author gpereira
 *
 */
@XmlRootElement(name = "OSImage")
public class OSImage {

// Here is the xml returned. There are a few differences with the documentation. I kept the field that do not seem to be used (affinityGroup, location, medialink) and added the Eula one.
//
//	<Images xmlns="http://schemas.microsoft.com/windowsazure" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
//		<OSImage>
//			<Category>Canonical</Category>
//			<Label>Ubuntu Server 12.04 LTS</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>CANONICAL__Canonical-Ubuntu-12-04-amd64-server-20120528.1.3-en-us-30GB.vhd</Name>
//			<OS>Linux</OS>
//			<Eula>http://www.ubuntu.com/project/about-ubuntu/licensing</Eula>
//			<Description>Ubuntu Server 12.04 LTS amd64 20120528 Cloud Image</Description>
//		</OSImage>
//		<OSImage>
//			<Category>Microsoft</Category>
//			<Label>Windows Server 2008 R2 SP1, June 2012</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd</Name>
//			<OS>Windows</OS>
//			<Eula/>
//			<Description>Windows Server 2008 R2 is a multi-purpose server designed to increase the reliability and flexibility of your server or private cloud infrastructure, helping you to save time and reduce costs. It provides you with powerful tools to react to business needs with greater control and confidence.</Description>
//		</OSImage>
//		<OSImage>
//			<Category>Microsoft</Category>
//			<Label>Microsoft SQL Server 2012 Evaluation Edition</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>MSFT__Sql-Server-11EVAL-11.0.2215.0-05152012-en-us-30GB.vhd</Name>
//			<OS>Windows</OS>
//			<Eula>http://go.microsoft.com/fwlink/?LinkID=251820;http://go.microsoft.com/fwlink/?LinkID=131004</Eula>
//			<Description>SQL Server 2012 Evaluation Edition (64-bit) on Windows Server 2008 R2 Service Pack 1. This image contains the full version of SQL Server, including all components except Distributed Replay, Always On, and Clustering capabilities. Some SQL Server 2012 components require additional setup and configuration before use.		Medium is the minimum recommended size for this image. To evaluate advanced SQL Server 2012 capabilities, Large or Extra-Large sizes are recommended.</Description>
//		</OSImage>
//		<OSImage>
//			<Category>Microsoft</Category>
//			<Label>Windows Server 2012 Release Candidate, July 2012</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>MSFT__Win2K12RC-Datacenter-201207.02-en.us-30GB.vhd</Name>
//			<OS>Windows</OS>
//			<Eula/>
//			<Description>Windows Server 2012 incorporates Microsoft's experience building and operating public clouds, resulting in a dynamic, highly available server platform. It offers a scalable, dynamic and multi-tenant-aware infrastructure that helps securely connect across premises. Windows Server is an open, scalable and cloud-ready web and application platform. It empowers IT administrators to help secure their users' access to a personalized environment from virtually anywhere. A pre-release version of Windows Server - Windows Server 2012 Release Candidate - has been incorporated into this VHD.</Description>
//			</OSImage>
//		<OSImage>
//			<Category>Microsoft</Category>
//			<Label>Windows Server 2008 R2 SP1, July 2012</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>MSFT__Win2K8R2SP1-Datacenter-201207.01-en.us-30GB.vhd</Name>
//			<OS>Windows</OS>
//			<Eula/>
//			<Description>Windows Server 2008 R2 is a multi-purpose server designed to increase the reliability and flexibility of your server or private cloud infrastructure, helping you to save time and reduce costs. It provides you with powerful tools to react to business needs with greater control and confidence.</Description>
//		</OSImage>
//		<OSImage>
//			<Category>OpenLogic</Category>
//			<Label>OpenLogic CentOS 6.2</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd</Name>
//			<OS>Linux</OS>
//			<Eula>http://www.openlogic.com/azure/service-agreement/</Eula>
//			<Description>This distribution of Linux is based on CentOS version 6.2	and is provided by OpenLogic. It contains an installation	of the Basic Server packages.</Description>
//		</OSImage>
//		<OSImage>
//			<Category>SUSE</Category>
//			<Label>openSUSE 12.1</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>SUSE__openSUSE-12-1-20120603-en-us-30GB.vhd</Name>
//			<OS>Linux</OS>
//			<Eula>http://opensuse.org/</Eula>
//			<Description>openSUSE is a free and Linux-based operating system for your PC, Laptop or Server. You can surf the web, manage your e-mails and photos, do office work, play videos or music and have a lot of fun!</Description>
//		</OSImage>
//		<OSImage>
//			<Category>SUSE</Category>
//			<Label>SUSE Linux Enterprise Server</Label>
//			<LogicalSizeInGB>30</LogicalSizeInGB>
//			<Name>SUSE__SUSE-Linux-Enterprise-Server-11SP2-20120601-en-us-30GB.vhd</Name>
//			<OS>Linux</OS>
//			<Eula>http://www.novell.com/licensing/eula/</Eula>
//			<Description>SUSE Linux Enterprise Server is a highly reliable, scalable, and secure server operating system, built to power mission-critical workloads in both physical and virtual environments. It is an affordable, interoperable, and manageable open source foundation. With it, enterprises can cost-effectively deliver core business services, enable secure networks, and simplify the management of their heterogeneous IT infrastructure, maximizing efficiency and value.</Description>
//		</OSImage>
//	</Images>

	
	/**
	 * The affinity in which the media is located. The AffinityGroup value is
	 * derived from storage account that contains the blob in which the media is
	 * located. If the storage account does not belong to an affinity group the
	 * value is NULL.
	 * 
	 * This value is NULL for platform images.
	 */
	@XmlElement(name = "AffinityGroup")
	private String affinityGroup;

	/**
	 * The repository classification of image. All user images have the category
	 * “User”.
	 */
	@XmlElement(name = "Category")
	private String category;

	/**
	 * The description of the image.
	 */
	@XmlElement(name = "Label")
	private String label;

	/**
	 * The geo-location in which this media is located. The Location value is
	 * derived from storage account that contains the blob in which the media is
	 * located. If the storage account belongs to an affinity group the value is
	 * NULL.
	 * 
	 * This value is NULL for platform images.
	 */
	@XmlElement(name = "Location")
	private String location;

	/**
	 * The location of the blob in the blob store in which the media for the
	 * image is located. The blob location belongs to a storage account in the
	 * subscription specified by the <subscription-id> value in the operation
	 * call.
	 */
	@XmlElement(name = "LogicalSizeInGB")
	private Integer logicalSizeInGB;

	/**
	 * The name of the OS image. This is the name that is used when creating one
	 * or more virtual machines using the image.
	 */
	@XmlElement(name = "Name")
	private String name;

	/**
	 * The location of the blob in the blob store in which the media for the
	 * image is located. The blob location belongs to a storage account in the
	 * subscription specified by the <subscription-id> value in the operation
	 * call.
	 */
	@XmlElement(name = "MediaLink")
	private String mediaLink;

	/**
	 * The operating system type of the OS image.
	 * 
	 * Possible Values are:
	 * 
	 * Linux
	 * 
	 * Windows
	 */
	@XmlElement(name = "OS")
	private String os;
	
	@XmlElement(name = "Eula")
	private String eula;

	public OSImage() {
		super();
	}

	public String getAffinityGroup() {
		return affinityGroup;
	}

	public void setAffinityGroup(String affinityGroup) {
		this.affinityGroup = affinityGroup;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getLogicalSizeInGB() {
		return logicalSizeInGB;
	}

	public void setLogicalSizeInGB(Integer logicalSizeInGB) {
		this.logicalSizeInGB = logicalSizeInGB;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMediaLink() {
		return mediaLink;
	}

	public void setMediaLink(String mediaLink) {
		this.mediaLink = mediaLink;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public void setEula(String eula) {
		this.eula = eula;
	}
	
	public String getEula() {
		return eula;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((affinityGroup == null) ? 0 : affinityGroup.hashCode());
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((eula == null) ? 0 : eula.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((logicalSizeInGB == null) ? 0 : logicalSizeInGB.hashCode());
		result = prime * result
				+ ((mediaLink == null) ? 0 : mediaLink.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((os == null) ? 0 : os.hashCode());
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
		OSImage other = (OSImage) obj;
		if (affinityGroup == null) {
			if (other.affinityGroup != null)
				return false;
		} else if (!affinityGroup.equals(other.affinityGroup))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (eula == null) {
			if (other.eula != null)
				return false;
		} else if (!eula.equals(other.eula))
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
		if (logicalSizeInGB == null) {
			if (other.logicalSizeInGB != null)
				return false;
		} else if (!logicalSizeInGB.equals(other.logicalSizeInGB))
			return false;
		if (mediaLink == null) {
			if (other.mediaLink != null)
				return false;
		} else if (!mediaLink.equals(other.mediaLink))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (os == null) {
			if (other.os != null)
				return false;
		} else if (!os.equals(other.os))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OSImage [affinityGroup=" + affinityGroup + ", category="
				+ category + ", label=" + label + ", location=" + location
				+ ", logicalSizeInGB=" + logicalSizeInGB + ", name=" + name
				+ ", mediaLink=" + mediaLink + ", os=" + os + ", eula=" + eula
				+ "]";
	}

}
