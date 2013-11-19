package com.google.inject.servlet3;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet3.util.Classes;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Milan Baran (milan.baran@gmail.com) Date: 11/19/13 Time: 11:10 AM
 */
public abstract class Servlet3Module extends ServletModule {

  private final List<Package> packages = Lists.newArrayList();
  private Boolean scanned = Boolean.FALSE;

  @Override
  protected void configureServlets() {
    super.configureServlets();
    configureServlets3();
    configureWebFilters();
  }

  protected abstract void configureServlets3();

  protected void configureWebFilters() {
    Set<Class<?>> viewClasses = new HashSet<Class<?>>();
    for(Package p : packages) {
      for(Class<?> filterClass :
          Classes.matching(
              Matchers.annotatedWith(WebFilter.class)
          ).in(p)) {
        if(Filter.class.isAssignableFrom(filterClass)) {
          Class<Filter> filterClazz = (Class<Filter>)filterClass;
          WebFilter webFilter = filterClass.getAnnotation(WebFilter.class);
          String[] urlPatterns = webFilter.urlPatterns();
          if(urlPatterns.length > 1) {
            filter(urlPatterns[0], Arrays.copyOfRange(urlPatterns,1,urlPatterns.length))
              .through(filterClazz,getInitParams(webFilter));
          } else {
            if(urlPatterns.length > 0) {
              filter(urlPatterns[0]).through(filterClazz, getInitParams(webFilter));
            } else {
              addError("Guice found a WebFilter %s with no urlPatterns defined.",webFilter.getClass().getCanonicalName());
            }
          }
        }
      }
    }
  }

  private Map<String,String> getInitParams(WebFilter webFilter) {
    checkNotNull(webFilter);
    checkNotNull(webFilter.initParams());
    final WebInitParam[] params = webFilter.initParams();
    final Map<String,String> initParams = Maps.newHashMapWithExpectedSize(params.length);
    for(int i=0; i < params.length; i++) {
      WebInitParam w = params[i];
      initParams.put(w.name(),w.value());
    }
    return initParams;
  }

  protected void scanFilters(Package pack) {
    Preconditions.checkArgument(null != pack, "Package parameter to scan() cannot be null");
    packages.add(pack);
    scanned = Boolean.TRUE;
  }

  private static void checkForRuntimeRetention(
      Class<? extends Annotation> annotationType) {
    Retention retention = annotationType.getAnnotation(Retention.class);
    checkArgument(retention != null && retention.value() == RetentionPolicy.RUNTIME,
                  "Annotation " + annotationType.getSimpleName() + " is missing RUNTIME retention");
  }

  private static class AnnotatedWithType extends AbstractMatcher<AnnotatedElement>
      implements Serializable {
    private final Class<? extends Annotation> annotationType;

    public AnnotatedWithType(Class<? extends Annotation> annotationType) {
      this.annotationType = checkNotNull(annotationType, "annotation type");
      checkForRuntimeRetention(annotationType);
    }

    public boolean matches(AnnotatedElement element) {
      return element.getAnnotation(annotationType) != null;
    }

    @Override public boolean equals(Object other) {
      return other instanceof AnnotatedWithType
             && ((AnnotatedWithType) other).annotationType.equals(annotationType);
    }

    @Override public int hashCode() {
      return 37 * annotationType.hashCode();
    }

    @Override public String toString() {
      return "annotatedWith(" + annotationType.getSimpleName() + ".class)";
    }

    private static final long serialVersionUID = 0;
  }
}
