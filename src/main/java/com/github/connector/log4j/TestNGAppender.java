/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.connector.log4j;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.testng.Reporter;

import java.time.LocalDateTime;
import java.util.Optional;

@Plugin(name = "TestNGAppender", category = "Core", elementType = "appender", printObject = true)
public final class TestNGAppender extends AbstractAppender {
	private static final String LogFormat = "%s %s [%s] %s >> %s";

	private TestNGAppender(String name, Layout layout, Filter filter) {
		super(name, filter, layout);
	}

	
	@PluginFactory
	public static TestNGAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout layout,
			@PluginElement("Filters") Filter filter) {

		return new TestNGAppender(name, Optional.ofNullable(layout).orElse(
				PatternLayout.createDefaultLayout()), filter);
	}

	public void append(LogEvent event) {
		String message = String.format(LogFormat, event.getLevel().toString(),
				LocalDateTime.now().toString(), event.getThreadName(),
				event.getLoggerName(), event.getMessage().getFormattedMessage());
		Reporter.log(message);
	}

}
