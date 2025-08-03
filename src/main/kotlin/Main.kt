import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    print("Enter the path to the program: ")
    val filepath = readLine()  // returns a nullable String
    println("Hello, $filepath!")

    val executor = Executors.newSingleThreadScheduledExecutor()

    val runnable = Runnable {
        println("Hello!")
    }

    val cpuFuture = executor.scheduleAtFixedRate(
        runnable,
        0,
        1000L / 500L, // repeat frequency - every 2 ms
        TimeUnit.MILLISECONDS
    )

    // to stop and interrupt a future
    cpuFuture?.cancel(true)

    // to wait for all futures to finish
    try {
        cpuFuture.get() // waits for future to finish or be cancelled - blocks current thread execution (repeating futures will still run)
    } catch (_: Exception) {
        executor.shutdown() // turns off the executor allowing the program to terminate when the end is reached
    }
}

