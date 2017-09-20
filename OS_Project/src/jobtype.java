/**
 * Created by bean on 4/14/2017.
 */
class jobtype {
    int JobNo;
    int Size;
    double StartTime;
    int CpuTimeUsed;
    int MaxCpuTime;
    int TermCpuTime;
    int NextSvc;
    int IOPending;
    int IOComp;
    int Priority;
    int JobType;
    boolean Blocked;
    boolean Latched;
    boolean InCore;
    boolean Terminated;
    boolean Overwrite;

    jobtype() {
    }

    boolean Blocked() {
        return this.Blocked;
    }

    void Blocked(boolean var1) {
        this.Blocked = var1;
    }

    int CpuTimeUsed() {
        return this.CpuTimeUsed;
    }

    void CpuTimeUsed(int var1) {
        this.CpuTimeUsed = var1;
    }

    int IOComp() {
        return this.IOComp;
    }

    void IOComp(int var1) {
        this.IOComp = var1;
    }

    int IOPending() {
        return this.IOPending;
    }

    void IOPending(int var1) {
        this.IOPending = var1;
    }

    boolean InCore() {
        return this.InCore;
    }

    void InCore(boolean var1) {
        this.InCore = var1;
    }

    int JobNo() {
        return this.JobNo;
    }

    void JobNo(int var1) {
        this.JobNo = var1;
    }

    int JobType() {
        return this.JobType;
    }

    void JobType(int var1) {
        this.JobType = var1;
    }

    boolean Latched() {
        return this.Latched;
    }

    void Latched(boolean var1) {
        this.Latched = var1;
    }

    int MaxCpuTime() {
        return this.MaxCpuTime;
    }

    void MaxCpuTime(int var1) {
        this.MaxCpuTime = var1;
    }

    int NextSvc() {
        return this.NextSvc;
    }

    void NextSvc(int var1) {
        this.NextSvc = var1;
    }

    boolean Overwrite() {
        return this.Overwrite;
    }

    void Overwrite(boolean var1) {
        this.Overwrite = var1;
    }

    int Priority() {
        return this.Priority();
    }

    void Priority(int var1) {
        this.Priority = var1;
    }

    int Size() {
        return this.Size;
    }

    void Size(int var1) {
        this.Size = var1;
    }

    double StartTime() {
        return this.StartTime;
    }

    void StartTime(double var1) {
        this.StartTime = var1;
    }

    int TermCpuTime() {
        return this.TermCpuTime;
    }

    void TermCpuTime(int var1) {
        this.TermCpuTime = var1;
    }

    boolean Terminated() {
        return this.Terminated;
    }

    void Terminated(boolean var1) {
        this.Terminated = var1;
    }
}
