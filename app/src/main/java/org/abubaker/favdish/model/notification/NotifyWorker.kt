package org.abubaker.favdish.model.notification

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters


// Create a new package as "notification" and a class as NotifyWorker as below.
// Extent the Worker class with required params.
class NotifyWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    // Override the doWork function.
    // This function will be executed when the work scheduler is triggered.
    // You can add your code that you want to execute periodically.
    override fun doWork(): Result {

        Log.i("Notify Worker", "doWork function is called...")

        return success()
    }
}