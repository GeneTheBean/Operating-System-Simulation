import com.sun.org.apache.bcel.internal.generic.MONITORENTER;
import jdk.nashorn.internal.scripts.JO;

import java.util.*;
import java.util.stream.Collectors;
/**
 * Created by bean on 4/13/2017.
 */

public class os {
    public static LinkedList<PCB> JobTable;         //List of all jobs in the system
    public static HashMap<Integer, PCB> JobMap;     //Map of all job nums and their relative Job Table refrences
    public final static int MAX_MEM = 100;          //Capacity for the Free Space Table
    public static List<FSTEntry> FreeSpaceTable;    //First Fit Free Space Table
    public static Queue<PCB> readyQueue;            //Queue for jobs ready to run
    public static Queue<PCB> diskQueue;             //Queue for jobs requesting disk service
    public static List<PCB> swapInQueue;            //Queue for jobs to swap in
    public static List<PCB> swapOutQueue;           //Queue for jobs to swap out
    public static List<PCB> terminatedJobs;         //Stores terminated jobs in core
    public static List<PCB> priorityQueue;          //Stores all of the jobs by their prirority
    public static Map<PCB, Integer> noSwapSpace;    //Stores all of the jobs that cannot fit
    public static int numJobs = 0;                  //Number of jobs in the Job Table
    public static int swapDirection = -1;           //Stores the direction of the last swap
    public static PCB swapJob;                      //Reference to the swap job
    public static PCB diskJob;                      //Reference to the disk job
    public static PCB currentJob;                   //Reference to the running job
    public static int osClock = 0;                  //Keeps track of os time
    public static boolean drumBusy;                 //Indicates whether drum is busy
    public static boolean diskBusy;                 //Indicates whether disk is busy
    public static boolean makingRoomForJob;         //Indicates whether os is currently making room for a process
    public static boolean jobTerminated;            //Indicates whther a job has terminated

    public static void siodisk(int jobnum) {
        sos.siodisk(jobnum);
        diskJob = JobMap.get(jobnum);
        diskJob.setPendingIO(diskJob.getPendingIO() - 1);
        diskBusy = true;
    }

    public static void siodrum(int jobnum, int jobsize, int coreaddress, int direction) {
        sos.siodrum(jobnum, jobsize, coreaddress, direction);
        swapJob = JobMap.get(jobnum);
        swapDirection = direction;
        drumBusy = true;
    }

    public static void ontrace() { sos.ontrace(); }

    public static void offtrace() { sos.offtrace(); }

    public static void startup() {
        JobTable = new LinkedList<PCB>();
        JobMap = new HashMap<Integer, PCB>();
        FreeSpaceTable = new LinkedList<FSTEntry>();
        FSTEntry full = new FSTEntry(0, MAX_MEM);
        FreeSpaceTable.add(full);
        readyQueue = new LinkedList<PCB>();
        diskQueue = new LinkedList<PCB>();
        swapInQueue = new LinkedList<PCB>();
        swapOutQueue = new LinkedList<PCB>();
        terminatedJobs = new LinkedList<PCB>();
        priorityQueue = new ArrayList<>();
        noSwapSpace = new HashMap<PCB, Integer>();
        currentJob = null;
        diskJob = null;
        swapJob = null;
        drumBusy = false;
        diskBusy = false;
        makingRoomForJob = false;
        jobTerminated = false;
        //sos.ontrace();
    }

    public static void Crint(int[] a, int[] p) {    //New Job Arrival
        interruptCurrentJob(p[5]);
        if (numJobs == 50) {
            System.out.println("Max number of jobs (50) exceeded!");
        }
        else {
            PCB job = new PCB(p);                   //Creates new job
            JobTable.add(job);                      //Adds the job to the Job Table
            JobMap.put(job.getNum(), job);          //Job is added the JobMap for reference by job number
            priorityQueue.add(job);                 //Priority queue is updated
            sortPriorityQueue();                    //Priority queue is sorted on each job arrival
            numJobs++;                              //Number of jobs in the system is updated
            job.setState(1);                        //The job is now in its 'new' state
        }
        swapper(a, p);
    }

