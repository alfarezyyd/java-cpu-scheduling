package alfarezyyd;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SchedulingAlgorithm {
  private Integer secondsNow = 0;
  private final LinkedList<Process> linkedListOfProcess;
  private final CountDownLatch countDownLatch;
  private final ScheduledExecutorService scheduledExecutorService;
  private final Integer numberOfTasks;
  private final LinkedList<Integer> linkedListOfWaitingTimeProcess = new LinkedList<>();

  public SchedulingAlgorithm(LinkedList<Process> linkedListOfProcess, CountDownLatch countDownLatch, ScheduledExecutorService scheduledExecutorService, Integer numberOfTasks) {
    this.linkedListOfProcess = linkedListOfProcess;
    this.countDownLatch = countDownLatch;
    this.scheduledExecutorService = scheduledExecutorService;
    this.numberOfTasks = numberOfTasks;
  }

  public void firstComeFirstServe(Integer totalOfSumBurstArrivalTime) {
    scheduledExecutorService.scheduleAtFixedRate(() -> {
      Helper.removeZeroBurstTimeProcess(linkedListOfProcess, linkedListOfProcess.get(0), linkedListOfWaitingTimeProcess);
      System.out.println("Detik ke-" + secondsNow + " -> ");
      System.out.println("List Process");
      if (secondsNow <= totalOfSumBurstArrivalTime) {
        for (int i = 0; i < linkedListOfProcess.size(); i++) {
          Process currentProcess = linkedListOfProcess.get(i);
          if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
            if (i == 0) {
              System.out.println(currentProcess.processName + " ~ " + currentProcess.burstTime + " ~ " + currentProcess.arrivalTime + " sedang berjalan");
              currentProcess.burstTime--;
            } else {
              linkedListOfProcess.get(i).waitingTime++;
              System.out.println(currentProcess.processName + " ~ " + currentProcess.burstTime + " ~ " + currentProcess.arrivalTime);
            }
          } else {
            break;
          }
        }
      }
      if (linkedListOfProcess.size() == 0) {
        countDownLatch.countDown();
      }
      secondsNow++;
    }, 0, 1, TimeUnit.SECONDS);
  }

  public void shortestJobFirst(Integer totalOfSumBurstArrivalTime, Boolean isPreemptive) {
    if (isPreemptive) {
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        System.out.println("Detik ke-" + secondsNow + " -> ");
        System.out.println("List Process");
        Process shortestProcess = null;
        if (secondsNow <= totalOfSumBurstArrivalTime) {
          for (Process currentProcess : linkedListOfProcess) {
            if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
              if (shortestProcess == null || currentProcess.burstTime < shortestProcess.burstTime) {
                shortestProcess = currentProcess;
              }
            }
          }

          displayListProcess(linkedListOfProcess, shortestProcess);
          if (shortestProcess != null) {
            Helper.removeZeroBurstTimeProcess(linkedListOfProcess, shortestProcess, linkedListOfWaitingTimeProcess);
          }
        }
        if (linkedListOfProcess.size() == 0) {
          countDownLatch.countDown();
        }
        secondsNow++;
      }, 0, 1, TimeUnit.SECONDS);
    } else {
      AtomicReference<Boolean> isProcessLocked = new AtomicReference<>(false);
      AtomicReference<Process> shortestProcess = new AtomicReference<>();
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        System.out.println("Detik ke-" + secondsNow + " -> ");
        System.out.println("List Process");
        if (!isProcessLocked.get()) {
          if (secondsNow <= totalOfSumBurstArrivalTime) {
            for (Process currentProcess : linkedListOfProcess) {
              if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
                shortestProcess.set(currentProcess);
                isProcessLocked.set(true);
              }
            }
          }
        }

        for (Process currentProcess : linkedListOfProcess) {
          if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
            if (currentProcess == shortestProcess.get()) {
              System.out.println(currentProcess.processName + " ~ " + currentProcess.burstTime + " ~ " + currentProcess.arrivalTime + " sedang berjalan");
              currentProcess.burstTime--;
              if (currentProcess.burstTime == 0) {
                isProcessLocked.set(false);
              }
            } else {
              currentProcess.waitingTime++;
              System.out.println(currentProcess.processName + " ~ " + currentProcess.burstTime + " ~ " + currentProcess.arrivalTime);
            }
          }
        }
        Helper.removeZeroBurstTimeProcess(linkedListOfProcess, shortestProcess.get(), linkedListOfWaitingTimeProcess);
        if (linkedListOfProcess.size() == 0) {
          countDownLatch.countDown();
        }
        secondsNow++;
      }, 0, 1, TimeUnit.SECONDS);
    }
  }

  public void priorityScheduling(Integer totalOfSumBurstArrivalTime, Boolean isPreemptive) {
    if (isPreemptive) {
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        Process highestPriorityAndShortestProcess = null;
        System.out.println("Detik ke-" + secondsNow + " -> ");
        System.out.println("List Process");
        if (secondsNow <= totalOfSumBurstArrivalTime) {
          for (Process currentProcess : linkedListOfProcess) {
            if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
              if (highestPriorityAndShortestProcess == null || currentProcess.processPriority < highestPriorityAndShortestProcess.processPriority) {
                highestPriorityAndShortestProcess = currentProcess; // Highest Priority Process
              }
            }
          }

          for (Process currentProcess : linkedListOfProcess) {
            if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
              if (highestPriorityAndShortestProcess != null && Objects.equals(highestPriorityAndShortestProcess.processPriority, currentProcess.processPriority) && currentProcess.burstTime < highestPriorityAndShortestProcess.burstTime) {
                highestPriorityAndShortestProcess = currentProcess; // Highest Priority and Shortest Process
              }
            }
          }

          displayListProcess(linkedListOfProcess, highestPriorityAndShortestProcess);
          if (highestPriorityAndShortestProcess != null) {
            Helper.removeZeroBurstTimeProcess(linkedListOfProcess, highestPriorityAndShortestProcess, linkedListOfWaitingTimeProcess);
          }
        }
        if (linkedListOfProcess.size() == 0) {
          countDownLatch.countDown();
        }
        secondsNow++;
      }, 0, 1, TimeUnit.SECONDS);
    } else {
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        Process highestPriorityProcess = null;
        System.out.println("Detik ke-" + secondsNow + " -> ");
        System.out.println("List Process");
        if (secondsNow <= totalOfSumBurstArrivalTime) {
          for (Process currentProcess : linkedListOfProcess) {
            if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
              if (highestPriorityProcess == null || currentProcess.processPriority < highestPriorityProcess.processPriority) {
                highestPriorityProcess = currentProcess;
              }
            }
          }

          displayListProcess(linkedListOfProcess, highestPriorityProcess);
          if (highestPriorityProcess != null) {
            Helper.removeZeroBurstTimeProcess(linkedListOfProcess, highestPriorityProcess, linkedListOfWaitingTimeProcess);
          }
        }
        if (linkedListOfProcess.size() == 0) {
          countDownLatch.countDown();
        }
        secondsNow++;
      }, 0, 1, TimeUnit.SECONDS);
    }
  }

  public void roundRobin(Integer timeQuantum, Integer totalOfSumBurstArrivalTime, Boolean isPreemptive) {
    if (!isPreemptive) {
      for (var currentProcess : linkedListOfProcess) {
        if (!Objects.equals(currentProcess.arrivalTime, linkedListOfProcess.get(0).arrivalTime)) {
          throw new RuntimeException("Arrival Time Tidak Sama");
        }
      }
      ArrayDeque<Process> processQueue = new ArrayDeque<>(linkedListOfProcess);
      AtomicReference<Process> currentProcess = new AtomicReference<>();
      currentProcess.set(processQueue.poll());
      AtomicReference<Integer> runningTime = new AtomicReference<>(0);
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        if (secondsNow <= totalOfSumBurstArrivalTime) {
          System.out.println("Detik ke-" + secondsNow + " -> ");
          System.out.println("List Process");
          if (secondsNow != 0 && (Objects.equals(timeQuantum, runningTime.get()) || currentProcess.get().burstTime == 0)) {
            if (currentProcess.get().burstTime > 0) {
              processQueue.offerLast(currentProcess.get());
            }
            currentProcess.set(processQueue.poll());
            runningTime.set(0);
          }

          if (currentProcess.get() != null) {
            displayListProcess(linkedListOfProcess, currentProcess.get());
          }

          if (currentProcess.get().burstTime == 0) {
            linkedListOfWaitingTimeProcess.add(currentProcess.get().waitingTime);
          }
        }
        if (processQueue.isEmpty()) {
          countDownLatch.countDown();
        }
        runningTime.getAndSet(runningTime.get() + 1);
        secondsNow++;
      }, 0, timeQuantum, TimeUnit.SECONDS);
    } else {
      AtomicReference<Integer> runningTime = new AtomicReference<>(0);
      AtomicInteger counterCopy = new AtomicInteger();
      LinkedList<Process> linkedListOfProcessNow = new LinkedList<>();
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        if (secondsNow <= totalOfSumBurstArrivalTime) {
          System.out.println("Detik ke-" + secondsNow + " -> ");
          System.out.println("List Process");
          for (int i = counterCopy.get(); i < linkedListOfProcess.size(); i++) {
            if (linkedListOfProcess.get(i).arrivalTime <= secondsNow && linkedListOfProcess.get(i).burstTime > 0) {
              linkedListOfProcessNow.addLast(linkedListOfProcess.get(i));
              counterCopy.getAndIncrement();
            }
          }

          Process currentProcess = linkedListOfProcessNow.getFirst();
          if (Objects.equals(timeQuantum, runningTime.get()) || currentProcess.burstTime == 0) {
            if (currentProcess.burstTime > 0) {
              linkedListOfProcessNow.addLast(currentProcess);
            }
            linkedListOfProcessNow.removeFirst();
            currentProcess = linkedListOfProcessNow.getFirst();
            runningTime.set(0);
          }
          displayListProcess(linkedListOfProcess, currentProcess);
          if (currentProcess.burstTime == 0) {
            runningTime.set(0);
            linkedListOfProcessNow.remove(currentProcess);
            linkedListOfWaitingTimeProcess.add(currentProcess.waitingTime);
          } else {
            runningTime.getAndSet(runningTime.get() + 1);
          }
        }
        secondsNow++;
        if (linkedListOfProcessNow.size() == 0) {
          countDownLatch.countDown();
        }
      }, 0, 1, TimeUnit.SECONDS);
    }
  }

  public void displayListProcess(LinkedList<Process> linkedListOfProcess, Process referenceProcess) {
    for (Process currentProcess : linkedListOfProcess) {
      if (currentProcess.arrivalTime <= secondsNow && currentProcess.burstTime > 0) {
        if (currentProcess == referenceProcess) {
          System.out.println(currentProcess.processName + " ~ " + currentProcess.burstTime + " ~ " + currentProcess.arrivalTime + " ~ " + currentProcess.processPriority + " sedang berjalan");
          currentProcess.burstTime--;
        } else {
          currentProcess.waitingTime++;
          System.out.println(currentProcess.processName + " ~ " + currentProcess.burstTime + " ~ " + currentProcess.arrivalTime + " ~ " + currentProcess.processPriority);
        }
      }
    }
  }

  public void countAverageOfWaitingTime() {
    Integer sumOfWaitingTime = 0;
    for (var waitingTime : linkedListOfWaitingTimeProcess) {
      System.out.println(waitingTime);
      sumOfWaitingTime += waitingTime;
    }
    System.out.println(sumOfWaitingTime);
    System.out.printf("Average Waiting Time is : %.3f", (sumOfWaitingTime.floatValue() / numberOfTasks.floatValue()));
  }
}
