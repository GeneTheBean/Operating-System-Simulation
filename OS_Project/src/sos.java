/**
 * Created by bean on 4/14/2017.
 */
public class sos {
    static final int MAXINT = 20000000;
    static final int Crintt = 1;
    static final int Diskintt = 2;
    static final int Drumintt = 3;
    static final int Troo = 4;
    static final int Svcc = 5;
    static final int MaxJTable = 50;
    static final int MaxDiffJobs = 30;
    static final int TrivCutoff = 100;
    static final int CrMaplmt = 21;
    static final int EndOfDay = 900000;
    static boolean trace;
    static boolean err;
    static boolean err13;
    static int Clock;
    static int[] Next;
    static int[] Parms;
    static int jti;
    static int cmi;
    static int[] Action;
    static jobtype[] JobTable;
    static int[][] CoreMap;
    static double DrumUtil;
    static double DiskUtil;
    static double CpuUtil;
    static double CoreUtil;
    static double DelCntr;
    static double DilCntr;
    static int JobCntr;
    static int TermCntr;
    static double AvgDil;
    static double AvgResponse;
    static double AvgDisk;
    static int[] SizeDist;
    static int[] MCpuTimeDist;
    static int[] TCpuTimeDist;
    static int[] PriorDist;
    static int[][][] Times;
    static int[][] WhichSvc;
    static int[] DrmTimes;
    static int[] DskTimes;
    static int[] CrdTimes;
    static int CardTimesPtr;
    static int DiskTimesPtr;
    static int DrumTimesPtr;
    static boolean DiskBusy;
    static boolean DrumBusy;
    static int JobSwpdNo;
    static int CoreAddr;
    static int Sze;
    static boolean writ;
    static int JobServdIndex;
    static int DrmStTm;
    static int DskStTm;
    static double LastSnap;
    static double SpstInt;
    static double LstCrChk;
    static int CondCode;
    static String[] CondMess = new String[]{"ILLEGAL ERROR", "normally (terminate svc issued) ", "abnormally (max cpu time exceeded) "};
    static String[] errorarray = new String[]{"ILLEGAL ERROR", "/// MAIN ERROR ** INCORRECT VALUE OF ACTION SET BY FIND THE NEXT EVENT ///", "/// MAIN ERROR ** INCORRECT VALUE OF ACTION SET BY GEN ROUTINES ///", "*** MAIN ERROR ** INCORRECT VALUE OF ACTION RETURNED BY OS ***", "*** GENCRINT ERROR ** JOB TABLE FULL - OS PROCESSING JOBS TOO SLOWLY ***", "/// RUN ERROR ** JOB SPECIFIED DOES NOT EXIST IN JOB TABLE ///", "/// RUN ERROR ** JOB SPECIFIED IS NOT IN CORE ///", "/// RUN ERROR ** JOB SPECFIED NOT IN CORE-MISSING IN CORE MAP ///", "*** RUN ERROR ** INCORRECT START LOCATION IN CORE SPECIFIED FOR JOB ***", "*** RUN ERROR ** INCORRECT SIZE IN CORE SPECIFIED FOR JOB ***", "*** RUN ERROR ** JOB SPECIFIED TO RUN IS BLOCKED ***", "*** RUN ERROR ** JOB TERMINATED OR HAS EXCEEDED MAXIMUM CPU TIME ***", "*** RUN ERROR ** QUANTUM OF TIME FOR JOB SPECIFIED EXCEEDED MAXIMUM CPU TIME ***", "*** RUN ERROR ** STARTING ADDRESS OR LENGTH SPECIFIED IS INCORRECT ***", "*** RUN ERROR ** QUANTUM SPECIFIED IS NEGATIVE OR ZERO ***", "*** IDLE ERROR ** UNBLOCKED JOBS EXISTS IN CORE ***", "*** IDLE ERROR ** BLOCKED JOBS IN CORE BUT DISK IDLE ***", "*** IDLE ERROR ** OS FAILS TO SWAP JOBS FROM DRUM INTO EMPTY CORE ***", "*** SIODISK ERROR ** JOB SPECIFIED DOES NOT EXIST ***", "*** SIODISK ERROR ** JOB SPECIFIED NOT IN CORE ***", "*** SIODISK ERROR ** JOB SPECIFIED HAS TERMINATED ***", "*** SIODISK ERROR ** DISK IS BUSY ***", "*** SIODISK ERROR ** JOB HAS NO IO PENDING ***", "*** SIODRUM ERROR ** DRUM IS BUSY ***", "*** SIODRUM ERROR ** JOB SPECIFIED DOES NOT EXIST ***", "*** SIODRUM ERROR ** SIZE OF JOB SPECIFIED IS INCORRECT ***", "*** SIODRUM ERROR ** JOB SPECIFIED HAS TERMINATED ***", "*** SIODRUM ERROR ** JOB SPECIFIED NOT IN CORE ***", "*** SIODRUM ERROR ** JOB SPECIFIED IS LATCHED - IT CANNOT BE SWAPPED ***", "/// SIODRUM ERROR ** JOB SPECIFIED NOT IN CORE - DOES NOT EXIST IN CORE MAP ///", "*** SIODRUM ERROR ** START LOCATION OF JOB SPECIFIED IS INCORRECT ***", "/// SIODRUM ERROR ** SIZE OF JOB SPECIFIED IS INCORRECT ///", "*** SIODRUM ERROR ** JOB SPECIFIED IS ALREADY IN CORE - NO NEED TO SWAP IN ***", "*** SIODRUM ERROR ** START LOCATION OF JOB SPECIFIED IS NEGATIVE ***", "*** SIODRUM ERROR ** CORE ADDRESSES OF JOB SPECIFIED OVERLAP OTHER JOBS ***", "/// SIODRUM ERROR ** CORE MAP FULL - NO ROOM IN CORE ///", "*** SIODRUM ERROR ** ATTEMPT TO SWAP IN JOB WITH SIZE = 0 ***", "/// SAVESTATISTICS ERROR ** JOB SPECIFIED NOT IN CORE MAP ///", "/// GENSVC ERROR ** INCORRECT SWITCH VALUE ///", "/// GENDSKINT ERROR ** CAN\'T FIND JOB IN CORE MAP IN ORDER TO DELETE ///", "           "};

