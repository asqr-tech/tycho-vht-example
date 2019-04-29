package org.eclipse.vtp.desktop.model.core.internal.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IExitBroadcastReceiver;

public class DesignTraversalHelper {

	public static <T> List<T> getDesignElements(Design design, Class<T> type) {
		List<T> ret = new ArrayList<T>();
		for (IDesignElement designElement : design.getDesignElements()) {
			T object = (T) designElement.getAdapter(type);
			if (object != null)
				ret.add(object);
		}
		return ret;
	}

	public static <T> List<T> getUpStreamDesignElements(IDesignElement startElement, Class<T> destinationType) {
		List<T> ret = new ArrayList<T>();
		Map<String, IDesignElement> visited = new HashMap<String, IDesignElement>();
		visited.put(startElement.getId(), startElement);
		getUpStreamDesignElements0(startElement, destinationType, ret, visited);
		return ret;
	}

	private static <T> void getUpStreamDesignElements0(IDesignElement startElement, Class<T> destinationType,
			List<T> elements, Map<String, IDesignElement> visited) {
		List<IDesignConnector> connectors = startElement.getIncomingConnectors();
		for (IDesignConnector connector : connectors) {
			IDesignElement sourceElement = connector.getOrigin();
			T adapter = (T) sourceElement.getAdapter(destinationType);
			if (adapter != null)
				elements.add(adapter);
			if (visited.get(sourceElement.getId()) == null) {
				visited.put(sourceElement.getId(), sourceElement);
				getUpStreamDesignElements0(sourceElement, destinationType, elements, visited);
			}
		}
		if (startElement.getDesign() == startElement.getDesign().getDocument().getMainDesign()) {
			List<IExitBroadcastReceiver> receivers = startElement.getExitBroadcastReceivers();
			if (!receivers.isEmpty()) {
				for (IDesignElement sourceElement : startElement.getDesign().getDesignElements()) {
					for (IDesignElementConnectionPoint point : sourceElement.getConnectorRecords()) {
						if (point.getDesignConnector() == null) {
							for (IExitBroadcastReceiver receiver : receivers) {
								if (point.getName().equals(receiver.getExitPattern())) {
									T adapter = (T) sourceElement.getAdapter(destinationType);
									if (adapter != null)
										elements.add(adapter);
									if (visited.get(sourceElement.getId()) == null) {
										visited.put(sourceElement.getId(), sourceElement);
										getUpStreamDesignElements0(sourceElement, destinationType, elements, visited);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static <T> List<T> getDownStreamDesignElements(IDesignElement startElement, Class<T> destinationType) {
		List<T> ret = new ArrayList<T>();
		Map<String, IDesignElement> visited = new HashMap<String, IDesignElement>();
		visited.put(startElement.getId(), startElement);
		getDownStreamDesignElements0(startElement, destinationType, ret, visited);
		return ret;
	}

	private static <T> void getDownStreamDesignElements0(IDesignElement startElement, Class<T> destinationType,
			List<T> elements, Map<String, IDesignElement> visited) {
		List<IDesignElementConnectionPoint> records = startElement.getConnectorRecords();
		for (IDesignElementConnectionPoint connectionPoint : records) {
			IDesignConnector connector = connectionPoint.getDesignConnector();
			if (connector != null) {
				IDesignElement targetElement = connector.getDestination();
				T adapter = (T) targetElement.getAdapter(destinationType);
				if (adapter != null)
					elements.add(adapter);
				if (visited.get(targetElement.getId()) == null) {
					visited.put(targetElement.getId(), targetElement);
					getDownStreamDesignElements0(targetElement, destinationType, elements, visited);
				}
			} else {
				if (startElement.getDesign() == startElement.getDesign().getDocument().getMainDesign()) {
					for (IDesignElement targetElement : startElement.getDesign().getDesignElements()) {
						for (IExitBroadcastReceiver receiver : targetElement.getExitBroadcastReceivers()) {
							if (connectionPoint.getName().equals(receiver.getExitPattern())) {
								T adapter = (T) targetElement.getAdapter(destinationType);
								if (adapter != null)
									elements.add(adapter);
								if (visited.get(targetElement.getId()) == null) {
									visited.put(targetElement.getId(), targetElement);
									getDownStreamDesignElements0(targetElement, destinationType, elements, visited);
								}
								break;
							}
						}
					}
				}
			}
		}
	}
}
