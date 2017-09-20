/**
 * Created by bean on 4/14/2017.
 */


public class PCB implements Comparable<PCB> {

    private int num;
    private int priority;
    private int size;
    private int maxCPUTime;
    private int state;
    private int address;
    private boolean blocked;
    private boolean killBit;
    private boolean inCore;
    private int pendingIO;

    /******************
     * Process States *
     * 1 - New        *
     * 2 - Ready      *
     * 3 - Running    *
     * 4 - Waiting    *
     * 5 - Terminated *
     ******************/

    public PCB(int []p) {
        num = p[1];
        priority = p[2];
        size = p[3];
        maxCPUTime = p[4];
        state = 0;
        address = 0;   //Not in core yet
        blocked = false;
        killBit = true;
        inCore = false;
        pendingIO = 0;
    }

    public int getNum() { return num; }

    public int getPriority() { return priority; }

    public void setPriority(int priority) { this.priority = priority; }

    public int getSize() { return size; }

    public int getMaxCPUTime() { return maxCPUTime; }

    public void setMaxCPUTime(int maxCPUTime) { this.maxCPUTime = maxCPUTime; }

    public int getState() { return state; }

    public void setState(int state) { this.state = state; }

    public int getAddress() { return address; }

    public void setAddress(int address) { this.address = address; }

    public boolean inCore() { return inCore; }

    public void setInCore(boolean inCore) { this.inCore = inCore; }

    public boolean blocked() { return blocked; };

    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public boolean killBit() { return killBit; }

    public void setKillBit(boolean killBit) { this.killBit = killBit; }

    public int getPendingIO() { return pendingIO; }

    public void setPendingIO(int pendingIO) { this.pendingIO = pendingIO; }

    @Override
    public int compareTo(PCB j) {
        if(priority > j.getPriority()) return 1;
        else if (priority < j.getPriority()) return -1;
        return 0;
    }

}
