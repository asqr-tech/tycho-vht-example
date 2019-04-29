package org.eclipse.vtp.desktop.export;

public interface IDefinitionBuilder {

	/** The context when parsing an application model. */
	public static final int CONTEXT_APPLICATION = 1;
	/** The context when parsing a fragment model. */
	public static final int CONTEXT_FRAGMENT = 2;
	/** The context when parsing a dialog model. */
	public static final int CONTEXT_DIALOG = 3;
	/** The process definition name space URI. */
	static final String NAMESPACE_URI_PROCESS_DEFINITION = //
	"http://eclipse.org/vtp/xml/framework/engine/process-definition"; //$NON-NLS-1$
	/** The common components name space URI. */
	public static final String NAMESPACE_URI_COMMON = //
	"http://eclipse.org/vtp/xml/framework/common/configurations"; //$NON-NLS-1$
	/** The database components name space URI. */
	public static final String NAMESPACE_URI_DATABASES = //
	"http://eclipse.org/vtp/xml/framework/databases/configurations"; //$NON-NLS-1$
	/** The web service components name space URI. */
	public static final String NAMESPACE_URI_WEBSERVICES = //
	"http://eclipse.org/vtp/xml/framework/webservices/configurations"; //$NON-NLS-1$
	/** The interactions core components name space URI. */
	public static final String NAMESPACE_URI_INTERACTIONS_CORE = //
	"http://eclipse.org/vtp/xml/framework/interactions/core/configurations"; //$NON-NLS-1$
	
	IFlowModel getMainModel();
	
	IFlowModel getDialogModel(String dialogId);
	
}
