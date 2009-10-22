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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jboss.osgi.deployment.deployer.DeployerService;
import org.jboss.osgi.spi.framework.OSGiBootstrap;
import org.jboss.osgi.spi.framework.OSGiBootstrapProvider;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

/**
 * Test OSGi System bundle access
 * 
 * @author thomas.diesler@jboss.com
 * @since 27-Jul-2009
 */
public class OSGiBootstrapTestCase 
{
   @Test
   public void testFrameworkLaunch() throws Exception
   {
      OSGiBootstrapProvider bootProvider = OSGiBootstrap.getBootstrapProvider();
      Framework framework = bootProvider.getFramework();
      
      assertEquals("BundleId == 0", 0, framework.getBundleId());
      assertEquals("SymbolicName", "org.eclipse.osgi", framework.getSymbolicName());
      
      framework.start();
      try
      {
         BundleContext context = framework.getBundleContext();
         ServiceReference sref = context.getServiceReference(DeployerService.class.getName());
         assertNotNull("DeployerService not null", sref);
      }
      finally
      {
         framework.stop();
         framework.waitForStop(1000);
      }
   }
}