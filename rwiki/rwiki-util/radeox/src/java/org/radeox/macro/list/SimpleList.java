/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --LICENSE NOTICE--
 */
package org.radeox.macro.list;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import org.radeox.util.Linkable;
import org.radeox.util.Nameable;

/**
 * Simple list formatter.
 * 
 * @author Matthias L. Jugel
 * @version $Id: SimpleList.java 7756 2006-04-13 12:25:49Z ian@caret.cam.ac.uk $
 */
public class SimpleList implements ListFormatter
{
	public String getName()
	{
		return "simple";
	}

	public void format(Writer writer, Linkable current, String listComment,
			Collection c, String emptyText, boolean showSize)
			throws IOException
	{
		writer.write("<div class=\"list\"><div class=\"list-title\">");
		writer.write(listComment);
		if (showSize)
		{
			writer.write(" (");
			writer.write("" + c.size());
			writer.write(")");
		}
		writer.write("</div>");
		if (c.size() > 0)
		{
			writer.write("<blockquote>");
			Iterator nameIterator = c.iterator();
			while (nameIterator.hasNext())
			{
				Object object = nameIterator.next();
				if (object instanceof Linkable)
				{
					writer.write(((Linkable) object).getLink());
				}
				else if (object instanceof Nameable)
				{
					writer.write(((Nameable) object).getName());
				}
				else
				{
					writer.write(object.toString());
				}

				if (nameIterator.hasNext())
				{
					writer.write(", ");
				}
			}
			writer.write("</blockquote>");
		}
		else
		{
			writer.write(emptyText);
		}
		writer.write("</div>");
	}
}
