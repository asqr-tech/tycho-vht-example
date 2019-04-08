package org.eclipse.vtp.framework.common;

import java.util.List;

public interface ILastResult
{
	public void clear();
	
	public ILastResultData addResult(int confidence, String utterence, String inputMode, String interpretation);
	
	public List<ILastResultData> getResults();
	
	public String getMarkName();
	
	public void setMarkName(String name);
	
	public String getMarkTime();
	
	public void setMarkTime(String time);
}
