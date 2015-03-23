# RestConnector
RestConnector is a test framework for RESTful HTTP and JAVA APIs. It is derived from the practical needs of daily testing work. The design philosophy of RestConnector is inspired and influenced by [spring-test-mvc](https://github.com/spring-projects/spring-test-mvc) and [resteasy-client](https://github.com/resteasy/Resteasy).

RestConnector can conveniently accomplish the testing process for RESTful HTTP servers with additional functionality of testing configuration.

## Basic usage for HTTP testing
The main aim of RestConnector is for achieving a better and convenient experience to test RESTful HTTP web services. Let's take some examples to illustrate the testing process:

```java
 import static com.github.connector.engine.web.request.TestRequestBuilders.*;

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
    public RequestBuilder getBooks(@QueryParam("version") int version, @QueryParam("token") String token);
}
```

Here we use JAVA annotations to accomplish the configuration of HTTP request, which are all very self-explained. For instance, @Host can config the request server host and its port and @QueryParms can set corresponding query parameters. And finally the above two code snippets can produce an HTTP request as: http://localhost:8080/test/proxy?version=1&token=ignored