    public sos() {
        Next = new int[6];
        Parms = new int[6];
        JobTable = new jobtype[52];
        CoreMap = new int[22][4];
        SizeDist = new int[30];
        MCpuTimeDist = new int[30];
        TCpuTimeDist = new int[30];
        PriorDist = new int[30];
        Times = new int[3][31][10];
        WhichSvc = new int[31][10];
        DrmTimes = new int[10];
        DskTimes = new int[10];
        CrdTimes = new int[10];
        Action = new int[1];
    }

    static int coreused() {
        int var0 = 0;

        for(int var2 = 1; var2 <= 20; ++var2) {
            if(CoreMap[var2][1] > 0) {
                var0 = var0 + CoreMap[var2][3] - CoreMap[var2][2] + 1;
            }
        }

        return var0;
    }

    static void error(int var0) {
        err = true;
        if(var0 == 13) {
            err13 = true;
        }

        System.out.println("\n\n\n*** Clock:  " + Clock + ", *** FATAL ERROR:  " + var0 + "\n");
        System.out.println(errorarray[var0] + "\n");
        System.out.println("Current Value of Registers:\n\n\ta = " + Action[0]);
        System.out.print("\tp [1..5] = ");

        for(int var1 = 1; var1 <= 5; ++var1) {
            System.out.print(" " + Parms[var1] + "  ");
        }

        System.out.println("\n");
        Statistics();
    }

    static void FindNextEvent() {
        int var1 = 0;
        int var2 = 20000000;

        int var0;
        for(var0 = 1; var0 <= 3; ++var0) {
            if(var2 > Next[var0]) {
                var1 = var0;
                var2 = Next[var0];
            }
        }

        if(Action[0] == 2) {
            for(var0 = 5; var0 >= 4; --var0) {
                if(var2 >= Next[var0] + Clock) {
                    var1 = var0;
                    var2 = Next[var0] + Clock;
                }
            }

            JobTable[jti].CpuTimeUsed = JobTable[jti].CpuTimeUsed + var2 - Clock;
            CpuUtil = CpuUtil + (double)var2 - (double)Clock;
        }

        Clock = var2;
        Action[0] = var1;
    }

    static void GenCrint() {
        int var0;
        for(var0 = 1; var0 <= 50 && !JobTable[var0].Overwrite; ++var0) {
            ;
        }

        if(var0 > 50) {
            error(4);
        }

        if(!err) {
            ++JobCntr;
            int var1 = JobCntr % 30;
            JobTable[var0].JobNo = JobCntr;
            JobTable[var0].Size = SizeDist[var1];
            JobTable[var0].StartTime = (double)Clock;
            JobTable[var0].CpuTimeUsed = 0;
            JobTable[var0].MaxCpuTime = MCpuTimeDist[var1];
            JobTable[var0].TermCpuTime = TCpuTimeDist[var1];
            JobTable[var0].NextSvc = 0;
            JobTable[var0].IOPending = 0;
            JobTable[var0].IOComp = 0;
            JobTable[var0].Priority = PriorDist[var1];
            if(JobTable[var0].MaxCpuTime >= 100 && JobTable[var0].TermCpuTime >= 100) {
                JobTable[var0].JobType = 2;
            } else {
                JobTable[var0].JobType = 1;
            }

            JobTable[var0].Blocked = false;
            JobTable[var0].Latched = false;
            JobTable[var0].InCore = false;
            JobTable[var0].Terminated = false;
            JobTable[var0].Overwrite = false;
            Parms[1] = JobTable[var0].JobNo;
            Parms[2] = JobTable[var0].Priority;
            Parms[3] = JobTable[var0].Size;
            Parms[4] = JobTable[var0].MaxCpuTime;
            Parms[5] = Clock;
            ++CardTimesPtr;
            int var2 = CardTimesPtr % 10;
            Next[1] = CrdTimes[var2] + Clock;
            if(trace) {
                System.out.print("*** Clock:  " + Parms[5]);
                System.out.print(", Job " + Parms[1] + " Arriving ");
                System.out.print("Size:  " + Parms[3]);
                System.out.println(" Priority:  " + Parms[2]);
                System.out.println(" Max CPU Time:  " + Parms[4] + "\n");
            }

            if(JobTable[var0].MaxCpuTime <= 0 || JobTable[var0].Size <= 0 || JobTable[var0].Size > 100) {
                if(trace) {
                    System.out.println(" But job deleted due to max cpu time or size.");
                }

                JobTable[var0].Overwrite = true;
                ++DelCntr;
            }
        }

    }

