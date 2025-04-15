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

import com.puhovin.intellijplugin.twm.ToolWindowManagerBundle;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Mark Scott
 * @version $Id: AvailabilityPreference.java 31 2007-06-01 23:01:54Z mark $
 */
public enum AvailabilityPreference implements Serializable {
	AVAILABLE(ToolWindowManagerBundle.message("preference.available")),
	UNAFFECTED(ToolWindowManagerBundle.message("preference.unaffected")),
	UNAVAILABLE(ToolWindowManagerBundle.message("preference.unavailable"));

	private final String text;

	private AvailabilityPreference(@NotNull final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}
