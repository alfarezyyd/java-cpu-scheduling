package alfarezyyd;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
  static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

  static LinkedList<Process> linkedListOfProcess = new LinkedList<>();
  static CountDownLatch countDownLatch = new CountDownLatch(1);

  static Scanner userInput = new Scanner(System.in);

  public static void main(String[] args) throws InterruptedException {
    List<Integer> dataUserInput = Helper.insertProcess(userInput, linkedListOfProcess);
    int chosenAlgorithm;
    Integer algorithmType;
    System.out.println("Algoritma CPU Scheduling");
    System.out.println("1. First-Come-First-Serve");
    System.out.println("2. Shortest-Job-First");
    System.out.println("3. Priority Scheduling");
    System.out.println("4. Round Robin");
    System.out.print("Piih Algoritma yang Ingin Digunakan: ");
    chosenAlgorithm = userInput.nextInt();
    linkedListOfProcess.sort(Comparator.comparingInt(theProcess -> theProcess.arrivalTime));
    SchedulingAlgorithm schedulingAlgorithm = new SchedulingAlgorithm(linkedListOfProcess, countDownLatch, scheduledExecutorService, dataUserInput.get(1));
    switch (chosenAlgorithm) {
      case 1 -> schedulingAlgorithm.firstComeFirstServe(dataUserInput.get(0));
      case 2 -> {
        algorithmType = Helper.inputAlgorithmType(userInput);
        switch (algorithmType) {
          case 1 -> schedulingAlgorithm.shortestJobFirst(dataUserInput.get(0), false);
          case 2 -> schedulingAlgorithm.shortestJobFirst(dataUserInput.get(0), true);
        }
      }
      case 3 -> {
        for (Process listOfProcess : linkedListOfProcess) {
          System.out.print("Input Nilai Priority Untuk Process-" + listOfProcess.processName + " : ");
          listOfProcess.processPriority = userInput.nextInt();
          Helper.digestNewLine(userInput);
        }
        algorithmType = Helper.inputAlgorithmType(userInput);
        switch (algorithmType) {
          case 1 -> schedulingAlgorithm.priorityScheduling(dataUserInput.get(0), false);
          case 2 -> schedulingAlgorithm.priorityScheduling(dataUserInput.get(0), true);
        }
      }
      case 4 -> {
        System.out.print("Masukkan Time Quantum yang Diinginkan: ");
        Integer timeQuantum = userInput.nextInt();
        Helper.digestNewLine(userInput);
        algorithmType = Helper.inputAlgorithmType(userInput);
        switch (algorithmType) {
          case 1 -> schedulingAlgorithm.roundRobin(timeQuantum, dataUserInput.get(0), false);
          case 2 -> schedulingAlgorithm.roundRobin(timeQuantum, dataUserInput.get(0), true);
        }
      }
    }
    countDownLatch.await();
    schedulingAlgorithm.countAverageOfWaitingTime();
    scheduledExecutorService.shutdownNow();
  }
}