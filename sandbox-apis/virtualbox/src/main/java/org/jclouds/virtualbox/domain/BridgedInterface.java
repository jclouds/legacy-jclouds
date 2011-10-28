package org.jclouds.virtualbox.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

public class BridgedInterface {

	private String name;
	private String guid;
	private String dhcp;
	private String ipAddress;
	private String networkMask;
	private String ipV6Address;
	private String ipV6NetworkMaskPrefixLength;
	private String hardwareAddress;
	private String mediumType;
	private String status;
	private String vBoxNetworkName;

	public BridgedInterface() {
		super();
	}

	public BridgedInterface(String bridgedInterface) {
		System.out.println(bridgedInterface);
		String pattern = Pattern.compile(": ").toString();
		Map<String, String> map = Splitter.on("\n").withKeyValueSeparator(pattern).split(bridgedInterface);
		List<String> trimmedValues = new ArrayList<String>();
		for (String string : map.values()) {
			trimmedValues.add(string.trim());
		}
		this.name = trimmedValues.get(0);
		this.guid = trimmedValues.get(1);
		this.dhcp = trimmedValues.get(2);
		this.ipAddress = trimmedValues.get(3);
		this.networkMask = trimmedValues.get(4);
		this.ipV6Address = trimmedValues.get(5);
		this.ipV6NetworkMaskPrefixLength = trimmedValues.get(6);
		this.hardwareAddress = trimmedValues.get(7);
		this.mediumType = trimmedValues.get(8);
		this.status = trimmedValues.get(9);
		this.vBoxNetworkName = trimmedValues.get(10);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getDhcp() {
		return dhcp;
	}

	public void setDhcp(String dhcp) {
		this.dhcp = dhcp;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNetworkMask() {
		return networkMask;
	}

	public void setNetworkMask(String networkMask) {
		this.networkMask = networkMask;
	}

	public String getIpV6Address() {
		return ipV6Address;
	}

	public void setIpV6Address(String ipV6Address) {
		this.ipV6Address = ipV6Address;
	}

	public String getIpV6NetworkMaskPrefixLength() {
		return ipV6NetworkMaskPrefixLength;
	}

	public void setIpV6NetworkMaskPrefixLength(String ipV6NetworkMaskPrefixLength) {
		this.ipV6NetworkMaskPrefixLength = ipV6NetworkMaskPrefixLength;
	}

	public String getHardwareAddress() {
		return hardwareAddress;
	}

	public void setHardwareAddress(String hardwareAddress) {
		this.hardwareAddress = hardwareAddress;
	}

	public String getMediumType() {
		return mediumType;
	}

	public void setMediumType(String mediumType) {
		this.mediumType = mediumType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getvBoxNetworkName() {
		return vBoxNetworkName;
	}

	public void setvBoxNetworkName(String vBoxNetworkName) {
		this.vBoxNetworkName = vBoxNetworkName;
	}


	public static void main(String[] args) {
		String bridgedInterface =
				 "Name:            eth0\nGUID:            30687465-0000-4000-8000-00261834d0cb\nDhcp:            Disabled\nIPAddress:       209.x.x.x\nNetworkMask:     255.255.255.0\nIPV6Address:     fe80:0000:0000:0000:0226:18ff:fe34:d0cb\nIPV6NetworkMaskPrefixLength: 64\nHardwareAddress: 00:26:18:34:d0:cb\nMediumType:      Ethernet\nStatus:          Up\nVBoxNetworkName: HostInterfaceNetworking-eth0";
		BridgedInterface bi = new BridgedInterface(bridgedInterface);
		System.out.println(bi.getName());
	}
}