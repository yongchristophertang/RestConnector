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

package com.github.yongchristophertang.config.junit;

import com.github.yongchristophertang.config.Property;
import com.github.yongchristophertang.config.PropertyHandler;
import com.github.yongchristophertang.config.PropertyConfig;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * JUnit4 Rule for property injection.
 *
 * @author Yong Tang
 * @since 1.0
 */
public class JUnit4PropertyInjectionRule implements MethodRule {
    /**
     * The rule that is about to load properties from {@link PropertyConfig}s and set values
     * to {@link Property}s.
     */
    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        PropertyHandler.loadProperties(method.getDeclaringClass(), new Object[]{target});
        return base;
    }
}
