/*
 * #%L
 * SLIM Plotter application and curve fitting library for
 * combined spectral lifetime visualization and analysis.
 * %%
 * Copyright (C) 2006 - 2014 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.slim.fit;

import java.util.EventObject;

/**
 * CurveEvent is an event for reporting the
 * status of curve collection generation.
 *
 * @author Curtis Rueden
 */
public class CurveEvent extends EventObject {

  // -- Fields --

  /** Progress value for the event. */
  protected int value;

  /** Progress maximum for the event. */
  protected int max;

  /** String message describing the event. */
  protected String message;

  // -- Constructor --

  /** Constructs a curve event. */
  public CurveEvent(Object source, int value, int max, String message) {
    super(source);
    this.value = value;
    this.max = max;
    this.message = message;
  }

  // -- CurveEvent methods --

  /** Gets the message describing the event. */
  public String getMessage() { return message; }

  /** Gets the progress value for the event. */
  public int getValue() { return value; }

  /** Gets the progress maximum for the event. */
  public int getMaximum() { return max; }

  /** Gets a string representation of the event. */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CurveEvent " + value + "/" + max + ": ");
    String name = getSource().getClass().getName();
    sb.append(name);
    if (message != null) {
      sb.append(": ");
      sb.append(message);
    }
    return sb.toString();
  }

}
