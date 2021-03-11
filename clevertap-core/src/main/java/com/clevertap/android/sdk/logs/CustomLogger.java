package com.clevertap.android.sdk.logs;

import com.elvishew.xlog.BuildConfig;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;

import android.app.Application;
import android.os.Environment;

import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

import java.io.File;


public class CustomLogger {

    public static Printer globalFilePrinter;

    private static final long MAX_TIME = 1000 * 60 * 60 * 24 * 2; //

    public static void initXLog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)
                .build();

        Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log
        Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
                .Builder(new File(Environment.getExternalStorageDirectory(), "xlogsample").getPath())       // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                .flattener(new ClassicFlattener())                     // Default: DefaultFlattener
                .build();

        XLog.init(                                                 // Initialize XLog
                config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
                filePrinter);

        // For future usage: partial usage in MainActivity.
        globalFilePrinter = filePrinter;
    }

}
