package socket;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chance on 2017/2/18.
 */
public class IMMessageManager {
    private static ConcurrentHashMap<String, LinkedList<String>> msgArray = new ConcurrentHashMap<>();

    /**
     * 添加离线消息
     * @param unid unid
     * @param msg msg
     */
    public static void addOfflineMessage(String unid, String msg) {
        LinkedList<String> msgs = (LinkedList<String>)getOfflineMessage(unid);
        if (null == msgs) {
            msgs = new LinkedList<>();
            msgArray.put(unid, msgs);
        }
        msgs.addLast(msg);
        System.out.println("添加离线消息：" + unid + ":" + msg);
    }
    public static void removeOfflineMessage(String unid) {
        if (null != unid && msgArray.containsKey(unid)) {
            msgArray.remove(unid);
            System.out.println("移除离线消息：" + unid);
        }
    }
    public static List<String> getOfflineMessage(String unid) {
        if (null != unid && msgArray.containsKey(unid)) {
            System.out.println("获取离线消息列表：" + unid);
            return msgArray.get(unid);
        }
        return null;
    }

}
