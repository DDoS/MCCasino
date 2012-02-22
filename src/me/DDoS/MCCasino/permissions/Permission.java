package me.DDoS.MCCasino.permissions;

/**
 *
 * @author DDoS
 */
public enum Permission {
    
    USE("mccasino.use"), 
    SETUP("mccasino.setup");

    private final String permString;

    private Permission(String name) {
        
        this.permString = name;
    
    }

    public String getPermissionString() {
    
        return permString;
    
    }
}
