package alfarezyyd;

public class Process {
  String processName;
  Integer arrivalTime;
  Integer burstTime;
  Integer processPriority;
  Integer waitingTime;

  public Process(String processName, Integer burstTime, Integer arrivalTime) {
    this.processName = processName;
    this.burstTime = burstTime;
    this.arrivalTime = arrivalTime;
    this.processPriority = 0;
    this.waitingTime = 0;
  }

  @Override
  public String toString() {
    return "Process{" +
        "processName='" + processName + '\'' +
        ", arrivalTime=" + arrivalTime +
        ", burstTime=" + burstTime +
        ", processPriority=" + processPriority +
        '}';
  }
}
