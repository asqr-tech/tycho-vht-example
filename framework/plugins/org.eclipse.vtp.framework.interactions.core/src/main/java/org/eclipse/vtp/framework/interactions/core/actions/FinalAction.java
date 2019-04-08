package org.eclipse.vtp.framework.interactions.core.actions;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.actions.ExitAction;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.ExitConfiguration;
import org.eclipse.vtp.framework.common.controller.IController;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.conversation.IFinal;

/**
 * FinalAction.
 * 
 * @author Lonnie Pryor
 */
public class FinalAction extends ExitAction
{
	/** The conversation to use. */
	private final IConversation conversation;
	/** Comment for variableRegistry. */
	private final IVariableRegistry variableRegistry;
	/** The configurations to use. */
	protected final AssignmentConfiguration[] configurations;

	/**
	 * Creates a new FinalAction.
	 * 
	 * @param context
	 * @param controller
	 * @param configuration
	 */
	public FinalAction(IActionContext context, IController controller,
			ExitConfiguration configuration, IConversation conversation,
			IVariableRegistry variableRegistry,
			AssignmentConfiguration[] configurations)
	{
		super(context, controller, configuration, configurations);
		this.conversation = conversation;
		this.variableRegistry = variableRegistry;
		this.configurations = configurations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.actions.ExitAction#execute()
	 */
	public IActionResult execute()
	{
		if ("true".equals(context.getAttribute("subdialog")))
		{
			if(context.isReportingEnabled())
			{
				Dictionary props = new Hashtable();
				props.put("event", "final");
				context.report(IReporter.SEVERITY_INFO, "Ending subdialog execution.",
					props);
			}
			IFinal f = conversation.createFinal();
			for (int i = 0; i < configurations.length; ++i)
				f.setVariableValue(configurations[i].getName(), variableRegistry
						.getVariable(configurations[i].getName()).toString());
			f.setVariableValue("DialogReturnValue", configuration.getValue());
			f.enqueue();
			return context.createResult(IActionResult.RESULT_NAME_ABORT);
		}
		else if ("true".equals(context.getAttribute("fragment")))
			return super.execute();
		else {
			conversation.createEndMessage(configurations).enqueue();
			return context.createResult(IActionResult.RESULT_NAME_ABORT);
		}
	}
}