    public static void Dskint(int[] a, int[] p) {   //Disk job finished I/O.
        interruptCurrentJob(p[5]);

        if(diskJob.killBit() == false) {            //If job was latched (couldn't kill) terminate it
            diskJob.setKillBit(true);
            jobTerminated = true;
            terminatedJobs.add(diskJob);
        }

        if(diskJob.getPendingIO() == 0)             //The job has finished all of I/O and must be unblocked.
            diskJob.setBlocked(false);

        diskJob = findDiskJob();                    //Calls method that returns a disk job
        if(diskJob == null) {
            diskBusy = false;
        }

        else {
            siodisk(diskJob.getNum());
        }
        swapper(a, p);
    }

    public static void Drmint(int[] a, int[] p) {   //Job either finished swapping in to or out of memory
        interruptCurrentJob(p[5]);
        if(swapDirection == 0) {                    //Job was swapped from drum to core, update the FST,
            moveIntoMemory(swapJob);                 //and add it to the ready queue
            readyQueue.add(swapJob);

            if(swapJob.getPendingIO() != 0) {       //If the job already had pending I/O,
                diskQueue.add(swapJob);             //push it back into the disk queue
            }
        }

        else if(swapDirection == 1) {               //Job was removed from core, update the FST
            moveOutOfMemory(swapJob);
            if(makingRoomForJob) {                  //This job was removed to make room for another one.
                makingRoomForJob = false;           //It is kept in the JobTable.
                swapJob.setState(1);                //New
                swapJob.setInCore(false);           //Not in core
            }

            else {                                  //Calls remove job to let os know that the job
                removeJob(swapJob);                 // has been moved out of core
            }
        }
        swapJob = null;
        drumBusy = false;
        swapper(a, p);
    }

    public static void Tro(int[] a, int[] p) {  //The running job either ran out of memory,
        interruptCurrentJob(p[5]);              //or only used some of its time slice
        PCB job = currentJob;
        if(job.getMaxCPUTime() == 0) {
            if(job == diskJob) {                //Job currently doing I/O cannot be killed
                job.setKillBit(false);
            }

            else {
                jobTerminated = true;
                terminatedJobs.add(job);
                checkTerminatedJob();
                if (job == diskJob) { diskJob = null; }
            }
        }
        swapper(a, p);
    }

    public static void Svc(int[] a, int[] p) {
        switch (a[0]) {
            case 5:                                 //Running job has terminated
                terminatedJobs.add(currentJob);
                jobTerminated = true;
                currentJob.setState(5);
                break;
            case 6:                                 //Running job requests I/O
                currentJob.setPendingIO(currentJob.getPendingIO() + 1);
                SvcDisk(a, p);
                break;
            case 7:
                interruptCurrentJob(p[5]);
                currentJob.setBlocked(true);        //Running job requests to block
                if(currentJob != diskJob && currentJob.getPendingIO() == 0) {
                    currentJob.setBlocked(false);   //The can't be blocked if it has no pending I/O requests
                }
                break;
            default:
                break;
        }
        osClock = p[5];
        swapper(a, p);
    }

    public static void SvcDisk(int []a, int []p) {
        interruptCurrentJob(p[5]);
        diskQueue.add(currentJob);

        if(!diskBusy) {
            diskJob = diskQueue.peek();
            siodisk(diskJob.getNum());
        }
    }

    public static void swapper(int []a, int []p) {
        findSwapInJob();
        findSwapOutJob();
        scheduler(a, p);
    }

    public static boolean findSwapInJob() {
        if(makingRoomForJob) return false;  //Can't swap in until swapper makes room for job that doesn't fit
        checkTerminatedJob();

        for(int i = 0;i < JobTable.size();i++) {    //If no jobs to swap, go to the scheduler
            PCB job = JobTable.get(i);
            if (job.getState() == 1 && !noSwapSpace.containsKey(job) && job != swapJob
                    && !swapInQueue.contains(job)) { //Job can be swapped
                swapInQueue.add(job);
                break;
            }
        }

        if(!swapInQueue.isEmpty()) {
            PCB drumJob = swapInQueue.get(0);

            if(!drumBusy) {
                swapInQueue.remove(drumJob);
                FindFSTEntry(drumJob);
                if(noSwapSpace.containsKey(drumJob)) {
                    makeRoomFor(drumJob);
                }
                else {
                    siodrum(drumJob.getNum(), drumJob.getSize(), drumJob.getAddress(), 0);
                    return true;
                }
            }
        }
        return false;
    }

