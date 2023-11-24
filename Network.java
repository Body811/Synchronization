import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Network{
    public static void main(String[] args){
        int maxNum,tc;
        Scanner obj=new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        maxNum=obj.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        tc=obj.nextInt();
        String name,type;
        Router router=new Router(maxNum);
        Vector<Device> devices = new Vector<>();
        for (int i = 0; i < tc; i++) {
            System.out.println("Enter the name and type of each device seperated by space");
            name=obj.next();
            type=obj.next();
            Device d=new Device(name,type,router);
            devices.add(d);
        }
        for (int i = 0; i < tc; i++) {
            devices.get(i).start();
        }
        obj.close();
    }
}
class Router {
    int maxNum;
    Semaphore full;
    Semaphore empty;
    Device connections[];
    public Router(int maxNum){
        this.maxNum=maxNum;
        connections= new Device[maxNum];
        for (int i = 0; i < maxNum; i++) {
            connections[i]=new Device("0","0",this);
        }
        full = new Semaphore(0);
        empty=new Semaphore(maxNum);
    }

    public void connect(Device d){
        empty.acquire(d,true);
        int temp= -1;
        for (int i = 0; i < maxNum; i++) {
            if(connections[i].name.equals("0")){
                connections[i] =d;
                temp=i+1;
                break;
            }
        }
        try {
            FileWriter file = new FileWriter("log.txt",true);
            file.write("Connection "+temp+": "+d.name+ " Occupied\n");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        full.release();
    }

    public void login(Device d){
        int temp=-1;
        for (int i = 0; i < maxNum; i++) {
            if(connections[i].equals(d)){
                temp=i+1;
                break;
            }
        }
        try {
            FileWriter file = new FileWriter("log.txt",true);
            file.write("Connection "+temp+": "+d.name+ " login\n");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void performOnlineActivity(int t,Device d){
        try {
            int temp=-1;
            for (int i = 0; i < maxNum; i++) {
                if(connections[i].equals(d)){
                    temp=i+1;
                    break;
                }
            }
                try {
                    FileWriter file = new FileWriter("log.txt", true);
                    file.write("Connection " + temp + ": " + d.name + " performs online activity\n");
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TimeUnit.SECONDS.sleep(t);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void disConnect(Device d){
        int temp=-1;
        for (int i = 0; i < maxNum; i++) {
            if(connections[i].equals(d)){
                connections[i] =new Device("0","0",this);
                temp=i+1;
                break;
            }
        }
        try {
            FileWriter file = new FileWriter("log.txt", true);
            file.write("Connection " + temp + ": " + d.name + " logged out\n");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        empty.release();
    }
}

class Semaphore{
    int n;
    Semaphore(int n){
        this.n=n;
    }
    public synchronized void acquire(Device d,boolean connect){
        n--;
        if(n<0){
            if(connect && !d.getName().equals("0")){
                try {
                    FileWriter file = new FileWriter("log.txt",true);
                    file.write("("+d.name+")"+"("+d.type+") arrived and waiting\n");
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else {
            try {
                FileWriter file = new FileWriter("log.txt",true);
                file.write("("+d.name+")"+"("+d.type+") arrived\n");
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void release(){
        n++;
        if(n <= 0)
            notify();
    }
}
class Device extends Thread{
    String name;
    String type;
    Router router;
    Device(String name,String type,Router router){
        this.name=name;
        this.type=type;
        this.router=router;
    }

    public void run(){
        Random r=new Random();
        router.connect(this);
        router.login(this);
        router.performOnlineActivity(r.nextInt(10),this);
        router.disConnect(this);
    }
}


