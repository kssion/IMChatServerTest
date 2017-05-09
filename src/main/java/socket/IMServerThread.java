package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器线程
 * Created by Chance on 2017/2/10.
 */
class IMServerThread extends Thread {

    private static boolean isRunning = false;

    private ServerSocket serverSocket = null;
    private final int port = 12000;

    public IMServerThread() {


    }

    public void run() {

        if (isRunning) {
            System.err.println("\n##### Error！Socket 服务未关闭！\n");
            return;
        }

        System.err.println("\n##### Socket 服务正在启动..\n");

        try {
            Thread.sleep(3000);

            try {
                this.serverSocket = new ServerSocket(port);
                System.err.println("\n##### *** socket start ***");
            } catch (Exception e) {
                System.err.println("\n##### IMSocketThread 创建 Socket 服务出错！\n");
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.err.println("##### Socket 服务启动完成！" + System.currentTimeMillis() + "\n");

        while (!this.isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                isRunning = true;
                if (null != socket && !socket.isClosed()) {
                    IMClientManager.addSocket(socket);
                }

            } catch (Exception e) {

                isRunning = false;
                try {
                    this.serverSocket.close();
                    System.err.println("\n##### Socket 服务关闭！\n");
                } catch (IOException e1) {
                    System.err.println("\n##### Socket 服务关闭出错！\n");
                    e1.printStackTrace();
                }
                break;
            }
        }
    }

    public void closeSocketServer() {

        isRunning = false;

        try {
            if (null != serverSocket && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.err.println("\n##### *** socket stop ***");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
