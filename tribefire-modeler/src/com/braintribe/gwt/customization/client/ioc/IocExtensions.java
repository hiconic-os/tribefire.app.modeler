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
package com.braintribe.gwt.customization.client.ioc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.braintribe.gwt.ioc.gme.client.IocExtensionPoints;
import com.braintribe.gwt.ioc.gme.client.Runtime;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.workbench.HyperlinkAction;
import com.braintribe.provider.SingletonBeanProvider;

public class IocExtensions extends IocExtensionPoints {

	@Override
	public Supplier<List<HyperlinkAction>> topBannerLinks() {
		return topBannerLinks;
	}

	public static Supplier<List<HyperlinkAction>> topBannerLinks = new SingletonBeanProvider<List<HyperlinkAction>>() {
		@Override
		public List<HyperlinkAction> create() throws Exception {
			List<HyperlinkAction> bean = publish(new ArrayList<HyperlinkAction>());
			bean.add(explorerLink.get());
			bean.add(servicesLink.get());
			bean.add(documentationLink.get());
			return bean;
		}
	};

	public static Supplier<HyperlinkAction> explorerLink = new SingletonBeanProvider<HyperlinkAction>() {
		@Override
		public HyperlinkAction create() throws Exception {
			HyperlinkAction bean = publish(HyperlinkAction.T.create());

			bean.setDisplayName(wrapWithLocalizedString(LocalizedText.INSTANCE.tribefireExplorer()));
			bean.setTarget("_blank");
			bean.setUrl(Runtime.tribefireExplorerUrl.get());

			return bean;
		}
	};

	public static Supplier<HyperlinkAction> servicesLink = new SingletonBeanProvider<HyperlinkAction>() {
		@Override
		public HyperlinkAction create() throws Exception {
			HyperlinkAction bean = publish(HyperlinkAction.T.create());

			bean.setDisplayName(wrapWithLocalizedString(LocalizedText.INSTANCE.tribefireServices()));
			bean.setTarget("_blank");
			bean.setUrl(Runtime.tribefireServicesUrl.get());

			return bean;
		}
	};

	public static Supplier<HyperlinkAction> documentationLink = new SingletonBeanProvider<HyperlinkAction>() {
		@Override
		public HyperlinkAction create() throws Exception {
			HyperlinkAction bean = publish(HyperlinkAction.T.create());

			bean.setDisplayName(wrapWithLocalizedString(LocalizedText.INSTANCE.tribefireDocumentation()));
			bean.setTarget("_blank");
			bean.setUrl(Runtime.tribefireDocumentationUrl.get());

			return bean;
		}
	};	
	
	private static LocalizedString wrapWithLocalizedString(String value) {
		LocalizedString result = LocalizedString.T.create();
		result.getLocalizedValues().put("default", value);
		return result;
	}

}
