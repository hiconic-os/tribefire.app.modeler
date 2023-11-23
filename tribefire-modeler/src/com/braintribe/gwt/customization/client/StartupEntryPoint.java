// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.gwt.customization.client;

import java.util.function.Supplier;

import com.braintribe.gwt.async.client.RuntimeConfiguration;
import com.braintribe.gwt.browserfeatures.client.UrlParameters;
import com.braintribe.gwt.customization.client.ioc.IocExtensions;
import com.braintribe.gwt.customizationui.client.GwtLocaleProvider;
import com.braintribe.gwt.customizationui.client.startup.CustomizationStartup;
import com.braintribe.gwt.customizationui.client.startup.GmeEntryPoint;
import com.braintribe.gwt.customizationui.client.startup.TribefireRuntime;
import com.braintribe.gwt.gxt.gxtresources.whitemask.client.MaskController;
import com.braintribe.gwt.ioc.gme.client.IocExtensionPoints;
import com.braintribe.gwt.ioc.gme.client.ModelerNew;
import com.braintribe.gwt.ioc.gme.client.Notification;
import com.braintribe.gwt.ioc.gme.client.Providers;
import com.braintribe.gwt.ioc.gme.client.Runtime;
import com.braintribe.gwt.ioc.gme.client.Startup;
import com.braintribe.gwt.ioc.gme.client.expert.LoadModelViaResourceConfig;
import com.braintribe.gwt.ioc.gme.client.expert.LoadModelViaResourceHandler;
import com.braintribe.gwt.security.client.SessionScope;
import com.braintribe.gwt.tribefirejs.client.tools.StaticTools;
import com.braintribe.provider.SingletonBeanProvider;
import com.braintribe.utils.i18n.I18nTools;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StartupEntryPoint extends GmeEntryPoint {	
	
	static {
		// force usage of the class (Gxt problem on Dev. Mode - Mac)
		Container.class.getName();
		StaticTools.class.getName();
	}
	
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		CustomizationStartup startup = new CustomizationStartup();
		
		boolean offline = 
				UrlParameters.getInstance().getParameter("offline") != null ? 
				UrlParameters.getInstance().getParameter("offline").equalsIgnoreCase("true") : 
				false;
				
		Runtime.useCommit = !offline;		
		com.braintribe.gwt.ioc.gme.client.Runtime.mainPanelProvider = ModelerNew.standAloneModeler;
		Notification.useLoadModel = true;
		
		IocExtensionPoints.INSTANCE = new IocExtensions();
		I18nTools.localeProvider = new GwtLocaleProvider();
		CustomizationStartup.start(Providers.packagingProvider); //Needed to start static configurations within the CustomizationStartup.
		
		String applicationId = RuntimeConfiguration.getInstance().getProperty("tribefireModelerName", "tribefire-modeler");
		Runtime.setApplicationId(applicationId);
		
		String logoUrl = TribefireRuntime.getProperty("TRIBEFIRE_LOGO_URL", null, true);
		String servicesUrl = Runtime.tribefireServicesAbsoluteUrl.get();
		String accessId = Runtime.accessId.get();
		
		MaskController.setProgressBarImage(logoUrl != null ? logoUrl : getDefaultLogoUrl(servicesUrl, accessId, applicationId));
		String title = TribefireRuntime.getProperty("TRIBEFIRE_LOADING_TITLE", null, true);
		if (title != null) {
			MaskController.setProgressBarTitle(title);
			titleSet = true;
		}
		logoSet = logoUrl != null;		
		
		if (offline) {
			startup.startCustomization(offlineModeler());
		}
		else {			
			startup.startCustomization(Startup.customization, false);
			if (!titleSet || !logoSet)
				waitForLogoAndText(servicesUrl, accessId, applicationId);
		}
	}
		
	private Supplier<Viewport> offlineModeler(){
		return new SingletonBeanProvider<Viewport>() {			
			@Override
			public Viewport create() throws Exception {
				try {
					Element loading = Document.get().getElementById("loading");
					loading.getParentElement().removeChild(loading);
					RootPanel.get().clear();
					
					SessionScope scope = new SessionScope();
					SessionScope.scopeManager.openAndPushScope(scope);
									
					LoadModelViaResourceHandler handler = com.braintribe.gwt.ioc.gme.client.Notification.loadModelViaResourceHandler.get();
					
					Viewport viewport = new Viewport();
					viewport.setBorders(false);
				
					viewport.add(handler.getModelerSupplier().get());
					RootPanel.get().add(viewport);
					
					LoadModelViaResourceConfig config = LoadModelViaResourceConfig.T.create();
					config.setUrl(UrlParameters.getInstance().getParameter("url"));
//					config.setCodec(UrlParameters.getInstance().getParameter("codec"));
					
					handler.onNotificationReceived(config);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}			
		};	
		
	}
	
}
