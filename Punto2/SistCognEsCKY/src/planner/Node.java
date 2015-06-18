/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salvatore
 */
public class Node<T> {

    private String pos;
    private T data;
    private List<Node<T>> child;
    private Node<T> parent;

    public Node(String pos, T data) {
        this.setPos(pos);
        this.data = data;
        this.child = new ArrayList<Node<T>>();
    }

    public Node(Node<T> node) {
        this.data = (T) node.getData();
        child = new ArrayList<Node<T>>();
    }

    public void addChild(Node<T> children) {
        children.setParent(this);
        child.add(children);
    }

    public void addChildAt(int index, Node<T> children) {
        children.setParent(this);
        this.child.add(index, children);
    }

    public void setChildren(List<Node<T>> children) {
        for (Node<T> child : children) {
            child.setParent(this);
        }

        this.child = children;
    }

    public void removeChildren() {
        this.child.clear();
    }

    public Node<T> removeChildAt(int index) {
        return child.remove(index);
    }

    public void removeThisIfItsAChild(Node<T> childToBeDeleted) {
        List<Node<T>> list = getChildren();
        list.remove(childToBeDeleted);
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return this.parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public List<Node<T>> getChildren() {
        return this.child;
    }

    public Node<T> getChildAt(int index) {
        return child.get(index);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (obj instanceof Node) {
            if (((Node<?>) obj).getData().equals(this.data) && ((Node<?>) obj).getPos().equals(this.pos)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return (this.pos == null ? "" : this.pos) + ":" + this.data.toString();
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

}
