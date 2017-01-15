package com.github.stefantt.vdrcontroller.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A node of a tree.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 *
 * @param <T> The type of the node's data
 */
public class TreeNode<T>
{
   private String name;
   private T data;
   private TreeNode<T> parent;
   private Set<TreeNode<T>> children = new HashSet<>();

   /**
    * Create an empty node.
    */
   public TreeNode()
   {
      this(null, null);
   }

   /**
    * Create a named node.
    *
    * @param name The name of the node
    */
   public TreeNode(String name)
   {
      this(name, null);
   }

   /**
    * Create a node.
    *
    * @param name The name of the node
    * @param data The data of the node
    */
   public TreeNode(String name, T data)
   {
      this.name = name;
      this.data = data;
   }

   public void addChild(TreeNode<T> child)
   {
      child.setParent(this);
      this.children.add(child);
   }

   public void addChild(String name, T data)
   {
      TreeNode<T> newChild = new TreeNode<>(name, data);
      newChild.setParent(this);
      children.add(newChild);
   }

   public void addChildren(Collection<TreeNode<T>> children)
   {
      for (TreeNode<T> child : children)
      {
         child.setParent(this);
      }
      this.children.addAll(children);
   }

   public Set<TreeNode<T>> getChildren()
   {
      return children;
   }

   /**
    * Remove all children.
    */
   public void removeChildren()
   {
      children.clear();
   }

   /**
    * @return The name of the node
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the node.
    *
    * @param name The new name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return The node's data
    */
   public T getData()
   {
      return data;
   }

   /**
    * Set the node's data.
    *
    * @param data The new data
    */
   public void setData(T data)
   {
      this.data = data;
   }

   /**
    * Set the parent node.
    *
    * @param parent The new parent
    */
   private void setParent(TreeNode<T> parent)
   {
      this.parent = parent;
   }

   /**
    * @return The parent node
    */
   public TreeNode<T> getParent()
   {
      return parent;
   }
}
