package com.ldt.musicr.mediadata.oldmodel;

/**
 * Created by trung on 8/12/2017.
 */

public class Field {
    private String Field;
    private String Name;
    public Field(String field,String name)
    {
        Field= field;
        Name=name;
    }
    public String getField()
    {
        return Field;
    }
    public String getName()
    {
        return Name;
    }
}
