import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

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
        for (Device d:devices){
            d.start();
        }
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
                temp=i;
                break;
            }
        }
        try {
            FileWriter file = new FileWriter("log.txt",true);
            file.write("Connection "+temp+1+": "+d.name+ " Occupied");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        full.release();
    }

    public void login(Device d){
        int temp=-1;
        for (int i = 0; i < maxNum; i++) {
            if(connections[i].equals(d)){
                temp=i;
                break;
            }
        }
        try {
            FileWriter file = new FileWriter("log.txt",true);
            file.write("Connection "+temp+1+": "+d.name+ " login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void performOnlineActivity(Device d) throws InterruptedException {
        int temp=-1;
        for (int i = 0; i < maxNum; i++) {
            if(connections[i].equals(d)){
                temp=i;
                break;
            }
        }
        if(temp != -1) {
            try {
                FileWriter file = new FileWriter("log.txt", true);
                file.write("Connection " + temp + 1 + ": " + d.name + " performs online activity");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Thread.sleep((long) (Math.random() * 2000));
        }
    }
    public void disConnect(Device d){
        int temp=-1;
        for (int i = 0; i < maxNum; i++) {
            if(connections[i].equals(d)){
                temp=i;
                break;
            }
        }
        try {
            FileWriter file = new FileWriter("log.txt", true);
            file.write("Connection " + temp + 1 + ": " + d.name + "logged out");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                    file.write("("+d.name+")"+"("+d.type+") arrived and waiting");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            try {
                FileWriter file = new FileWriter("log.txt",true);
                file.write("("+d.name+")"+"("+d.type+") arrived");
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        router.connect(this);
        router.login(this);
        try {
            router.performOnlineActivity(this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        router.disConnect(this);
    }
}


