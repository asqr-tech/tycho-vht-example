package org.eclipse.vtp.desktop.model.core.internal;

import java.util.List;

import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectField;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;

public class VariableHelper
{
	/**
	 * @param name
	 * @param variableType
	 * @param array
	 * @return
	 */
	public static Variable constructVariable(String name, IBusinessObjectSet objectSet, FieldType type)
	{
		Variable v = new Variable(name, type);
		buildObjectFields(v, objectSet);
		return v;
	}

	/**
	 * @param def
	 * @param businessObjectSet
	 */
	public static void buildObjectFields(ObjectDefinition def, IBusinessObjectSet businessObjectSet)
	{
		if(def.getType().isObject())
		{
			//lookup business object
			List<IBusinessObject> bos = businessObjectSet.getBusinessObjects();
			for(IBusinessObject ibo : bos)
			{
				if(ibo.getName().equals(def.getType().getName()))
				{
					List<IBusinessObjectField> fields = ibo.getFields();
					for(IBusinessObjectField ibof : fields)
					{
						buildObjectFields(def.addField(
								new ObjectField(ibof.getName(),
									ibof.getDataType(), false)), businessObjectSet);
					}
				}
			}
		}
		else if(def.getType().getPrimitiveType() == Primitive.ARRAY)
		{
			def.addField(new ObjectField("numberOfItems", FieldType.NUMBER, true));
		}
		else if(def.getType().getPrimitiveType() == Primitive.STRING)
		{
			def.addField(new ObjectField("length", FieldType.NUMBER, true));
		}
	}
	
}