    static void GenDrmint() {
        if(trace) {
            System.out.print("*** Clock:  " + Clock + ", Swap ");
            if(writ) {
                System.out.print("out");
            } else {
                System.out.print("in");
            }

            System.out.println(" completed for job " + JobSwpdNo);
        }

        if(!writ) {
            int var0;
            for(var0 = 1; var0 < 50 && JobTable[var0].JobNo != JobSwpdNo; ++var0) {
                ;
            }

            JobTable[var0].InCore = true;
            int var1 = coreused();
            CoreUtil += (double)var1 * ((double)Clock - LstCrChk);
            LstCrChk = (double)Clock;
            CoreMap[cmi][1] = JobSwpdNo;
            CoreMap[cmi][2] = CoreAddr;
            CoreMap[cmi][3] = CoreAddr + Sze - 1;
            PutCoreMap();
        }

        DrumBusy = false;
        DrumUtil = DrumUtil + (double)Clock - (double)DrmStTm;
        Next[3] = 1800000;
        Action[0] = 3;
        Parms[5] = Clock;
    }

    static void GenDskint() {
        int var2 = JobServdIndex;
        if(trace) {
            System.out.print("*** Clock:  " + Clock + ", IO Completion");
            System.out.println(" for job " + JobTable[var2].JobNo);
        }

        DiskBusy = false;
        JobTable[var2].Latched = false;
        if((double)JobTable[var2].IOPending == 1.0D) {
            JobTable[var2].Blocked = false;
        }

        --JobTable[var2].IOPending;
        ++JobTable[var2].IOComp;
        DiskUtil = DiskUtil + (double)Clock - (double)DskStTm;
        Next[2] = 1800000;
        Action[0] = 2;
        Parms[5] = Clock;
        if(JobTable[var2].Terminated && (double)JobTable[var2].IOPending == 0.0D) {
            int var3 = coreused();
            CoreUtil += (double)var3 * ((double)Clock - LstCrChk);
            LstCrChk = (double)Clock;

            int var0;
            for(var0 = 1; var0 <= 21 && JobTable[var2].JobNo != CoreMap[var0][1]; ++var0) {
                ;
            }

            if(var0 > 21) {
                error(39);
            }

            if(!err) {
                for(int var1 = 1; var1 <= 3; ++var1) {
                    CoreMap[var0][var1] = 0;
                }

                JobTable[var2].Overwrite = true;
                JobTable[var2].InCore = false;
            }
        }

    }

    static void GenSvc() {
        Parms[5] = Clock;
        int var0;
        if(JobTable[jti].TermCpuTime <= JobTable[jti].CpuTimeUsed) {
            var0 = 0;
        } else {
            byte var1 = 31;
            var0 = WhichSvc[jti % var1][JobTable[jti].NextSvc % 10];
        }

        if(trace) {
            System.out.print("*** Clock:  " + Clock + ", Job " + JobTable[jti].JobNo);
            switch(var0) {
                case 0:
                    System.out.print(" terminate ");
                    break;
                case 1:
                    System.out.print(" block ");
                    break;
                case 2:
                    System.out.print(" I/O ");
            }

            System.out.println("Svc issued");
        }

        if(var0 >= 0 && var0 <= 2) {
            switch(var0) {
                case 0:
                    Action[0] = 5;
                    CondCode = 1;
                    SaveStatistics();
                    return;
                case 1:
                    Action[0] = 7;
                    if(JobTable[jti].IOPending > 0) {
                        JobTable[jti].Blocked = true;
                    }

                    ++JobTable[jti].NextSvc;
                    return;
                case 2:
                    Action[0] = 6;
                    ++JobTable[jti].IOPending;
                    ++JobTable[jti].NextSvc;
                    return;
            }
        } else {
            error(38);
        }

    }

    static void GenTro() {
        if(trace) {
            System.out.print("Clock:  " + Clock + ", time run out ");
            System.out.println("on Job " + JobTable[jti].JobNo);
        }

        if(JobTable[jti].CpuTimeUsed >= JobTable[jti].MaxCpuTime) {
            CondCode = 2;
            SaveStatistics();
        }

        Parms[5] = Clock;
    }

    static void Idle() {
        if(trace) {
            System.out.println("*** Clock:  " + Clock + ", executive idling");
        }

        boolean var1 = false;
        boolean var2 = false;

        for(int var0 = 1; var0 <= 50 && !var2; ++var0) {
            if(JobTable[var0].InCore) {
                var1 = true;
                if(!JobTable[var0].Blocked && !JobTable[var0].Terminated) {
                    var2 = true;
                }
            }
        }

        if(var2) {
            error(15);
        }

        if(!err && !DiskBusy && var1) {
            error(16);
        }

        if(!err && !DrumBusy && JobCntr > TermCntr && !var1) {
            error(17);
        }

    }

