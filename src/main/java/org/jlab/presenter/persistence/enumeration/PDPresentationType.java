package org.jlab.presenter.persistence.enumeration;

public enum PDPresentationType {
    PD(  "8:00 AM meeting (M-F) Daily Summary: PD Summary"),
    RUN( "RUN - 8:00 AM meeting (M-F) Daily Summary: Accelerator Running"), 
    SAD( "SAD - 8:00 AM meeting (M-F) Daily Summary: Scheduled Accelerator Down"), 
    LSD( "8:00 AM meeting (M-F) Daily Summary: Long Shut Down"), 
    HCO( "HCO - 8:00 AM meeting (M-F) Daily Summary: Hot Checkout"),
    SUM1("SUM1 - 1:30 PM meeting ( W ) Weekly Summary: Beginning of Week 1 (Oncoming PD)"), 
    SUM2("SUM2 - 1:30 PM meeting ( W ) Weekly Summary: End of Week 1"), 
    SUM3("SUM3 - 1:30 PM meeting ( W ) Weekly Summary: End of Week 2 (Outgoing PD)");
    
    private final String description;
    
    PDPresentationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
