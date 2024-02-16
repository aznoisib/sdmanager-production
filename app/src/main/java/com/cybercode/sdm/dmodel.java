package com.cybercode.sdm;

public class dmodel {
    private String name;
    private int Prog;
    private String Ids;
    private String sts ;
    private boolean act;






    public void setIds(String Ids) {
        this.Ids = Ids;
    }
    public  String getIds() {
        return Ids;
    }
    public String  getSts() {
        return sts;
    }
    public boolean getAct() {
        return act;
    }

    public String getName() {
        return name;
    }
    public void setSts(String sts) {
        this.sts = sts;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAct(boolean act) {
        this.act = act;
    }
    public int getProg() {
        return Prog;
    }

    public void setProg(int Prog) {
        this.Prog = Prog;
    }
}