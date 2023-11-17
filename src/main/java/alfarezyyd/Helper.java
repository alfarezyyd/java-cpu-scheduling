package alfarezyyd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Helper {
  public static void digestNewLine(Scanner userInput) {
    userInput.nextLine();
  }

  public static boolean removeZeroBurstTimeProcess(LinkedList<Process> linkedListOfProcess, Process activeProcess, LinkedList<Integer> linkedListOfWaitingTime) {
    if (activeProcess.burstTime == 0) {
      linkedListOfWaitingTime.add(activeProcess.waitingTime);
      return linkedListOfProcess.remove(activeProcess);
    }
    return false;
  }

  public static List<Integer> insertProcess(Scanner userInput, LinkedList<Process> linkedListOfProcess) {
    Integer numberOfProcess;
    Integer totalOfSumBurstArrivalTime = 0;
    System.out.print("Anda Ingin Input Berapa Banyak Process? ");
    numberOfProcess = userInput.nextInt();
    Helper.digestNewLine(userInput);
    for (int i = 0; i < numberOfProcess; i++) {
      System.out.print("Input Nama Process : ");
      String processName = userInput.nextLine();
      System.out.print("Input Burst Time Process : ");
      Integer processBurstTime = userInput.nextInt();
      totalOfSumBurstArrivalTime += processBurstTime;
      Helper.digestNewLine(userInput);
      System.out.print("Input Arrival Time Process : ");
      Integer processArrivalTime = userInput.nextInt();
      totalOfSumBurstArrivalTime += processArrivalTime;
      Helper.digestNewLine(userInput);
      Process newProcess = new Process(processName, processBurstTime, processArrivalTime);
      linkedListOfProcess.add(newProcess);
    }
    return List.of(totalOfSumBurstArrivalTime, numberOfProcess);
  }

  public static Integer inputAlgorithmType(Scanner userInput) {
    System.out.println("Jenis Algoritma");
    System.out.println("1. Non-Preemptive");
    System.out.println("2. Preemptive");
    System.out.print("Pilih Jenis Algoritma: ");
    return userInput.nextInt();
  }
}
