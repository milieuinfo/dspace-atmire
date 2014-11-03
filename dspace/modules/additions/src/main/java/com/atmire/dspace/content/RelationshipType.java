package com.atmire.dspace.content;

import org.apache.commons.lang3.tuple.Pair;
import org.dspace.storage.rdbms.TableRow;

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
}
