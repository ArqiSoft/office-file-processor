package sds.officeprocessor.domain.models;

public class Property {
    
    public String Name;
    public String Value;
    public double Error;

    public Property(String Name, String Value, double Error) {
        this.Name = Name;
        this.Value = Value;
        this.Error = Error;
    }   
    
    public Property(String Name, String Value) {
        this.Name = Name;
        this.Value = Value;
    }   
}
