/**
 * Created by bean on 4/26/2017.
 */
public class FSTEntry implements Comparable<FSTEntry> { //FST entry for First Fit Scheduling Algorithm

    public int address;
    public int size;

    public FSTEntry(int address, int size) {
        this.address = address;
        this.size = size;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getSize() { return size; }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int compareTo(FSTEntry f) {
        if(address > f.getAddress()) return 1;
        else if (address < f.getAddress()) return -1;
        return 0;
    }

}
