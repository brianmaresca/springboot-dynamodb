package example.springbootdynamodb.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestIdLoggingFilter extends OncePerRequestFilter {

  public static final String KEY = "x-request-id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    long startTime = System.currentTimeMillis();

    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String attr = headerNames.nextElement();
      log.trace("header field: {} with value: {}", attr, request.getHeader(attr));
    }

    HttpServletRequest thisRequest = request;
    if (StringUtils.isBlank(request.getHeader(KEY))
        && !StringUtils.endsWithIgnoreCase(request.getRequestURI(), "health")) {
      String requestId = generateRequestId();
      thisRequest = new MutableHttpServletRequest(request);
      ((MutableHttpServletRequest) thisRequest).putHeader(KEY, requestId);
      MDC.put(KEY, thisRequest.getHeader(KEY));
      response.addHeader(KEY, requestId);

    } else if (request.getHeader(KEY) != null) {
      MDC.put(KEY, request.getHeader(KEY));
    }

    try {
      filterChain.doFilter(thisRequest, response);
    } finally {
      // dont bother logging successful health checks
      if (!StringUtils.endsWithIgnoreCase(request.getRequestURI(), "health")
          || response.getStatus() != 200) {
        log.info(
            "{} {} statusCode={}, duration: {}ms",
            thisRequest.getMethod(),
            thisRequest.getRequestURI(),
            response.getStatus(),
            System.currentTimeMillis() - startTime);
      }

      MDC.remove(KEY);
    }
  }

  protected String generateRequestId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  static final class MutableHttpServletRequest extends HttpServletRequestWrapper {

    // holds custom header and value mapping
    private final Map<String, String> customHeaders;

    public MutableHttpServletRequest(HttpServletRequest request) {
      super(request);
      this.customHeaders = new HashMap<>();
    }

    public void putHeader(String name, String value) {
      this.customHeaders.put(name.toLowerCase(), value);
    }

    public String getHeader(String name) {
      return Optional.ofNullable(customHeaders) // check the custom headers first
          .map(hdrs -> hdrs.get(name.toLowerCase()))
          .orElseGet(
              () ->
                  ((HttpServletRequest) getRequest())
                      .getHeader(name)); // else return from into the original wrapped object
    }

    public Enumeration<String> getHeaderNames() {
      // create a set of the custom header names
      Set<String> set = new HashSet<>(customHeaders.keySet());

      // now add the headers from the wrapped request object
      Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
      while (e.hasMoreElements()) {
        // add the names of the request headers into the list
        String n = e.nextElement();
        set.add(n);
      }

      // create an enumeration from the set and return
      return Collections.enumeration(set);
    }
  }
}
