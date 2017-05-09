package socket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Auth: Chance
 * Created by Chance on 2017/3/14.
 */
public class IMClientManager {

    private static final HashMap<String, IMClient> clientsMap = new HashMap<>();
    private static final ArrayList<IMClient> reusingMap = new ArrayList<>(); // 可复用的客户端处理程序

    // 客户进入 添加
    public static void addSocket(Socket s) {
        IMClient client;
        System.out.println("addSocket");
        synchronized (reusingMap) {
            if (reusingMap.isEmpty()) {
                client = new IMClient();
                System.out.println("新建Client:" + client.hashCode());
            } else {
                client = reusingMap.remove(0);
                System.out.println("存在可重用Client:" + client.hashCode());
            }
        }
        client.setSocket(s);
        IMClientThread thread = new IMClientThread(client);
        client.thread = thread;
        thread.start();
    }

    // 获取客户端
    public static IMClient getClient(String unid) {
        synchronized (clientsMap) {
            if (null != unid) {
                return clientsMap.get(unid);
            }
            return null;
        }
    }

    // 登录成功 添加到列表
    public static void addClient(IMClient client) {
        synchronized (clientsMap) {
            if (clientsMap.containsKey(client.getUnid())) {
                IMClient c = clientsMap.get(client.getUnid());
                c.setOffline();
                System.out.println("强制已登录用户" + client.getUnid() + "离线");
            }
            clientsMap.put(client.getUnid(), client);
        }
    }

    // 退出或离线 移除 添加到复用列表
    public static void removeClient(IMClient client) {
        synchronized (clientsMap) {
            if (null != client) {
                if (null != client.getUnid() && clientsMap.containsKey(client.getUnid())) {
                    System.out.println("移除Client");
                    clientsMap.remove(client.getUnid());
                }
                reusingMap.add(client);
                System.out.println("添加Client到重用池");
            }
        }
    }

}
