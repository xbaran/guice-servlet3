import com.google.inject.servlet3.Servlet3Module;
import com.google.inject.servlet3.test.FooFilter;

import org.testng.annotations.Test;

/**
 * Author: Milan Baran (milan.baran@gmail.com) Date: 11/19/13 Time: 1:57 PM
 */
public class ConfigurationTest {

  @Test
  public void testConfiguration() throws Exception {
    new Servlet3Module() {
      @Override
      protected void configureServlets3() {
        scanFilters(FooFilter.class.getPackage());
      }
    };
  }
}
