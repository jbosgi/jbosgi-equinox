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
package org.jboss.osgi.equinox;

//$Id: FelixIntegration.java 91762 2009-07-29 12:14:37Z thomas.diesler@jboss.com $

import java.util.Map;

import org.jboss.osgi.deployment.DeploymentActivator;
import org.jboss.osgi.spi.framework.FrameworkIntegrationBean;
import org.jboss.osgi.spi.util.ServiceLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Equinox specific OSGi Framework integration.
 * 
 * @author thomas.diesler@jboss.com
 * @since 23-Jan-2009
 */
public class EquinoxIntegration extends FrameworkIntegrationBean
{
   // Provide logging
   final Logger log = LoggerFactory.getLogger(EquinoxIntegration.class);
   
   private DeploymentActivator deploymentActivator;

   @Override
   protected Framework createFramework(Map<String, Object> properties)
   {
      // Log INFO about this implementation
      String implTitle = getClass().getPackage().getImplementationTitle();
      String impVersion = getClass().getPackage().getImplementationVersion();
      log.info(implTitle + " - " + impVersion);

      // Load the framework instance
      FrameworkFactory factory = ServiceLoader.loadService(FrameworkFactory.class);
      return factory.newFramework(properties);
   }

   @Override
   protected void registerSystemServices(BundleContext context)
   {
      deploymentActivator = new DeploymentActivator();
      deploymentActivator.start(context);
   }

   @Override
   protected void unregisterSystemServices(BundleContext context)
   {
      if (deploymentActivator != null)
         deploymentActivator.stop(context);
   }
}