    static void init() {
        int[] var0 = new int[10];
        DrmTimes[0] = 11;
        DrmTimes[1] = 17;
        DrmTimes[2] = 21;
        DrmTimes[3] = 19;
        DrmTimes[4] = 15;
        DrmTimes[5] = 23;
        DrmTimes[6] = 25;
        DrmTimes[7] = 13;
        DrmTimes[8] = 29;
        DrmTimes[9] = 27;
        DskTimes[0] = 55;
        DskTimes[1] = 85;
        DskTimes[2] = 105;
        DskTimes[3] = 95;
        DskTimes[4] = 75;
        DskTimes[5] = 115;
        DskTimes[6] = 125;
        DskTimes[7] = 65;
        DskTimes[8] = 145;
        DskTimes[9] = 135;
        CrdTimes[0] = 2800;
        CrdTimes[1] = 2600;
        CrdTimes[2] = 3300;
        CrdTimes[3] = 1400;
        CrdTimes[4] = 30;
        CrdTimes[5] = 10;
        CrdTimes[6] = 19;
        CrdTimes[7] = 2850;
        CrdTimes[8] = 2740;
        CrdTimes[9] = 4000;
        SizeDist[0] = 15;
        SizeDist[1] = 18;
        SizeDist[2] = 25;
        SizeDist[3] = 8;
        SizeDist[4] = 10;
        SizeDist[5] = 30;
        SizeDist[6] = 47;
        SizeDist[7] = 27;
        SizeDist[8] = 10;
        SizeDist[9] = 14;
        SizeDist[10] = 30;
        SizeDist[11] = 16;
        SizeDist[12] = 19;
        SizeDist[13] = 23;
        SizeDist[14] = 5;
        SizeDist[15] = 15;
        SizeDist[16] = 6;
        SizeDist[17] = 10;
        SizeDist[18] = 8;
        SizeDist[19] = 7;
        SizeDist[20] = 17;
        SizeDist[21] = 15;
        SizeDist[22] = 40;
        SizeDist[23] = 11;
        SizeDist[24] = 14;
        SizeDist[25] = 17;
        SizeDist[26] = 21;
        SizeDist[27] = 23;
        SizeDist[28] = 5;
        SizeDist[29] = 8;
        MCpuTimeDist[0] = 14000;
        MCpuTimeDist[1] = 23;
        MCpuTimeDist[2] = 2500;
        MCpuTimeDist[3] = 20;
        MCpuTimeDist[4] = 3500;
        MCpuTimeDist[5] = 14;
        MCpuTimeDist[6] = '\ufde8';
        MCpuTimeDist[7] = 100;
        MCpuTimeDist[8] = 10;
        MCpuTimeDist[9] = 1500;
        MCpuTimeDist[10] = 11;
        MCpuTimeDist[11] = 10;
        MCpuTimeDist[12] = 550;
        MCpuTimeDist[13] = 1400;
        MCpuTimeDist[14] = 17;
        MCpuTimeDist[15] = '鱀';
        MCpuTimeDist[16] = 19;
        MCpuTimeDist[17] = 1300;
        MCpuTimeDist[18] = 15;
        MCpuTimeDist[19] = 21;
        MCpuTimeDist[20] = 131;
        MCpuTimeDist[21] = 153;
        MCpuTimeDist[22] = 1000;
        MCpuTimeDist[23] = 32;
        MCpuTimeDist[24] = 18;
        MCpuTimeDist[25] = 5300;
        MCpuTimeDist[26] = 62;
        MCpuTimeDist[27] = 17;
        MCpuTimeDist[28] = 7100;
        MCpuTimeDist[29] = 15;

        int var1;
        for(var1 = 0; var1 <= 14; ++var1) {
            PriorDist[2 * var1] = 1;
            PriorDist[2 * var1 + 1] = 2;
        }

        PriorDist[6] = 5;
        PriorDist[7] = 1;
        TCpuTimeDist[0] = 4000;
        TCpuTimeDist[1] = 21;
        TCpuTimeDist[2] = 2000;
        TCpuTimeDist[3] = 20;
        TCpuTimeDist[4] = 4000;
        TCpuTimeDist[5] = 11;
        TCpuTimeDist[6] = '썐';
        TCpuTimeDist[7] = 90;
        TCpuTimeDist[8] = 9;
        TCpuTimeDist[9] = 100;
        TCpuTimeDist[10] = 10;
        TCpuTimeDist[11] = 12;
        TCpuTimeDist[12] = 500;
        TCpuTimeDist[13] = 1300;
        TCpuTimeDist[14] = 15;
        TCpuTimeDist[15] = 3000;
        TCpuTimeDist[16] = 15;
        TCpuTimeDist[17] = 1200;
        TCpuTimeDist[18] = 13;
        TCpuTimeDist[19] = 20;
        TCpuTimeDist[20] = 130;
        TCpuTimeDist[21] = 150;
        TCpuTimeDist[22] = 900;
        TCpuTimeDist[23] = 37;
        TCpuTimeDist[24] = 20;
        TCpuTimeDist[25] = 2500;
        TCpuTimeDist[26] = 60;
        TCpuTimeDist[27] = 14;
        TCpuTimeDist[28] = 3000;
        TCpuTimeDist[29] = 15;

        int var2;
        for(var1 = 0; var1 <= 30; ++var1) {
            for(var2 = 0; var2 <= 9; ++var2) {
                Times[1][var1][var2] = 3 * (var2 + 1);
            }
        }

        var0[1] = 4;
        var0[2] = 8;
        var0[3] = 11;
        var0[4] = 12;
        var0[5] = 17;
        var0[6] = 21;
        var0[7] = 24;
        var0[8] = 27;
        var0[9] = 30;

        for(var1 = 1; var1 <= 9; ++var1) {
            for(var2 = 0; var2 <= 9; ++var2) {
                Times[1][var0[var1]][var2] = 500 * (var2 + 1);
            }
        }

        for(var1 = 0; var1 <= 14; ++var1) {
            for(var2 = 0; var2 <= 9; ++var2) {
                Times[2][2 * var1][var2] = 3 * (var2 + 1);
            }

            for(var2 = 0; var2 <= 9; ++var2) {
                Times[2][2 * var1 + 1][var2] = 500 * (var2 + 1);
            }
        }

        for(var2 = 0; var2 <= 9; ++var2) {
            Times[2][30][var2] = 3 * (var2 + 1);
        }

        for(var1 = 0; var1 <= 30; ++var1) {
            for(var2 = 0; var2 <= 9; ++var2) {
                WhichSvc[var1][var2] = 2 - var2 % 2;
            }
        }

        for(var1 = 1; var1 <= 51; ++var1) {
            JobTable[var1] = new jobtype();
            JobTable[var1].JobNo = 0;
            JobTable[var1].Size = 0;
            JobTable[var1].StartTime = 0.0D;
            JobTable[var1].CpuTimeUsed = 0;
            JobTable[var1].MaxCpuTime = 0;
            JobTable[var1].TermCpuTime = 0;
            JobTable[var1].NextSvc = 0;
            JobTable[var1].IOPending = 0;
            JobTable[var1].IOComp = 0;
            JobTable[var1].Priority = 0;
            JobTable[var1].JobType = 0;
            JobTable[var1].Blocked = false;
            JobTable[var1].Latched = false;
            JobTable[var1].InCore = false;
            JobTable[var1].Terminated = false;
            JobTable[var1].Overwrite = true;
        }

        for(var1 = 1; var1 <= 20; ++var1) {
            for(var2 = 1; var2 <= 3; ++var2) {
                CoreMap[var1][var2] = 0;
            }
        }

        CoreMap[21][1] = -1;
        CoreMap[21][2] = 100;
        CoreMap[21][3] = 1000000000;
        Clock = 0;
        Action[0] = 1;
        Next[1] = 0;

        for(var1 = 2; var1 <= 5; ++var1) {
            Next[var1] = 900001;
        }

        JobCntr = 0;
        DelCntr = 0.0D;
        TermCntr = 0;
        DilCntr = 0.0D;
        DrumTimesPtr = 0;
        DiskTimesPtr = 0;
        CardTimesPtr = 0;
        AvgDil = 0.0D;
        AvgResponse = 0.0D;
        DrumUtil = 0.0D;
        DiskUtil = 0.0D;
        CpuUtil = 0.0D;
        CoreUtil = 0.0D;
        LstCrChk = 0.0D;
        AvgDisk = 0.0D;

        for(var1 = 0; var1 <= 9; ++var1) {
            AvgDisk += (double)DskTimes[var1];
        }

        AvgDisk /= 10.0D;
        DiskBusy = false;
        DrumBusy = false;
        trace = false;
        LastSnap = 0.0D;
        SpstInt = 60000.0D;
        System.out.println("\n\n\t\t\tOPERATING SYSTEM SIMULATION\n\n");
    }

