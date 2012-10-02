/**
 * Copyright 2011 The Open Source Research Group,
 *                University of Erlangen-Nürnberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sweble.wikitext.engine.astwom;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;

import lombok.Getter;

import org.sweble.wikitext.parser.AstNodeTypes;
import org.sweble.wikitext.parser.nodes.Bold;
import org.sweble.wikitext.parser.nodes.HorizontalRule;
import org.sweble.wikitext.parser.nodes.InternalLink;
import org.sweble.wikitext.parser.nodes.Italics;
import org.sweble.wikitext.parser.nodes.Newline;
import org.sweble.wikitext.parser.nodes.Redirect;
import org.sweble.wikitext.parser.nodes.TagExtension;
import org.sweble.wikitext.parser.nodes.WikitextNode;
import org.sweble.wikitext.parser.nodes.WtList;
import org.sweble.wikitext.parser.nodes.WtText;
import org.sweble.wikitext.parser.nodes.XmlAttribute;
import org.sweble.wikitext.parser.nodes.XmlCharRef;
import org.sweble.wikitext.parser.nodes.XmlComment;
import org.sweble.wikitext.parser.nodes.XmlElement;
import org.sweble.wikitext.parser.nodes.XmlEntityRef;
import org.sweble.wikitext.parser.utils.TextUtils;

import de.fau.cs.osr.utils.XmlGrammar;

public class Toolbox
{
	public static void replaceAstNode(
			WtList container,
			WikitextNode oldAstNode,
			WikitextNode newAstNode) throws AssertionError
	{
		ListIterator<WikitextNode> i = container.listIterator();
		while (i.hasNext())
		{
			WikitextNode node = i.next();
			if (node == oldAstNode)
			{
				i.set(newAstNode);
				i = null;
				break;
			}
		}
		if (i != null)
			throw new AssertionError();
	}
	
	public static void removeAstNode(WtList container, WikitextNode astNode)
			throws AssertionError
	{
		Iterator<WikitextNode> i = container.iterator();
		while (i.hasNext())
		{
			WikitextNode node = i.next();
			if (node == astNode)
			{
				i.remove();
				i = null;
				break;
			}
		}
		if (i != null)
			throw new AssertionError();
	}
	
	public static void insertAstNode(
			WtList container,
			WikitextNode astNode,
			WikitextNode beforeAstNode) throws AssertionError
	{
		ListIterator<WikitextNode> i = container.listIterator();
		while (i.hasNext())
		{
			WikitextNode n = i.next();
			if (n == beforeAstNode)
			{
				i.previous();
				i.add(astNode);
				i = null;
				break;
			}
		}
		
		if (i != null)
			throw new AssertionError();
	}
	
	public static void insertAstNodeAfter(
			WtList container,
			WikitextNode astNode,
			WikitextNode afterAstNode) throws AssertionError
	{
		ListIterator<WikitextNode> i = container.listIterator();
		while (i.hasNext())
		{
			WikitextNode n = i.next();
			if (n == afterAstNode)
			{
				i.add(astNode);
				i = null;
				break;
			}
		}
		
		if (i != null)
			throw new AssertionError();
	}
	
	public static void prependAstNode(WtList container, WikitextNode astNode)
	{
		container.add(0, astNode);
	}
	
	public static void appendAstNode(WtList container, WikitextNode astNode)
	{
		container.add(astNode);
	}
	
	public static ListIterator<WikitextNode> advanceAfter(
			WtList container,
			WikitextNode node)
	{
		ListIterator<WikitextNode> i = container.listIterator();
		while (i.hasNext())
		{
			if (i.next() == node)
				return i;
		}
		return null;
	}
	
	public static ListIterator<WikitextNode> advanceBefore(
			WtList container,
			WikitextNode node)
	{
		ListIterator<WikitextNode> i = container.listIterator();
		while (i.hasNext())
		{
			if (i.next() == node)
			{
				i.previous();
				return i;
			}
		}
		return null;
	}
	
	public static void removeAstNode(ListIterator<WikitextNode> i, WikitextNode astNode)
	{
		while (i.hasNext())
		{
			if (i.next() == astNode)
			{
				i.remove();
				return;
			}
		}
		throw new InternalError();
	}
	
	// =========================================================================
	
	@SuppressWarnings("unchecked")
	public static <T> T expectType(Class<T> type, Object obj)
	{
		if (obj != null && !type.isInstance(obj))
			throw new IllegalArgumentException(
					"Expected object of type " + type.getName() + "!");
		return (T) obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T expectType(Class<T> type, Object obj, String argName)
	{
		if (obj != null && !type.isInstance(obj))
			throw new IllegalArgumentException(
					"Expected argument `" + argName + "' to be of type " + type.getName() + "!");
		return (T) obj;
	}
	
	// =========================================================================
	
	public static XmlElement addRtData(XmlElement n)
	{
		if (n.getEmpty())
		{
			TextUtils.addRtData(
					(WikitextNode) n,
					TextUtils.joinRt('<', n.getName()),
					TextUtils.joinRt(" />"),
					null);
		}
		else
		{
			TextUtils.addRtData(
					(WikitextNode) n,
					TextUtils.joinRt('<', n.getName()),
					TextUtils.joinRt('>'),
					TextUtils.joinRt("</", n.getName(), '>'));
		}
		
		for (WikitextNode attr : n.getXmlAttributes())
			addRtData((XmlAttribute) attr);
		
		return n;
	}
	
	public static XmlAttribute addRtData(XmlAttribute n)
	{
		if (n.getHasValue())
		{
			TextUtils.addRtData(
					n,
					TextUtils.joinRt(' ', n.getName(), "=\""),
					TextUtils.joinRt('"'));
		}
		else
		{
			TextUtils.addRtData(
					n,
					TextUtils.joinRt(' ', n.getName()),
					null);
		}
		
		return n;
	}
	
	public static Bold addRtData(Bold n)
	{
		TextUtils.addRtData(
				n,
				TextUtils.joinRt("'''"),
				TextUtils.joinRt("'''"));
		return n;
	}
	
	public static WikitextNode addRtData(Italics n)
	{
		TextUtils.addRtData(
				n,
				TextUtils.joinRt("''"),
				TextUtils.joinRt("''"));
		return n;
	}
	
	public static InternalLink addRtData(InternalLink n)
	{
		TextUtils.addRtData(
				n,
				TextUtils.joinRt("[[", n.getTarget()),
				TextUtils.joinRt("]]"));
		return n;
	}
	
	public static XmlComment addRtData(XmlComment n)
	{
		TextUtils.addRtData(
				n,
				TextUtils.joinRt("<!--", n.getContent(), "-->"));
		return n;
	}
	
	public static HorizontalRule addRtData(HorizontalRule n)
	{
		TextUtils.addRtData(
				n,
				TextUtils.joinRt("----"));
		return n;
	}
	
	public static Redirect addRtData(Redirect n)
	{
		TextUtils.addRtData(
				n,
				TextUtils.joinRt("#REDIRECT[[", n.getTarget(), "]]"));
		return n;
	}
	
	public static TagExtension addRtData(TagExtension n)
	{
		for (WikitextNode attr : n.getXmlAttributes())
		{
			if (attr.getNodeType() != AstNodeTypes.NT_XML_ATTRIBUTE)
				continue;
			addRtData((XmlAttribute) attr);
		}
		
		TextUtils.addRtData(
				n,
				TextUtils.joinRt("<", n.getName()),
				TextUtils.joinRt(">", n.getBody(), "</", n.getName(), ">"));
		return n;
	}
	
	// =========================================================================
	
	private static final String validTargetRxStr =
			"(?:[^\\u0000-\\u001F\\u007F\\uFFFD<>{}|\\[\\]/]+)";
	
	private static final String validTitleRxStr =
			"(?:[^\\u0000-\\u001F\\u007F\\uFFFD<>{}|\\[\\]:/]+)";
	
	@Getter(lazy = true)
	private static final Pattern validTargetRx = Pattern.compile(validTargetRxStr);
	
	@Getter(lazy = true)
	private static final Pattern validTitleRx = Pattern.compile(validTitleRxStr);
	
	private static final String validPathRxStr =
			"(?:(?:" + validTitleRxStr + "/)*" + validTitleRxStr + "?)";
	
	@Getter(lazy = true)
	private static final Pattern validPathRx = Pattern.compile(validPathRxStr);
	
	// =========================================================================
	
	public static void checkValidTitle(String title)
			throws UnsupportedOperationException,
			IllegalArgumentException
	{
		if (title == null)
			throw new UnsupportedOperationException("Cannot remove attribute `title'");
		
		if (!getValidTitleRx().matcher(title).matches())
			throw new IllegalArgumentException("Invalid title");
	}
	
	public static void checkValidCategory(String category)
			throws UnsupportedOperationException,
			IllegalArgumentException
	{
		if (category == null)
			throw new UnsupportedOperationException("Cannot remove attribute `category'");
		
		if (!getValidTitleRx().matcher(category).matches())
			throw new IllegalArgumentException("Invalid category");
	}
	
	public static String checkValidNamespace(String namespace)
			throws IllegalArgumentException
	{
		if (namespace == null || namespace.isEmpty())
			return null;
		
		if (!getValidTitleRx().matcher(namespace).matches())
			throw new IllegalArgumentException("Invalid namespace");
		
		return namespace;
	}
	
	/**
	 * Checks for a valid path expression and removes a trailing slash if
	 * present.
	 * 
	 * @param path
	 *            The path to check.
	 * @return The path stripped of a trailing slash if present.
	 * @throws IllegalArgumentException
	 */
	public static String checkValidPath(String path)
			throws IllegalArgumentException
	{
		if (path == null || path.isEmpty())
			return null;
		
		if (path == null || !getValidPathRx().matcher(path).matches())
			throw new IllegalArgumentException("Invalid path");
		
		int l = path.length() - 1;
		return (path.charAt(l) == '/') ? path.substring(0, l) : path;
	}
	
	public static void checkValidTarget(String target)
	{
		if (target == null)
			throw new UnsupportedOperationException("Cannot remove target attribute");
		
		if (!getValidTargetRx().matcher(target).matches())
			throw new IllegalArgumentException("Invalid target");
	}
	
	public static void checkValidXmlName(String name)
	{
		if (name == null)
			throw new NullPointerException("Name cannot be null");
		
		if (!XmlGrammar.xmlName().matcher(name).matches())
			throw new IllegalArgumentException("Not a valid XML Name");
	}
	
	public static void checkValidCommentText(String text)
	{
		if (text == null)
			throw new NullPointerException("WtText cannot be null");
		
		if (!XmlGrammar.xmlCommentText().matcher(text).matches())
			throw new IllegalArgumentException("Not a valid XML Comment text");
	}
	
	// =========================================================================
	
	public static String toText(WikitextNode n)
	{
		switch (n.getNodeType())
		{
			case WikitextNode.NT_TEXT:
				return ((WtText) n).getContent();
				
			case AstNodeTypes.NT_NEWLINE:
				return ((Newline) n).getContent();
				
			case AstNodeTypes.NT_XML_COMMENT:
			case AstNodeTypes.NT_IGNORED:
				return "";
				
			case AstNodeTypes.NT_XML_CHAR_REF:
				return new String(Character.toChars(((XmlCharRef) n).getCodePoint()));
				
			case AstNodeTypes.NT_XML_ENTITY_REF:
				return ((XmlEntityRef) n).getResolved();
				
			default:
				throw new IllegalArgumentException();
		}
	}
}