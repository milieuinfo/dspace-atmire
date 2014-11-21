package com.atmire.dspace.content;

import org.dspace.content.Item;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 04 Nov 2014
 */
public class Relationship {

    private Integer id;
    private Item left;

	public Relationship() {
	}

	public Relationship(Integer id, Item left, Item right, RelationshipType type) {
		this.id = id;
		this.left = left;
		this.right = right;
		this.type = type;

	}

	private Item right;
    private RelationshipType type;

	public Relationship(Relationship other) {
		this.id = other.id;
		this.left = other.left;
		this.right = other.right;
		this.type = other.type;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Item getLeft() {
        return left;
    }

    public void setLeft(Item left) {
        this.left = left;
    }

    public Item getRight() {
        return right;
    }

    public void setRight(Item right) {
        this.right = right;
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }
}
