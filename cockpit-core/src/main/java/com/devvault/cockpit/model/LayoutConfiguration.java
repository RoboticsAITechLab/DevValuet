package com.devvault.cockpit.model;

import java.util.Map;
import java.util.List;

/**
 * Layout configuration for UI arrangement
 */
public class LayoutConfiguration {
    private String layoutType;
    private List<String> panelOrder;
    private Map<String, Boolean> visiblePanels;
    private Map<String, Integer> panelSizes;
    private boolean responsiveDesign;
    
    // Constructors
    public LayoutConfiguration() {}
    
    // Getters and Setters
    public String getLayoutType() { return layoutType; }
    public void setLayoutType(String layoutType) { this.layoutType = layoutType; }
    
    public List<String> getPanelOrder() { return panelOrder; }
    public void setPanelOrder(List<String> panelOrder) { this.panelOrder = panelOrder; }
    
    public Map<String, Boolean> getVisiblePanels() { return visiblePanels; }
    public void setVisiblePanels(Map<String, Boolean> visiblePanels) { this.visiblePanels = visiblePanels; }
    
    public Map<String, Integer> getPanelSizes() { return panelSizes; }
    public void setPanelSizes(Map<String, Integer> panelSizes) { this.panelSizes = panelSizes; }
    
    public boolean isResponsiveDesign() { return responsiveDesign; }
    public void setResponsiveDesign(boolean responsiveDesign) { this.responsiveDesign = responsiveDesign; }
}