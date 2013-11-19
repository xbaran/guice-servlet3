package com.google.inject.servlet3.test;

import com.google.inject.Singleton;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

@Singleton
    @WebFilter(
        filterName="FooFilter",
        urlPatterns={"/foo.jsp", "/foo/*"},
        initParams = {
                    @WebInitParam(name="foo", value="Hello "),
                    @WebInitParam(name="bar", value=" World!")
                 })
    public class FooFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void destroy() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}