    public static void offtrace() {
        trace = false;
    }

    public static void ontrace() {
        trace = true;
    }

    static void PutCoreMap() {
        int[] var0 = new int[100];
        if(trace) {
            int var1;
            for(var1 = 0; var1 <= 99; ++var1) {
                var0[var1] = 0;
            }

            for(var1 = 1; var1 <= 20; ++var1) {
                if(CoreMap[var1][1] > 0) {
                    for(int var2 = CoreMap[var1][2]; var2 <= CoreMap[var1][3]; ++var2) {
                        var0[var2] = CoreMap[var1][1];
                    }
                }
            }

            System.out.println("\n\n\t\t\t\tCORE MAP\n");

            for(var1 = 0; var1 < 4; ++var1) {
                System.out.print(" Partition Job   ");
            }

            System.out.println("\n");

            for(var1 = 0; var1 < 25; ++var1) {
                if(var1 < 10) {
                    System.out.print(" ");
                }

                System.out.print("     " + var1 + "\t   " + var0[var1] + "\t      ");
                System.out.print(var1 + 25 + "    " + var0[var1 + 25] + "\t       ");
                System.out.print(var1 + 50 + "    " + var0[var1 + 50] + "  \t");
                System.out.println(var1 + 75 + "    " + var0[var1 + 75]);
            }

            System.out.println();
        }

    }

