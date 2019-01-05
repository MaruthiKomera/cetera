package com.cetera.enums;

/**
 * This is a simple enumeration class that holds the <i>"Yes" or "No"</i>
 * values 
 * 
 * @author Radwan
 */
public enum YesOrNo {
    Y("Yes"), N("No");
    
    /**
     * The String <i>value</i> is used to hold the nice version
     * of the enumeration type, so it can be used when logging or
     * printing its value.
     */
    String value;
    
    private YesOrNo(String value){
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
