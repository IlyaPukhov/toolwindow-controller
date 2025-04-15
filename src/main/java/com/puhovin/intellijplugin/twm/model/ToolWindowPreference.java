/*
 * Copyright 2007 Mark Scott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.puhovin.intellijplugin.twm.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Mark Scott
 * @version $Id: ToolWindowPreference.java 31 2007-06-01 23:01:54Z mark $
 */
public class ToolWindowPreference implements Comparable<ToolWindowPreference>, Serializable {
	private String id = "";
	private AvailabilityPreference availabilityPreference = AvailabilityPreference.UNAFFECTED;

	public ToolWindowPreference() {
	}

	public ToolWindowPreference(@NotNull final String id, @NotNull AvailabilityPreference availabilityPreference) {
		this.id = id;
		this.availabilityPreference = availabilityPreference;
	}

	@NotNull
	public String getId() {
		return id;
	}

	public @NotNull AvailabilityPreference getAvailabilityPreference() {
		return availabilityPreference;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAvailabilityPreference(AvailabilityPreference availabilityPreference) {
		this.availabilityPreference = availabilityPreference;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		ToolWindowPreference that = (ToolWindowPreference) obj;

		if (availabilityPreference != that.availabilityPreference) {
			return false;
		}

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + availabilityPreference.hashCode();

		return result;
	}

	public int compareTo(ToolWindowPreference o) {
		if (o == null) {
			throw new NullPointerException("Cannot compare to null");
		}

		return id.compareTo(o.id);
	}
}
