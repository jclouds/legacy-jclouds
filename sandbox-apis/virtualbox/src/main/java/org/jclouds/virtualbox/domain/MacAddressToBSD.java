package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public enum MacAddressToBSD implements Function<String, String> {

	INSTANCE;

	@Override
	public String apply(String macAddress) {
	      checkArgument(macAddress.length() == 17);
	   return  Joiner.on(":").join(
				Iterables.transform(Splitter.on(":").split(macAddress),
						new Function<String, String>() {
					@Override
					public String apply(String arg0) {
						if (arg0.equals("00"))
							return "0";
						if (arg0.startsWith("0"))
							return arg0.substring(1);

						return arg0;
					}

				}));
	}

}