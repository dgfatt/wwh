/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.wwh.opensourceprojectanalysis.handler;

import android.os.*;
import android.os.MessageQueue;
import android.os.Process;
import android.util.Config;
import android.util.Log;
import android.util.Printer;

/**
  * Class used to run a message loop for a thread.  Threads by default do
  * not have a message loop associated with them; to create one, call
  * {@link #prepare} in the thread that is to run the loop, and then
  * {@link #loop} to have it process messages until the loop is stopped.
  * 
  * <p>Most interaction with a message loop is through the
  * {@link android.os.Handler} class.
  * 
  * <p>This is a typical example of the implementation of a Looper thread,
  * using the separation of {@link #prepare} and {@link #loop} to create an
  * initial Handler to communicate with the Looper.
  * 
  * <pre>
  *  class LooperThread extends Thread {
  *      public Handler mHandler;
  *      
  *      public void run() {
  *          Looper.prepare();
  *          
  *          mHandler = new Handler() {
  *              public void handleMessage(Message msg) {
  *                  // process incoming messages here
  *              }
  *          };
  *          
  *          Looper.loop();
  *      }
  *  }</pre>
  *
  *  Looper主要作用：
  *   1、与当前线程绑定，保证一个线程只会有一个Looper实例，同时一个Looper实例也只有一个MessageQueue。
  *   2、loop()方法，不断从MessageQueue中去取消息，交给消息的target属性的dispatchMessage去处理。
 好了，我们的异步消息处理线程已经有了消息队列（MessageQueue），也有了在无限循环体中取出消息的哥们，现在缺的就是发送消息的对象了，于是乎：Handler登场了。
  */
