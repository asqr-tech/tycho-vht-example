/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.core.util;

/**
 * Utility class for performing 2/3D vector based distance calculations.
 * 
 * @author Trip
 * @version 1.0
 */
public class Geom
{
	/**
	 * Returns a 3D vector magnitude for the line created from Point1 to Point2.
	 * 
	 * @param Point1 The origin of the vector
	 * @param Point2 The destination of the vector
	 * @return The vector magnitude
	 */
	static float magnitude(Point3D Point1, Point3D Point2)
	{
		Point3D Vector = newPoint3D(0, 0, 0);

		Vector.X = Point2.X - Point1.X;
		Vector.Y = Point2.Y - Point1.Y;
		Vector.Z = Point2.Z - Point1.Z;

		return (float)Math.sqrt((Vector.X * Vector.X) + (Vector.Y * Vector.Y) + (Vector.Z * Vector.Z));
	}

	/**
	 * Calculates the distance between a given point and a line.
	 * 
	 * @param point The point away from the line
	 * @param lineStart The origin point of the line
	 * @param lineEnd The destination point of the line
	 * @return The distance between the point and the line
	 */
	public static float DistancePointLine(Point3D point, Point3D lineStart,
		Point3D lineEnd)
	{
		float LineMag;
		float U;
		Point3D intersection = newPoint3D(0, 0, 0);

		LineMag = magnitude(lineEnd, lineStart);

		U = (
				((point.X - lineStart.X) * (lineEnd.X - lineStart.X))
				+ ((point.Y - lineStart.Y) * (lineEnd.Y - lineStart.Y))
				+ ((point.Z - lineStart.Z) * (lineEnd.Z - lineStart.Z))
			) / (LineMag * LineMag);

		if((U < 0.0f) || (U > 1.0f))
		{
			return 0; // closest point does not fall within the line segment
		}

		intersection.X = lineStart.X + (U * (lineEnd.X - lineStart.X));
		intersection.Y = lineStart.Y + (U * (lineEnd.Y - lineStart.Y));
		intersection.Z = lineStart.Z + (U * (lineEnd.Z - lineStart.Z));

		return magnitude(point, intersection);
	}

	/**
	 * Constructs a new Point3D object with the given coordinates.
	 * 
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @param z The Z coordinate
	 * @return A new Point3D object
	 */
	public static Point3D newPoint3D(float x, float y, float z)
	{
		return new Geom().new Point3D(x, y, z);
	}

	/**
	 * Represents a point in three dimensional space.
	 * 
	 * @author trip
	 */
	public class Point3D
	{
		/** The X coordinate */
		public float X;
		/** The Y coordinate */
		public float Y;
		/** The Z coordinate */
		public float Z;

		/**
		 * Constructs a new Point3D object at the origin (0, 0, 0).
		 */
		public Point3D()
		{
			X = 0;
			Y = 0;
			Z = 0;
		}

		/**
		 * Constructs a new Point3D object with the given coordinates.
		 * 
		 * @param X The X coordinate
		 * @param Y The Y coordinate
		 * @param Z The Z coordinate
		 */
		public Point3D(float X, float Y, float Z)
		{
			this.X = X;
			this.Y = Y;
			this.Z = Z;
		}
	}
}
