package org.eclipse.concierge.test.support;

import org.osgi.service.component.ComponentContext;

public class ComponentImpl implements Service {

	public void activate(ComponentContext componentContext) {
		Monitor.noOfCallsOfActivate++;
		Monitor.addCall("ComponentImpl.activate");
	}

	public void deactivate(ComponentContext componentContext) {
		Monitor.addCall("ComponentImpl.deactivate");
		Monitor.noOfCallsOfDeactivate++;
	}

	@Override
	public boolean isServiceOK() {
		return true;
	}

}