public class Looper {
//    private static final boolean DEBUG = false;
//    private static final boolean localLOGV = DEBUG ? Config.LOGD : Config.LOGV;
//
//    // sThreadLocal.get() will return null unless you've called prepare().
//    private static final ThreadLocal sThreadLocal = new ThreadLocal();
//
//    final android.os.MessageQueue mQueue;
//    volatile boolean mRun;
//    Thread mThread;
//    private Printer mLogging = null;
//    private static Looper mMainLooper = null;
//
//     /** Initialize the current thread as a looper.
//      * This gives you a chance to create handlers that then reference
//      * this looper, before actually starting the loop. Be sure to call
//      * {@link #loop()} after calling this method, and end it by calling
//      * {@link #quit()}.
//      */
//    public static final void prepare() {
//        // 判断了sThreadLocal是否为null，否则抛出异常。这也就说明了Looper.prepare()方法不能被调用两次，同时也保证了一个线程中只有一个Looper实例
//        if (sThreadLocal.get() != null) {
//            throw new RuntimeException("Only one Looper may be created per thread");
//        }
//        // sThreadLocal是一个ThreadLocal对象，可以在一个线程中存储变量
//        // 将一个Looper的实例放入了ThreadLocal
//        sThreadLocal.set(new Looper());
//    }
//
//    /** Initialize the current thread as a looper, marking it as an application's main
//     *  looper. The main looper for your application is created by the Android environment,
//     *  so you should never need to call this function yourself.
//     * {@link #prepare()}
//     */
//
//    public static final void prepareMainLooper() {
//        prepare();
//        setMainLooper(myLooper());
//        if (Process.supportsProcesses()) {
//            myLooper().mQueue.mQuitAllowed = false;
//        }
//    }
//
//    private synchronized static void setMainLooper(Looper looper) {
//        mMainLooper = looper;
//    }
//
//    /** Returns the application's main looper, which lives in the main thread of the application.
//     */
//    public synchronized static final Looper getMainLooper() {
//        return mMainLooper;
//    }
//
//    /**
//     *  Run the message queue in this thread. Be sure to call
//     * {@link #quit()} to end the loop.
//     */
//    public static final void loop() {
//        Looper me = myLooper();
//        // 拿到该looper实例中的mQueue（消息队列）
//        android.os.MessageQueue queue = me.mQueue;
//
//        // Make sure the identity of this thread is that of the local process,
//        // and keep track of what that identity token actually is.
//        Binder.clearCallingIdentity();
//        final long ident = Binder.clearCallingIdentity();
//
//        // 进入了我们所说的无限循环。
//        while (true) {
//            // 取出一条消息，如果没有消息则阻塞。
//            Message msg = queue.next(); // might block
//            //if (!me.mRun) {
//            //    break;
//            //}
//            if (msg != null) {
//                if (msg.target == null) {
//                    // No target is a magic identifier for the quit message.
//                    return;
//                }
//                if (me.mLogging!= null) me.mLogging.println(
//                        ">>>>> Dispatching to " + msg.target + " "
//                        + msg.callback + ": " + msg.what
//                        );
//                // 把消息交给msg的target的dispatchMessage方法去处理。Msg的target是什么呢？其实就是handler对象
//                msg.target.dispatchMessage(msg);
//                if (me.mLogging!= null) me.mLogging.println(
//                        "<<<<< Finished to    " + msg.target + " "
//                        + msg.callback);
//
//                // Make sure that during the course of dispatching the
//                // identity of the thread wasn't corrupted.
//                final long newIdent = Binder.clearCallingIdentity();
//                if (ident != newIdent) {
//                    Log.wtf("Looper", "Thread identity changed from 0x"
//                            + Long.toHexString(ident) + " to 0x"
//                            + Long.toHexString(newIdent) + " while dispatching to "
//                            + msg.target.getClass().getName() + " "
//                            + msg.callback + " what=" + msg.what);
//                }
//
//                // 释放消息占据的资源。
//                msg.recycle();
//            }
//        }
//    }
//
//    /**
//     * Return the Looper object associated with the current thread.  Returns
//     * null if the calling thread is not associated with a Looper.
//     * 方法直接返回了sThreadLocal存储的Looper实例，如果me为null则抛出异常，也就是说looper方法必须在prepare方法之后运行。
//     */
//    public static final Looper myLooper() {
//        return (Looper)sThreadLocal.get();
//    }
//
//    /**
//     * Control logging of messages as they are processed by this Looper.  If
//     * enabled, a log message will be written to <var>printer</var>
//     * at the beginning and ending of each message dispatch, identifying the
//     * target Handler and message contents.
//     *
//     * @param printer A Printer object that will receive log messages, or
//     * null to disable message logging.
//     */
//    public void setMessageLogging(Printer printer) {
//        mLogging = printer;
//    }
//
//    /**
//     * Return the {@link android.os.MessageQueue} object associated with the current
//     * thread.  This must be called from a thread running a Looper, or a
//     * NullPointerException will be thrown.
//     */
//    public static final android.os.MessageQueue myQueue() {
//        return myLooper().mQueue;
//    }
//
//    /**
//     * 在构造方法中，创建了一个MessageQueue（消息队列）。
//     */
//    private Looper() {
//        mQueue = new android.os.MessageQueue();
//        mRun = true;
//        mThread = Thread.currentThread();
//    }
//
//    public void quit() {
//        Message msg = Message.obtain();
//        // NOTE: By enqueueing directly into the message queue, the
//        // message is left with a null target.  This is how we know it is
//        // a quit message.
//        mQueue.enqueueMessage(msg, 0);
//    }
//
//    /**
//     * Return the Thread associated with this Looper.
//     */
//    public Thread getThread() {
//        return mThread;
//    }
//
//    /** @hide */
//    public MessageQueue getQueue() {
//        return mQueue;
//    }
//
//    public void dump(Printer pw, String prefix) {
//        pw.println(prefix + this);
//        pw.println(prefix + "mRun=" + mRun);
//        pw.println(prefix + "mThread=" + mThread);
//        pw.println(prefix + "mQueue=" + ((mQueue != null) ? mQueue : "(null"));
//        if (mQueue != null) {
//            synchronized (mQueue) {
//                long now = SystemClock.uptimeMillis();
//                Message msg = mQueue.mMessages;
//                int n = 0;
//                while (msg != null) {
//                    pw.println(prefix + "  Message " + n + ": " + msg.toString(now));
//                    n++;
//                    msg = msg.next;
//                }
//                pw.println(prefix + "(Total messages: " + n + ")");
//            }
//        }
//    }
//
//    public String toString() {
//        return "Looper{"
//            + Integer.toHexString(System.identityHashCode(this))
//            + "}";
//    }
//
//    static class HandlerException extends Exception {
//
//        HandlerException(Message message, Throwable cause) {
//            super(createMessage(cause), cause);
//        }
//
//        static String createMessage(Throwable cause) {
//            String causeMsg = cause.getMessage();
//            if (causeMsg == null) {
//                causeMsg = cause.toString();
//            }
//            return causeMsg;
//        }
//    }
}

