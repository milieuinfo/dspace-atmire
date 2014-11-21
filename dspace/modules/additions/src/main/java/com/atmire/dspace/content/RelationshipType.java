package com.atmire.dspace.content;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 03 Nov 2014
 */
public class RelationshipType {

    private int id;
    private String leftType;
    private String rightType;
	private String leftLabel;
    private String rightLabel;
	private Pair<Integer,Integer> leftCardinality;
    private Pair<Integer,Integer> rightCardinality;

    public RelationshipType() {
    }

    public RelationshipType(String leftType, String rightType) {
        this.leftType = leftType;
        this.rightType = rightType;
    }

	public RelationshipType(String leftType, String rightType, String leftLabel, String rightLabel, Pair<Integer, Integer> leftCardinality, Pair<Integer, Integer> rightCardinality, String semanticRuleset) {
        this.leftType = leftType;
        this.rightType = rightType;
        this.leftLabel = leftLabel;
        this.rightLabel = rightLabel;
        this.leftCardinality = leftCardinality;
        this.rightCardinality = rightCardinality;
        this.semanticRuleset = semanticRuleset;
    }

    public RelationshipType(int id, String leftType, String rightType, String leftLabel, String rightLabel, Pair<Integer, Integer> leftCardinality, Pair<Integer, Integer> rightCardinality, String semanticRuleset) {
		this.id = id;
		this.leftType = leftType;
		this.rightType = rightType;
		this.leftLabel = leftLabel;
		this.rightLabel = rightLabel;
		this.leftCardinality = leftCardinality;
		this.rightCardinality = rightCardinality;
		this.semanticRuleset = semanticRuleset;
	}

	private String semanticRuleset;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLeftType() {
        return leftType;
    }

    public void setLeftType(String leftType) {
        this.leftType = leftType;
    }

    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public String getLeftLabel() {
        return leftLabel;
    }

    public void setLeftLabel(String leftLabel) {
        this.leftLabel = leftLabel;
    }

    public String getRightLabel() {
        return rightLabel;
    }

    public void setRightLabel(String rightLabel) {
        this.rightLabel = rightLabel;
    }

    public Pair<Integer, Integer> getLeftCardinality() {
        return leftCardinality;
    }

    public void setLeftCardinality(Pair<Integer, Integer> leftCardinality) {
        this.leftCardinality = leftCardinality;
    }

    public Pair<Integer, Integer> getRightCardinality() {
        return rightCardinality;
    }

    public void setRightCardinality(Pair<Integer, Integer> rightCardinality) {
        this.rightCardinality = rightCardinality;
    }

    public String getSemanticRuleset() {
        return semanticRuleset;
    }

    public void setSemanticRuleset(String semanticRuleset) {
        this.semanticRuleset = semanticRuleset;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("leftType", leftType)
                .append("rightType", rightType)
                .append("leftLabel", leftLabel)
                .append("rightLabel", rightLabel)
                .append("leftCardinality", leftCardinality)
                .append("rightCardinality", rightCardinality)
                .append("semanticRuleset", semanticRuleset)
                .toString();
    }
}
