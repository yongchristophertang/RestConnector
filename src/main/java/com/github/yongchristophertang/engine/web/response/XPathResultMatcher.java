/*
 *  Copyright 2014-2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.yongchristophertang.engine.web.response;

import com.github.yongchristophertang.engine.web.ResultMatcher;
import javaslang.control.Try;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * XPath result matcher for XML validation
 *
 * @author Yong Tang
 * @since 0.5
 */
public class XPathResultMatcher {
    private DocumentBuilder builder;
    private XPathExpression xPath;

    protected XPathResultMatcher(String expression) throws ParserConfigurationException, XPathExpressionException {
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xPath = XPathFactory.newInstance().newXPath().compile(expression);
    }

    public ResultMatcher value(final Matcher<String> mathcher) {
        return result -> MatcherAssert
            .assertThat("XPath: ", xPath.evaluate(builder.parse(result.getResponseStringContent())), mathcher);
    }

    public ResultMatcher value(final String value) {
        return value(equalTo(value));
    }

    public ResultMatcher exists() {
        return result ->
            Try.of(() -> xPath.evaluate(builder.parse(result.getResponseStringContent())))
                .onFailure(t -> MatcherAssert.assertThat("The xpath evaluation does not exist", true, equalTo(false)));
    }

    public ResultMatcher doesNotExist() {
        return result -> Try.of(() -> xPath.evaluate(builder.parse(result.getResponseStringContent())))
            .onSuccess(s -> MatcherAssert.assertThat("The xpath evaluation exists", true, equalTo(false)));
    }
}