    public static void findSwapOutJob() {                  //Finds a job to swap out
        checkTerminatedJob();
        if (!swapOutQueue.isEmpty()) {
            PCB swapOutJob = swapOutQueue.get(0);
            if (!drumBusy) {
                swapOutQueue.remove(swapOutJob);
                swapJob = swapOutJob;
                swapOutQueue.remove(swapJob);
                siodrum(swapJob.getNum(), swapJob.getSize(), swapJob.getAddress(), 1);
                swapJob.setPriority(swapJob.getPriority() + 1);//Each time a job gets swapped out, increment its priority
            }
        }
    }

    public static void scheduler(int[] a, int[] p) {       //Implementation of Shortest Remaining Time Next Algorithm
        List<PCB> remainingTimeOrder = readyQueue.stream() //Returns a list of runnable jobs
                .filter(job -> !job.blocked())             //Can't Run Blocked Jobs
                .collect(Collectors.toList());

        if(remainingTimeOrder.isEmpty()) {
            if (!diskBusy && !diskQueue.isEmpty())
                siodisk(diskQueue.peek().getNum());        //Check I/O queue
            a[0] = 1;                                      //No jobs to run
            return;
        }

        else {
            Collections.sort(remainingTimeOrder, (p1,p2) ->  p1.getMaxCPUTime() - p2.getMaxCPUTime());
            PCB job = remainingTimeOrder.remove(0);
            readyQueue.remove(job);
            p[2] = job.getAddress();
            p[3] = job.getSize();
            p[4] = job.getMaxCPUTime();
            a[0] = 2;
            job.setState(3);                               //Running state
            currentJob = job;
        }
        //Display.Info();
    }


    public static void FindFSTEntry(PCB job) {              //Finds a free space in FST for the job
        boolean foundSpace = false;
        for (int i = 0; i < FreeSpaceTable.size(); i++) {   //Iterates through all of the FST entries
            FSTEntry entry = FreeSpaceTable.get(i);         //for the First Fit Algorithm
            if (entry.getSize() >= job.getSize()) {         //Job fits
                if (noSwapSpace.containsKey(job)) {
                    noSwapSpace.remove(job);
                }
                job.setAddress(entry.getAddress());
                foundSpace = true;
                break;
            }
        }

        if(!foundSpace) {
            noSwapSpace.put(job, 0);
            return;
        }
        sortFST();
    }

    public static void moveIntoMemory(PCB job) {    //Updates the FST for job coming in to memory
        boolean foundFSTEntry = false;
        for(int i = 0;i < FreeSpaceTable.size();i++) { //Job is in memory, FST needs to update
            FSTEntry entry = FreeSpaceTable.get(i);
            if (entry.getAddress()  <= job.getAddress() &&
                    entry.getAddress() + entry.getSize() >= job.getAddress() + job.getSize()) {
                if(job.getAddress() == entry.getAddress()) { //Touches the lower bound
                    entry.setAddress(entry.getAddress() + job.getSize());
                    entry.setSize(entry.getSize() - job.getSize());
                    if (entry.getSize() == 0) { FreeSpaceTable.remove(entry); }
                    foundFSTEntry = true;
                }

                else if(job.getAddress() + job.getSize() ==
                        entry.getAddress() + entry.getSize()) { //Touches the upper bound
                    entry.setSize(entry.getSize() - job.getSize());
                    foundFSTEntry = true;
                }

                else {  //Within in the boundaries, splitting into two FST entries
                    int original = entry.getSize();
                    entry.setSize(job.getAddress() - entry.getAddress());
                    FSTEntry split = new FSTEntry(job.getAddress() + job.getSize(),
                            (entry.getAddress() + original) - (job.getAddress() + job.getSize()));
                    FreeSpaceTable.add(split);
                    foundFSTEntry = true;
                }

                if(foundFSTEntry == true) {
                    sortFST();
                    job.setInCore(true);
                    job.setState(2);    //Ready
                    break;
                }
            }
        }
    }

    public static void moveOutOfMemory(PCB job) {   //Updates the Free Space Table for job coming out of memory
        FSTEntry entry = new FSTEntry(job.getAddress(), job.getSize());
        FreeSpaceTable.add(entry);
        sortFST();
        checkAdjacentFSTEntry();
    }

