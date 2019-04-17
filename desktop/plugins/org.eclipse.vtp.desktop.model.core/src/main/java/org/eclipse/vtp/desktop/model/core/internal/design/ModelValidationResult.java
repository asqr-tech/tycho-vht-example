package org.eclipse.vtp.desktop.model.core.internal.design;

import java.util.LinkedList;
import java.util.List;

public class ModelValidationResult
{
	public static final int FAIL_ON_ERROR = 0;
	public static final int FAIL_ON_WARNING = 1;
	public static final int FAIL_ON_INFO = 2;
	
	private boolean failOnWarning = false;
	private boolean failOnInfo = false;
	private List<String> validationErrors = new LinkedList<String>();
	private List<String> validationWarnings = new LinkedList<String>();
	private List<String> validationInformation = new LinkedList<String>();

	/**
	 * @param failureFlags
	 */
	public ModelValidationResult(int failureFlags)
	{
		super();
		failOnWarning = (failureFlags & FAIL_ON_WARNING) > 0;
		failOnInfo = (failureFlags & FAIL_ON_INFO) > 0;
	}

	/**
	 * @param error
	 */
	public void addError(String error)
	{
		validationErrors.add(error);
	}
	
	/**
	 * @param warning
	 */
	public void addWarning(String warning)
	{
		validationWarnings.add(warning);
	}
	
	/**
	 * @param information
	 */
	public void addInformation(String information)
	{
		validationInformation.add(information);
	}
	
	/**
	 * @return
	 */
	public List<String> getErrors()
	{
		return validationErrors;
	}
	
	/**
	 * @return
	 */
	public List<String> getWarnings()
	{
		return validationWarnings;
	}
	
	/**
	 * @return
	 */
	public List<String> getInformation()
	{
		return validationInformation;
	}
	
	/**
	 * @return
	 */
	public boolean isValid()
	{
		return (validationErrors.size() == 0) && (!failOnWarning || (validationWarnings.size() < 1)) && (!failOnInfo || (validationInformation.size() < 1));
	}
}
