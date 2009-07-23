/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.osgi.equinox;

//$Id$

import static org.junit.Assert.*;

import org.jboss.osgi.equinox.framework.EquinoxIntegration;
import org.jboss.osgi.spi.framework.OSGiBootstrap;
import org.jboss.osgi.spi.framework.OSGiBootstrapProvider;
import org.jboss.osgi.spi.framework.OSGiFramework;
import org.jboss.osgi.spi.testing.OSGiTest;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 * Test OSGi System bundle access
 * 
 * @author thomas.diesler@jboss.com
 * @since 23-Jan-2009
 */
public class SystemBundleTestCase extends OSGiTest
{
   @Test
   public void testEquinoxIntegration()
   {
      EquinoxIntegration framework = new EquinoxIntegration();
      framework.create();
      framework.start();
      
      Bundle bundle = framework.getBundle();

      assertEquals("BundleId == 0", 0, bundle.getBundleId());
      assertEquals("SymbolicName", "org.eclipse.osgi", bundle.getSymbolicName());
   }
   
   @Test
   public void testSystemBundle()
   {
      OSGiBootstrapProvider configProvider = OSGiBootstrap.getBootstrapProvider();
      OSGiFramework framework = configProvider.getFramework();
      Bundle bundle = framework.getBundle();

      assertEquals("BundleId == 0", 0, bundle.getBundleId());
      assertEquals("SymbolicName", "org.eclipse.osgi", bundle.getSymbolicName());
   }
}