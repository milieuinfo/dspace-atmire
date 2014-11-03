package com.atmire.dspace.app.xmlui.aspect.atmiremodules;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * User: kevin (kevin at atmire.com)
 * Date: 24/10/12
 * Time: 14:46
 */
public class AtmireModulesOverviewContainer {

    private List<AtmireModuleOverview> atmireModuleOverviews = new ArrayList<AtmireModuleOverview>();


    public List<AtmireModuleOverview> getAtmireModuleOverviews() {
        return atmireModuleOverviews;
    }

    @Autowired(required = false)
    public void setAtmireModuleOverviews(List<AtmireModuleOverview> atmireModuleOverviews) {
        this.atmireModuleOverviews = atmireModuleOverviews;
    }
}
