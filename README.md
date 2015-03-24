# RestConnector
RestConnector is a test framework for RESTful HTTP and JAVA APIs. It is derived from the practical needs of daily testing work. The design philosophy of RestConnector is inspired and influenced by [spring-test-mvc](https://github.com/spring-projects/spring-test-mvc) and [resteasy-client](https://github.com/resteasy/Resteasy).

RestConnector can conveniently accomplish the testing process for RESTful HTTP servers with additional functionality of testing configuration.

## Basic usage for HTTP testing
The main aim of RestConnector is for achieving a better and convenient experience to test RESTful HTTP web services. Let's take some examples to illustrate the testing process:

```java
 import static TestRequestBuilders.*;

 @Test
 public void test() throws Exception {
     WebTemplate webTemplate = WebTemplateBuilder.defaultConfig().build();
     webTemplate.perform(get("http://localhost:8080/test").param("version", 1).param("token","ignored"));
 }
```

The above code snippets can produce an HTTP request as: GET http://localhost:8080/test?version=1&token=ignored

RestConnector provides most common HTTP tools to build HTTP requests. In addition to the "get()" in the example, post, delete, put, etc. are also supported for real world testing.

## HTTP testing with proxy interface
Besides the basic usage for HTTP testing which is composed of many tedious and duplicate steps to construct a request, RestConnector also provides a more convenient approach to realize the same goal.

See the example below:
```java
import static TestRequestBuilders.*;

 @Test
 public void test() throws Exception {
     WebTemplate webTemplate = WebTemplateBuilder.defaultConfig().build();
     webTemplate.perform(api(TestAPI.class).demo(1, ignored);
 }
```

In this example, we do not build the request manually. Instead, we construct it with a proxied interface: TestAPI. Let's see what it looks like:
```java
@Host(value = "http://localhost", port = 8080)
@Path("/test")
public interface TestAPI {
    @Path("/proxy")
    @GET
    public RequestBuilder demo(@QueryParam("version") int version, @QueryParam("token") String token);
}
```

Here we use JAVA annotations to accomplish the configuration of HTTP request, which are all very self-explained. For instance, @Host can config the request server host and its port and @QueryParms can set corresponding query parameters. And finally the above two code snippets can produce an HTTP request as: GET http://localhost:8080/test/proxy?version=1&token=ignored

In the tool box, RestConnector also provides all common HTTP request configurations which will be explained in JavaDoc in details.

Please be noted that the methods located in proxy interface have to return a RequestBuilder interface or its subclasses in order to make the whole engine work. Should the return value is a RequestBuilder, it is impossible to do any alterations for this request. However if the method returned a subclass, say HttpRequestBuilders, it can leverage the configuration methods, like param, body and header, to more customize the http request.

## HTTP response handlers, matchers and transformers
RestConnector has very similar testing APIs as spring-test-mvc. Therefor it is not surprised that the beautiful fluent APIs of MockMvc have been inherited in RestConnector. To write the entire case in one line is a convenient and possible approach in RestConnector.

In addition to the `andDo`, `andExpect` and `andReturn` which already exist in MockMvc APIs, RestConnector has added an `andTransform` command that can be used to transform json response into an Object, List or Map and also be able to parse the json response into a segment of the original string.

Let's see an example here:
```java
Object object = webTemplate.perform(api(TestAPI.class).demo(1, "passed")).andDo(print())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(jsonPath("$.code", is("10004040"))).andTransform(json().object(Object.class));
```

In this code snippet, first we use `andDo` to print the HTTP process, and then we use `andExpect` to assert the response's contentType to be `application/json;charset=utf-8`, and also assert the target json part should be `10004040`. Finally, the response is transformed to an object with its type, `Object`.

## DB configuration and injection
It is very common that database manipulation is involved in the test case. We use database to generate test data, validate test results and even store test products. RestConnector provides built-in support for Sql and Mongo databases, which are injected into test cases using google guice.

This db support can be applied to both JUnit4 and TestNG, all of which are implemented with Guice integration. And the support is a non-intrusive plugin to the test engine, which will not impact on any behaviours that JUnit or TestNG offer.

First, we see an example using TestNG:
```java
@Test
@SqlDB(url = "testurl1", userName = "user1", password = "pwd1")
@SqlDB(url = "testurl2", userName = "user2", password = "pwd2")
@Listeners(TestNGDBListener.class)
public class GuiceTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Inject
    private JdbcTemplate jdbcTemplate2;


    @Test
    private void testDemo() {
        System.out.println(jdbcTemplate);
        System.out.println(jdbcTemplate2);
    }

    @Test
    private void testDemo2() {
        System.out.println(jdbcTemplate);
        System.out.println(jdbcTemplate2);
    }
}
```

In the above example, the `TestNGDBListener` is used as a TestNG listener to run the case, which actually provides the guice injection of all database internally. We could use `@Inject` to inject a `JdbcTemplate` into the test case, which is an Sql DB manipulating template developed by [Spring-Jdbc](https://github.com/spring-projects/spring-framework).

There is one issue should be pointed out that this database injection is based on the class level, that means the `testDemo` and `testDemo2` have the same instances of `jdbcTemplate` and `jdbcTemplate2`.

The counterpart to JUnit4 is:
```java
@Mongo(host = "localhost", port = 30017, database = "categories")
public class GuiceJUnitTest {
    @Rule
    public JUnit4DBInjectionRule rule = new JUnit4DBInjectionRule();

    @Inject
    private MongoTemplate mongoTemplate;

    @Test
    public void demoTest() {
        System.out.println(mongoTemplate);
    }

    @Test
    public void demoTest2() {
        System.out.println(mongoTemplate);
    }
}
```

As using TestNG, we create a JUnit rule `JUnit4DBInjectionRule` to realize the same functionality like `TestNGDBListener`. The injected mongo processor is a `MongoTemplate` which provides a series mongo manipulation abilities created by [Spring-mongo project](https://github.com/spring-projects/spring-data-mongodb). Please be noted that the injected mongo template is also a class level instance.