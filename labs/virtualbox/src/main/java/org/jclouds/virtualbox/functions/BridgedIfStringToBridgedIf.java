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

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.virtualbox.domain.BridgedIf;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@Singleton
public class BridgedIfStringToBridgedIf implements Function<String, BridgedIf> {

	private static final String BRIDGED_IF_STATUS = "Status";
	private static final String BRIDGED_IF_MEDIUM_TYPE = "MediumType";
	private static final String BRIDGED_IF_NETWORK_MASK = "NetworkMask";
	private static final String BRIDGED_IF_IP_ADDRESS = "IPAddress";
	private static final String BRIDGED_IF_GUID = "GUID";
	private static final String BRIDGED_IF_NAME = "Name";

	@Inject
	public BridgedIfStringToBridgedIf() {
	}

	@Override
	public BridgedIf apply(String rawBridgedIf) {
		checkNotNull(rawBridgedIf, "bridged interface can't be null");

		String transformedBridgedIf = transformRawBridgedIf(rawBridgedIf);
		Map<String, String> bridgedIfMap = Splitter.on("\n")
				.omitEmptyStrings().withKeyValueSeparator("=")
				.split(transformedBridgedIf);

		return BridgedIf
				.builder()
				.name(getValueFromMap(bridgedIfMap, BRIDGED_IF_NAME))
				.guid(getValueFromMap(bridgedIfMap, BRIDGED_IF_GUID))
				.ip(getValueFromMap(bridgedIfMap, BRIDGED_IF_IP_ADDRESS))
				.networkMask(getValueFromMap(bridgedIfMap, BRIDGED_IF_NETWORK_MASK))
				.mediumType(getValueFromMap(bridgedIfMap, BRIDGED_IF_MEDIUM_TYPE))
				.status(getValueFromMap(bridgedIfMap, BRIDGED_IF_STATUS))
				.build();
	}

	private String getValueFromMap(Map<String, String> map, String key) {
		return map.get(key).trim();
	}

	/**
	 * This is an helper to simplify the split step of the raw bridgedIf
	 * Substitute first ':' with '='
	 * 
	 * @param rawBridgedIf
	 * @return
	 */
	private String transformRawBridgedIf(String rawBridgedIf) {
		Iterable<String> transformedLines = Iterables.transform(
				Splitter.on("\n").split(rawBridgedIf),
				new Function<String, String>() {

					@Override
					public String apply(String line) {
						return line.replaceFirst(":", "=");
					}

				});

		StringBuilder stringBuilder = new StringBuilder();

		for (String line : transformedLines) {
			stringBuilder.append(line + "\n");
		}
		return stringBuilder.toString();
	}

}