    static void Run() {
        int var3 = 1;

        int var4;
        for(var4 = 1; var3 < 21 && (Parms[2] != CoreMap[var3][2] || Parms[3] != CoreMap[var3][3] - CoreMap[var3][2] + 1); ++var3) {
            ;
        }

        if(var3 >= 21) {
            error(13);
        }

        if(!err) {
            while(var4 <= 50 && CoreMap[var3][1] != JobTable[var4].JobNo) {
                ++var4;
            }
        }

        if(!err && var4 > 50) {
            error(5);
            var4 = 1;
        }

        jti = var4;
        if(!err && !JobTable[jti].InCore) {
            System.err.println("JOBNO:  " + JobTable[jti].JobNo);
            error(6);
        }

        if(!err && JobTable[jti].Blocked) {
            System.err.println("JOBNO:  " + JobTable[jti].JobNo);
            error(10);
        }

        if(!err && JobTable[jti].Terminated) {
            System.err.println("JOBNO:  " + JobTable[jti].JobNo);
            error(11);
        }

        if(!err && Parms[4] + JobTable[jti].CpuTimeUsed > JobTable[jti].MaxCpuTime) {
            System.err.println("JOBNO:  " + JobTable[jti].JobNo);
            error(12);
        }

        if(!err && Parms[4] <= 0) {
            System.err.println("JOBNO:  " + JobTable[jti].JobNo);
            error(14);
        }

        if(!err) {
            Next[4] = Parms[4];
            int var1 = JobTable[jti].JobType;
            byte var5 = 31;
            int var2 = jti % var5;
            int var0 = JobTable[jti].NextSvc;
            int var9 = Times[var1][var2][9];
            int var10 = var0 / 10;
            int var6 = var0 % 10;
            int var11 = Times[var1][var2][var6];
            int var8 = var9 * var10 + var11;
            int var7 = JobTable[jti].TermCpuTime;
            if(var8 < var7) {
                Next[5] = var8 - JobTable[jti].CpuTimeUsed;
            } else {
                Next[5] = var7 - JobTable[jti].CpuTimeUsed;
            }

            if(trace) {
                System.out.print("*** Clock:  " + Clock + ", ");
                System.out.print("Job " + JobTable[jti].JobNo + " ");
                System.out.print("running size:  " + Parms[3]);
                System.out.println(" Priority:  " + JobTable[jti].Priority);
                System.out.print(" Max CPU Time:  " + JobTable[jti].MaxCpuTime + ", ");
                System.out.println("CPU time used:  " + JobTable[jti].CpuTimeUsed);
                System.out.println();
            }
        }

    }

    static void SaveStatistics() {
        if(JobTable[jti].CpuTimeUsed > 100) {
            double var0 = (double)Times[JobTable[jti].JobType][jti % 31][0];
            double var2;
            if(var0 > AvgDisk) {
                var2 = (double)JobTable[jti].CpuTimeUsed;
            } else {
                double var6 = var0 + AvgDisk;
                if(var6 - var6 >= 0.5D) {
                    var2 = (var0 + AvgDisk) * (double)(JobTable[jti].IOComp - 1) + var0 + 1.0D;
                } else {
                    var2 = (var0 + AvgDisk) * (double)(JobTable[jti].IOComp - 1) + var0;
                }
            }

            double var4 = ((double)Clock - JobTable[jti].StartTime) / var2;
            System.out.print("\n*** Clock:  " + Clock + ", ");
            System.out.print("Job " + JobTable[jti].JobNo + " ");
            System.out.println("terminated " + CondMess[CondCode]);
            printf("Dilation:  %.2f ", var4);
            System.out.print("CPU time:  " + JobTable[jti].CpuTimeUsed);
            System.out.println("  # I/O operations completed:  " + JobTable[jti].IOComp);
            System.out.println(" # I/O operations pending:  " + JobTable[jti].IOPending);
            System.out.println();
            AvgDil += var4;
            ++DilCntr;
        } else {
            double var8 = (double)Clock - JobTable[jti].StartTime;
            AvgResponse += var8;
            System.out.print("\n*** Clock:  " + Clock + ", ");
            System.out.print("Job " + JobTable[jti].JobNo + " ");
            System.out.println("terminated " + CondMess[CondCode]);
            printf(" Response Time:  %.0f ", var8);
            System.out.print("CPU time:  " + JobTable[jti].CpuTimeUsed);
            System.out.println(" # I/O operations completed:  " + JobTable[jti].IOComp);
            System.out.println(" # I/O operations pending:  " + JobTable[jti].IOPending);
            System.out.println();
        }

        ++TermCntr;

        int var14;
        for(var14 = 1; var14 < 21 && JobTable[jti].JobNo != CoreMap[var14][1]; ++var14) {
            ;
        }

        if(var14 == 21) {
            error(37);
        }

        if(!err) {
            if(JobTable[jti].IOPending != 0) {
                JobTable[jti].Terminated = true;
            } else {
                JobTable[jti].Overwrite = true;
                JobTable[jti].InCore = false;
                JobTable[jti].Terminated = true;
                double var10 = (double)coreused();
                CoreUtil += var10 * ((double)Clock - LstCrChk);
                LstCrChk = (double)Clock;

                for(int var15 = 1; var15 <= 3; ++var15) {
                    CoreMap[var14][var15] = 0;
                }
            }

            PutCoreMap();
        }

    }

