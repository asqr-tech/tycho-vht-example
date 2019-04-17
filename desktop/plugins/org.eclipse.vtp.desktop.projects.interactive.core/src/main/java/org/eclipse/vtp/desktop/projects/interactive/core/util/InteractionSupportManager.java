package org.eclipse.vtp.desktop.projects.interactive.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractionTypeSupport;

public class InteractionSupportManager
{
	private List<InteractionSupportListener> listeners = new LinkedList<InteractionSupportListener>();
	private List<InteractionTypeSupport> originalSupport = null;
	private Map<String, SupportRecord> currentSupport = new HashMap<String, SupportRecord>();
	private List<InteractionType> installedTypes = InteractionTypeManager.getInstance().getInteractionTypes();

	public InteractionSupportManager()
	{
		super();
	}
	
	public void init(List<InteractionTypeSupport> originalSupport)
	{
		this.originalSupport = originalSupport;
		currentSupport.clear();
		for(InteractionType installedType : installedTypes)
		{
			currentSupport.put(installedType.getId(), new SupportRecord(installedType));
		}
		for(InteractionTypeSupport typeSupport : originalSupport)
		{
			SupportRecord sr = currentSupport.get(typeSupport.getInteractionType());
			if(sr != null)
				sr.setSupport(typeSupport);
			else
				currentSupport.put(typeSupport.getInteractionType(), sr = new SupportRecord(typeSupport.getInteractionType(), typeSupport.getInteractionTypeName(), false, typeSupport));
		}
		fireUpdate();
	}
	
	public List<SupportRecord> getCurrentSupport()
	{
		List<SupportRecord> ret = new LinkedList<SupportRecord>();
		ret.addAll(currentSupport.values());
		return ret;
	}
	
	public List<InteractionTypeSupport> getSupport()
	{
		List<InteractionTypeSupport> ret = new ArrayList<InteractionTypeSupport>();
		for(SupportRecord sr : currentSupport.values())
		{
			if(sr.isSupported())
				ret.add(sr.support);
		}
		return ret;
	}

	public void addListener(InteractionSupportListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	public void removeListener(InteractionSupportListener listener)
	{
		listeners.remove(listener);
	}
	
	public void addSupport(SupportRecord record)
	{
		for(InteractionTypeSupport typeSupport : originalSupport)
		{
			if(typeSupport.getInteractionType().equals(record.getId()))
			{
				record.setSupport(typeSupport);
				fireUpdate();
				return;
			}
		}
		InteractionTypeSupport newSupport = new InteractionTypeSupport(record.getId(), record.getName());
		record.setSupport(newSupport);
		fireUpdate();
		return;
	}
	
	public void removeSupport(SupportRecord record)
	{
		record.setSupport(null);
		fireUpdate();
	}
	
	private void fireUpdate()
	{
		for(InteractionSupportListener listener : listeners)
		{
			listener.supportChanged();
		}
	}
	
	public class SupportRecord
	{
		String id = null;
		String name = null;
		boolean installed = false;
		InteractionTypeSupport support = null;
		
		public SupportRecord(InteractionType installedType)
		{
			this(installedType.getId(), installedType.getName(), true, null);
		}
		
		public SupportRecord(String id, String name, boolean installed, InteractionTypeSupport support)
		{
			super();
			this.id = id;
			this.name = name;
			this.installed = installed;
			this.support = support;
		}

		public boolean isSupported()
		{
			return support != null;
		}

		private void setSupport(InteractionTypeSupport support)
		{
			this.support = support;
		}

		public boolean isInstalled()
		{
			return installed;
		}

		public String getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}

		public InteractionTypeSupport getSupport()
		{
			return support;
		}
	}
}
