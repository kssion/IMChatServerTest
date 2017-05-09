package socket;

/**
 * Created by Chance on 2017/4/23.
 */
public class IMClientThread extends Thread {

    public IMClientThread(Runnable target) {
        super(target);
    }
    @Override
    protected void finalize() {
        System.out.println("~IMClientThread");
    }
}
