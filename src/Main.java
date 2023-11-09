
import java.util.ArrayList;
import java.util.Scanner;



public class Main {
    public static void main(String[] args) {

        Network n = new Network();
    }
}

class Router{
    Semaphore semaphore;
    Router(int maxConnections){
        semaphore = new Semaphore(maxConnections);
    }
    public int connect(){
        return semaphore.acquire();
    }
    public void release(){
        semaphore.release();
    }
}

class Semaphore{
    private int maxConnections;
    private int n;
    Semaphore(int maxConnections){
        this.maxConnections = maxConnections;
        n = 0;
    }

    public int acquire(){
        while(n >= maxConnections){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        n++;
        return (n);
    }

    public void release(){
        n--;
    }
}

class Device extends Thread{
    private String name;
    private String type;
    private Router router;
    private int connectionNum;  

    Device(String name, String type, Router router){
        this.name = name;
        this.type = type;
        this.router = router;
        System.out.printf("(%s) (%s) Arrived\n",name, type);
    }

    @Override
    public void run() {
        connect();
        doActivity();
        disconnect();
    }

    public void connect(){
        connectionNum = router.connect();
        System.out.printf("Connection %d: %s Connected\n",connectionNum,name);
    }

    public void doActivity(){
        System.out.printf("Connection %d: %s Performs Online Activity\n",connectionNum,name);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect(){
        router.release();
        System.out.printf("Connection %d: %s Logged out\n",connectionNum,name);
    }
}

class Network{

    Network() {
        int n, tc;
        ArrayList<Device> devices = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of WI-FI Connections?");
        n = scanner.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        tc = scanner.nextInt();
        Router router = new Router(n);

        for(int i = 0; i < tc; i++){
            String name = scanner.next();
            String type = scanner.next();
            Device device = new Device(name, type, router);
            devices.add(device);
        }
        for (Device device: devices) {
            device.start();

        }
        scanner.close();
    }
}