    public static void interruptCurrentJob(int sosClock) {     //Stops the running job
        if(currentJob != null && numJobs != 0) {
            PCB job = currentJob;
            if (job.getState() == 3) {                      //Update Remaining CPU time for running job
                job.setMaxCPUTime(job.getMaxCPUTime() - (sosClock - osClock));
                if(job.getMaxCPUTime() != 0) { readyQueue.add(job);
                }
                job.setState(2);                                //Ready state
            }
        }
        osClock = sosClock;                                     //Update os timer
    }

    public static void makeRoomFor(PCB job) {           //Finds a replacement job for job coming in
        boolean foundReplacement = false;
        for(int i = 0;i < priorityQueue.size();i++) {
            PCB p = priorityQueue.get(i);
            if(p != job && p != diskJob && p.inCore()
                    && !terminatedJobs.contains(p)) {   //Necessary conditions for replacement job
                p.setState(1);                          //The job being replaced is removed from ready state
                if(readyQueue.contains(p))
                    readyQueue.remove(p);
                if(diskQueue.contains(p))
                    diskQueue.remove(p);
                swapOutQueue.add(0, p);           //Job to be replaced queued in for swap out
                swapInQueue.add(0, job);          //Job replacing queued in for swap in
                makingRoomForJob = true;
                foundReplacement = true;
                return;
            }
        }

        if(!foundReplacement) {
            swapInQueue.add(job);
        }
    }

    public static PCB findDiskJob() {
        //Check for remaining disk job remaining I/O
        if(diskJob.getPendingIO() != 0) { //Previous I/O finished
            return diskJob;
        }
        else {  //No more pending I/O for current disk job
            diskQueue.remove(diskJob);
            if(!diskQueue.isEmpty())
                return diskQueue.peek();
        }
        return null;
    }

    public static void terminate(PCB job) {
        siodrum(job.getNum(), job.getSize(), job.getAddress(), 1);
    }

    public static void checkAdjacentFSTEntry () {   //Checks for adjacent FST entries and updates table
        for(int i = 0;i < FreeSpaceTable.size() - 1;i++) {
            FSTEntry current = FreeSpaceTable.get(i);
            FSTEntry next = FreeSpaceTable.get(i + 1);
            if(current.getAddress() + current.getSize() >= next.getAddress()){
                current.setSize(current.getSize() + next.getSize());
                FreeSpaceTable.remove(next);
                checkAdjacentFSTEntry();
            }
        }
    }

    public static boolean allJobsBlocked() {  //Checks to see if all ready are blocked
        boolean allJobsBlocked = true;
        for(PCB job: readyQueue) {
            if(job.blocked() == false)
                allJobsBlocked = false;

            else {  //Jobs that are blocked but not latched are unblocked
                if(job != diskJob && !diskQueue.contains(job)) {
                    job.setBlocked(false);
                    allJobsBlocked = false;
                }
            }
        }
        return allJobsBlocked;
    }

    public static boolean inSOSCore(PCB job) {  //Checks to see if the job is in sos core
        for (int i = 1; i < sos.JobTable.length; i++) {
            if (job.getNum() == sos.JobTable[i].JobNo()) {   //Found job in sos job table
                if (!sos.JobTable[i].InCore()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void checkTerminatedJob() {   //Checks to see if the terminated job is in core
        if(jobTerminated) {
            PCB firstTerminatedJob = terminatedJobs.get(0);
            if(!inSOSCore(firstTerminatedJob)) {
                terminatedJobs.remove(0);
                firstTerminatedJob.setState(5); //Terminated
                moveOutOfMemory(firstTerminatedJob);
                removeJob(firstTerminatedJob);
                if(terminatedJobs.isEmpty()) jobTerminated = false;
            }
        }
    }

    public static void removeJob(PCB job) {     //Job termination steps
        JobTable.remove(job);
        JobMap.remove(job.getNum());
        readyQueue.remove(job);
        priorityQueue.remove(job);
        if(currentJob == job) currentJob = null;
        job.setBlocked(false);
        numJobs--;
    }

    public static void sortPriorityQueue() {
        Collections.sort(priorityQueue, (p1, p2) -> p1.compareTo(p2));
    }

    public static void sortFST() { Collections.sort(FreeSpaceTable, (f1,f2) -> f1.compareTo(f2)); }

}