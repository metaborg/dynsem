package org.metaborg.meta.interpreter.framework;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 
 * @author vladvergu
 * 
 */
public abstract class AbstractNode implements INode, IMatchable {

	private INode parent;
	private INode replacedBy;
	private INodeSource source;

	@Override
	@SuppressWarnings("unchecked")
	public <T> T match(Class<T> clazz) {
		if (this.getClass() == clazz) {
			return (T) this;
		} else {
			return null;
		}
	}

	@Override
	public <T extends INode> INode setParent(T parent) {
		Objects.requireNonNull(parent);
		this.parent = parent;
		return this;
	}

	@Override
	public <T extends INode> T adoptChild(T child) {
		child.setParent(this);
		return child;
	}

	@Override
	public <K extends INode> INodeList<K> adoptChildren(INodeList<K> children) {
		INodeList<K> list = children;
		while (list.size() > 0) {
			adoptChild(list.head());
			list = list.tail();
		}
		return children;
	}

	@Override
	public <T extends INode> T replace(T newNode) {
		if (getParent() != null) {
			return getParent().replaceChild(this, newNode);
		}
		return newNode;
	}

	@Override
	public <T extends INode> T replaceChild(INode oldChild, T newChild) {
		return NodeUtils.replaceChild(this, oldChild, newChild);
	}

	@Override
	public INode replacement() {
		return replacedBy;
	}

	@Override
	public void setReplacedBy(INode newNode) {
		this.replacedBy = newNode;
	}

	@Override
	public boolean replaced() {
		return replacedBy != null;
	}

	@Override
	public boolean isRoot() {
		return parent != null;
	}

	@Override
	public INode getParent() {
		return parent;
	}

	@Override
	public void setSourceInfo(INodeSource src) {
		this.source = src;
	}

	@Override
	public INodeSource getSourceInfo() {
		return source;
	}

	@Override
	public String toString() {
		return NodeUtils.toString(this);
	}

}
