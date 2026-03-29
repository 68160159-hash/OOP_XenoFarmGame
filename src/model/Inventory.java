package model;

public class Inventory {
    private int solarEnergy;
    private int dewDrop;

    public Inventory(int solar, int dew) {
        this.solarEnergy = solar;
        this.dewDrop = dew;
    }

    // ชื่อเมธอดให้ตรงกับที่ PlayAreaPanel เรียกหา
    public int getSolarCount() { return solarEnergy; }
    public int getDewCount() { return dewDrop; }

    public boolean useSolar() {
        if (solarEnergy > 0) { solarEnergy--; return true; }
        return false;
    }

    public boolean useDew() {
        if (dewDrop > 0) { dewDrop--; return true; }
        return false;
    }

    public void addSolar(int amount) { solarEnergy += amount; }
    public void addDew(int amount) { dewDrop += amount; }
}