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
package org.jboss.osgi.equinox.framework;

//$Id$

import static org.eclipse.core.runtime.adaptor.LocationManager.PROP_INSTALL_AREA;
import static org.jboss.osgi.spi.Constants.OSGI_SERVER_HOME;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.osgi.framework.internal.core.Framework;
import org.eclipse.osgi.framework.internal.core.FrameworkProperties;
import org.jboss.logging.Logger;
import org.jboss.osgi.spi.FrameworkException;
import org.jboss.osgi.spi.framework.OSGiFramework;
import org.jboss.osgi.spi.logging.ExportedPackageHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * The OSGiFramework for Eclipse Equinox
 * 
 * @author thomas.diesler@jboss.com
 * @since 01-Apr-2009
 */
public class EquinoxIntegration implements OSGiFramework
{
   // Provide logging
   final Logger log = Logger.getLogger(EquinoxIntegration.class);

   private Map<String, Object> properties = new HashMap<String, Object>();
   private List<URL> autoInstall = new ArrayList<URL>();
   private List<URL> autoStart = new ArrayList<URL>();

   private Framework framework;

   public Map<String, Object> getProperties()
   {
      return properties;
   }

   public void setProperties(Map<String, Object> props)
   {
      this.properties = props;
   }

   public List<URL> getAutoInstall()
   {
      return autoInstall;
   }

   public void setAutoInstall(List<URL> autoInstall)
   {
      this.autoInstall = autoInstall;
   }

   public List<URL> getAutoStart()
   {
      return autoStart;
   }

   public void setAutoStart(List<URL> autoStart)
   {
      this.autoStart = autoStart;
   }

   public Bundle getBundle()
   {
      assertFrameworkStart();
      return framework.getBundle(0);
   }

   public BundleContext getBundleContext()
   {
      return getBundle().getBundleContext();
   }

   public void create()
   {
      String implVersion = getClass().getPackage().getImplementationVersion();
      log.info("OSGi Integration Equinox - " + implVersion);

      // Push configured props to FrameworkProperties 
      System.setProperty("osgi.framework.useSystemProperties", "false");
      Iterator<String> itKeys = properties.keySet().iterator();
      while (itKeys.hasNext())
      {
         String key = itKeys.next();
         Object value = properties.get(key);
         if (value instanceof String)
         {
            FrameworkProperties.setProperty(key, (String)value);
         }
      }

      // Prevent a NPE when the config area is not set
      if (FrameworkProperties.getProperty(PROP_INSTALL_AREA) == null)
      {
         String osgiHome = FrameworkProperties.getProperty(OSGI_SERVER_HOME);
         if (osgiHome != null)
         {
            FrameworkProperties.setProperty(PROP_INSTALL_AREA, osgiHome + "/data/equinox");
         }
         else
         {
            log.warn("Cannot find value for property '" + PROP_INSTALL_AREA + "' nor for '" + OSGI_SERVER_HOME + "'");
            String userHome = FrameworkProperties.getProperty("user.home");
            FrameworkProperties.setProperty(PROP_INSTALL_AREA, userHome + "/equinox");
         }
      }
      
      // Init the Framework
      framework = new Framework(new FrameworkAdaptorImpl());
   }

   public void start()
   {
      assertFrameworkCreate();
      
      // Start the Framework
      framework.launch();
      
      // Get system bundle context
      BundleContext context = getBundleContext();
      if (context == null)
         throw new FrameworkException("Cannot obtain system context");

      // Log the the framework packages
      ExportedPackageHelper packageHelper = new ExportedPackageHelper(context);
      packageHelper.logExportedPackages(getBundle());
      
      Map<URL, Bundle> autoBundles = new HashMap<URL, Bundle>();

      // Add the autoStart bundles to autoInstall
      for (URL bundleURL : autoStart)
      {
         autoInstall.add(bundleURL);
      }

      // Install autoInstall bundles
      for (URL bundleURL : autoInstall)
      {
         try
         {
            Bundle bundle = context.installBundle(bundleURL.toString());
            log.info("Installed bundle: " + bundle.getSymbolicName());
            autoBundles.put(bundleURL, bundle);
         }
         catch (BundleException ex)
         {
            stop();
            throw new IllegalStateException("Cannot install bundle: " + bundleURL, ex);
         }
      }

      // Start autoStart bundles
      for (URL bundleURL : autoStart)
      {
         try
         {
            Bundle bundle = autoBundles.get(bundleURL);
            bundle.start();
            packageHelper.logExportedPackages(bundle);
            log.info("Started bundle: " + bundle.getSymbolicName());
         }
         catch (BundleException ex)
         {
            stop();
            throw new IllegalStateException("Cannot install bundle: " + bundleURL, ex);
         }
      }
   }

   public void stop()
   {
      if (framework != null)
      {
         framework.shutdown();
      }
   }

   private void assertFrameworkCreate()
   {
      if (framework == null)
         create();
   }

   private void assertFrameworkStart()
   {
      assertFrameworkCreate();
      if ((framework.getBundle(0).getState() & Bundle.ACTIVE) == 0)
         start();
   }
}