    public static void main(String[] var0) {
        new sos();
        init();
        os.startup();
        err = false;
        err13 = false;

        while(Clock < 900000 && !err) {
            FindNextEvent();
            if(Action[0] < 6 && Action[0] > 0) {
                switch(Action[0]) {
                    case 1:
                        GenCrint();
                        break;
                    case 2:
                        GenDskint();
                        break;
                    case 3:
                        GenDrmint();
                        break;
                    case 4:
                        GenTro();
                        break;
                    case 5:
                        GenSvc();
                }
            } else {
                error(1);
            }

            if(!err && Action[0] < 8 && Action[0] > 0) {
                switch(Action[0]) {
                    case 1:
                        os.Crint(Action, Parms);
                        break;
                    case 2:
                        os.Dskint(Action, Parms);
                        break;
                    case 3:
                        os.Drmint(Action, Parms);
                        break;
                    case 4:
                        os.Tro(Action, Parms);
                        break;
                    case 5:
                    case 6:
                    case 7:
                        os.Svc(Action, Parms);
                }
            } else if(!err) {
                error(2);
            }

            if(!err && (double)Clock - LastSnap >= SpstInt) {
                SnapShot();
            }

            if(!err) {
                if(Action[0] == 1) {
                    Idle();
                } else if(Action[0] == 2) {
                    Run();
                } else {
                    error(3);
                }
            }
        }

        if(!err) {
            Statistics();
        }

    }

    public static void siodisk(int var0) {
        int var1 = 1;
        if(trace) {
            System.out.println("*** Clock:  " + Clock + ", Job " + var0 + " I/O started");
        }

        while(var1 <= 50 && JobTable[var1].JobNo != var0) {
            ++var1;
        }

        if(var1 > 50) {
            error(18);
            var1 = 1;
        }

        if(!err && !JobTable[var1].InCore) {
            error(19);
        }

        if(!err && JobTable[var1].Overwrite) {
            error(20);
        }

        if(!err && DiskBusy) {
            error(21);
        }

        if(!err && JobTable[var1].IOPending == 0) {
            error(22);
        }

        if(!err) {
            ++DiskTimesPtr;
            DskStTm = Clock;
            Next[2] = Clock + DskTimes[DiskTimesPtr % 10];
            JobTable[var1].Latched = true;
            JobServdIndex = var1;
            DiskBusy = true;
        }

    }

    public static void siodrum(int var0, int var1, int var2, int var3) {
        int var8 = 1;
        JobSwpdNo = var0;
        Sze = var1;
        CoreAddr = var2;
        if(var3 == 1) {
            writ = true;
        } else {
            writ = false;
        }

        if(trace) {
            System.out.print("*** Clock:  " + Clock + ", Job " + JobSwpdNo);
            if(writ) {
                System.out.print(" swapout started.  ");
            } else {
                System.out.print(" swapin started.  ");
            }

            System.out.println("Size:  " + Sze);
            System.out.println(" Starting address:  " + CoreAddr + "\n");
        }

        PutCoreMap();
        if(DrumBusy) {
            error(23);
        }

        if(!err) {
            while(var8 <= 50 && JobTable[var8].JobNo != JobSwpdNo) {
                ++var8;
            }
        }

        if(var8 > 50) {
            error(24);
            var8 = 1;
        }

        if(!err && JobTable[var8].Size != var1) {
            error(25);
        }

        if(!err && Sze == 0) {
            error(36);
        }

        if(!err && JobTable[var8].Overwrite) {
            error(26);
        }

        double var6 = (double)(Sze + CoreAddr - 1);
        int var9;
        if(writ) {
            if(!err && !JobTable[var8].InCore) {
                error(27);
            }

            if(!err && JobTable[var8].Latched) {
                error(28);
            }

            var9 = 1;
            if(!err) {
                while(var9 <= 21 && CoreMap[var9][1] != JobSwpdNo) {
                    ++var9;
                }
            }

            cmi = var9;
            if(cmi > 21) {
                error(29);
                cmi = 1;
            }

            if(!err && CoreAddr != CoreMap[cmi][2]) {
                error(30);
            }

            if(!err && var6 != (double)CoreMap[cmi][3]) {
                error(31);
            }

            if(!err) {
                JobTable[var8].InCore = false;
                double var4 = (double)coreused();
                CoreUtil += var4 * ((double)Clock - LstCrChk);
                LstCrChk = (double)Clock;
                CoreMap[cmi][1] = 0;
                CoreMap[cmi][2] = 0;
                CoreMap[cmi][3] = 0;
                PutCoreMap();
            }
        } else {
            if(!err && JobTable[var8].InCore) {
                error(32);
            }

            if(!err && CoreAddr < 0) {
                error(33);
            }

            if(!err) {
                for(var9 = 1; var9 <= 21; ++var9) {
                    if(CoreMap[var9][1] != 0 && CoreAddr <= CoreMap[var9][3] && var6 >= (double)CoreMap[var9][2]) {
                        error(34);
                    }
                }
            }

            var9 = 1;
            if(!err) {
                while(var9 <= 21 && CoreMap[var9][1] != 0) {
                    ++var9;
                }
            }

            cmi = var9;
            if(cmi > 21) {
                error(35);
            }
        }

        DrumBusy = true;
        DrmStTm = Clock;
        ++DrumTimesPtr;
        Next[3] = DrmTimes[DrumTimesPtr % 10] + Clock;
    }

