package me.DDoS.MCCasino.permissions;

/**
 *
 * @author DDoS
 */
public enum MCCPermissions {
    
    USE("mccasino.use"), 
    SETUP("mccasino.setup");

    private String permString;

    private MCCPermissions(String name) {
        
        this.permString = name;
    
    }

    public String getPermissionString() {
    
        return permString;
    
    }
}