    static void SnapShot() {
        LastSnap = (double)Clock;
        System.out.println("\n\n\n * * * SYSTEM STATUS AT " + Clock + " * * *");
        System.out.println(" ===================================\n");
        int var1;
        if(Action[0] == 2 && !err13) {
            for(var1 = 1; var1 <= 21 && (Parms[2] != CoreMap[var1][2] || Parms[3] != CoreMap[var1][3] - CoreMap[var1][2] + 1); ++var1) {
                ;
            }

            if(var1 >= 21) {
                error(13);
            }

            if(!err13) {
                System.out.println(" CPU:  job #" + CoreMap[var1][1] + " running");
            }
        } else {
            System.out.println(" CPU:  idle");
        }

        if(!err13) {
            if(DiskBusy) {
                System.out.print(" Disk running for job ");
                System.out.print(JobTable[JobServdIndex].JobNo);
                System.out.println(" since " + DskStTm);
            } else {
                System.out.println(" Disk:  idle");
            }

            if(DrumBusy) {
                System.out.print("Drum:  swapping job " + JobSwpdNo);
                if(writ) {
                    System.out.println(" out since " + DrmStTm);
                } else {
                    System.out.println(" in since " + DrmStTm);
                }
            } else {
                System.out.println(" Drum:  idle");
            }

            int var2 = coreused();
            System.out.println("Memory:  " + var2 + " K words in use");
            System.out.print("Average dilation:  ");
            if(DilCntr == 0.0D) {
                System.out.println("0.00");
            } else {
                printf("%.2f\n", AvgDil / DilCntr);
            }

            System.out.print("Average Response time:  ");
            if((double)TermCntr - DilCntr == 0.0D) {
                System.out.println("0.00");
            } else {
                printf("%.2f\n", AvgResponse / ((double)TermCntr - DilCntr));
            }

            boolean var0 = trace;
            trace = true;
            PutCoreMap();
            trace = var0;
            System.out.println("\n\n\t\t\tJOBTABLE\n");
            System.out.print("Job#  Size  Time CPUTime MaxCPU  I/O\'s ");
            System.out.println("Priority Blocked  Latched InCore Term");
            System.out.print("          Arrived  Used  Time   Pending");
            System.out.println("\n\n");

            for(var1 = 1; var1 <= 50; ++var1) {
                if(!JobTable[var1].Overwrite) {
                    printf("%4d  ", JobTable[var1].JobNo);
                    printf("%3d  ", JobTable[var1].Size);
                    printf("%6.0f ", JobTable[var1].StartTime);
                    printf("%6d ", JobTable[var1].CpuTimeUsed);
                    printf("%6d  ", JobTable[var1].MaxCpuTime);
                    printf("%3d  ", JobTable[var1].IOPending);
                    printf("    %d  ", JobTable[var1].Priority);
                    if(JobTable[var1].Blocked) {
                        System.out.print("     yes");
                    } else {
                        System.out.print("     no ");
                    }

                    if(JobTable[var1].Latched) {
                        System.out.print("     yes");
                    } else {
                        System.out.print("     no ");
                    }

                    if(JobTable[var1].InCore) {
                        System.out.print("     yes");
                    } else {
                        System.out.print("     no ");
                    }

                    if(JobTable[var1].Terminated) {
                        System.out.println("     yes");
                    } else {
                        System.out.println("     no ");
                    }
                }
            }

            System.out.println("\n\n");
            if(Clock != 0) {
                System.out.println("\n\n");
                System.out.print(" Total jobs:  " + JobCntr + "\t");
                System.out.println("terminated:  " + TermCntr);
                printf(" %% utilization   CPU:  %.2f", CpuUtil * 100.0D / (double)Clock);
                printf("   disk:  %.2f", DiskUtil * 100.0D / (double)Clock);
                printf("   drum:  %.2f", DrumUtil * 100.0D / (double)Clock);
                printf("   memory:  %.2f", CoreUtil / (double)Clock);
            }

            System.out.println("\n\n");
        }

    }

    private static void Statistics() {
        System.out.println("\n\n                          FINAL STATISTICS");
        SnapShot();
    }

    private static void printf(String var0, int var1) {
        Format.print(System.out, var0, (long)var1);
    }

    private static void printf(String var0, double var1) {
        Format.print(System.out, var0, var1);
    }
}