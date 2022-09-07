import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Cloud {

    private ServerSocket socketS;
    private int porto;
    private Hashtable<String,User> users;
    private Hashtable<String, List<Server>> servers;
    private ArrayList<Bid> bids;

    // Controlo de concorrência
    private ReentrantLock lockCloud;
    private Condition noServers;


  
  public Cloud (int porto){
        this.porto=porto;
        this.users =new Hashtable<>();
        this.servers = new Hashtable<>();
        this.bids = new ArrayList<>();
        this.lockCloud = new ReentrantLock();
        this.noServers = lockCloud.newCondition();
    }


    public Hashtable<String, User> getUsers() {
        return users;
    }

    public boolean registerUser(String nick, String pass) {
        lockCloud.lock();

        if(users.containsKey(nick)){
            System.out.println("Utilizador " + nick + " já existente");
            lockCloud.unlock();
            return false;
        }

        this.users.put(nick,new User(nick,pass));
        lockCloud.unlock();
        return true;
    }


    public User loginIn (String nick){
        lockCloud.lock();

        User us=this.users.get(nick);
        us.lock();

        lockCloud.unlock();

        us.setAuthenticatedIn();

        us.unlock();
        return us;
    }

    public void removerBid(int id){

        lockCloud.lock();

        for(Bid b : this.bids){
            if(b.getId() == id){
                this.bids.remove(b);
                break;
            }
        }

        lockCloud.unlock();
    }


    public int testeUser(String nick, String pass){
        lockCloud.lock();
        if(!this.users.containsKey(nick)){
            System.out.println("Utilizador não existe");
            lockCloud.unlock();
            return 0;
        }
        if(!this.users.get(nick).passwordCheck(pass)){
            System.out.println("Password errada");
            lockCloud.unlock();
            return 1;
        }

        User us = this.users.get(nick);
        us.lock();

        lockCloud.unlock();

        if (us.isAuthenticated()) {
               System.out.println("Já autenticado");
               us.unlock();
               return 2;
        }

        us.unlock();
        return 3;
    }

    public String minhasBids(String nick){
        StringBuilder s = new StringBuilder();
        String sAux;
        int flag=0;
        for (Bid b : this.bids){
            if (b.getNick().equals(nick)){
                sAux= "Bid no valor de " + b.getBidValue() + " nos servidores do tipo " + b.getTipoServer() + " com id = " + b.getId()+" | " ;
                s.append(sAux);
                flag++;
            }
        }
        if (flag==0){
            sAux = "Nada a apresentar.";
            s.append(sAux);
        }
        return s.toString();
    }


    public double getPreco(String tipo){
        lockCloud.lock();

        Server serv = this.servers.get(tipo).get(0);

        serv.lock();
        lockCloud.unlock();

        double preco = serv.getPrecoMax();

        serv.unlock();
        return preco;
    }

    public void addBid(double bid, String nick, String tipo, int id){
        lockCloud.lock();

        Bid b = new Bid(bid,nick,tipo,id);
        this.bids.add(b);

        lockCloud.unlock();
    }


    public void libertarServidor(String id, String tipo){
        lockCloud.lock();

        List<Server> listServ =  this.servers.get(tipo);

        for(Server c : listServ){
            if(c.getId().equals(id)) {

                c.setEstado("livre");
                this.noServers.signal();

                this.lockCloud.unlock();
                return;
            }
        }

        lockCloud.unlock();
        return;
    }


    // Acho que deviamos fazer uma haDisponivel para cada tipo de servidor
    public Server haDisponivel (String tipo) throws InterruptedException {

        lockCloud.lock();

        int flag=0;
        List<Server> listServ =  this.servers.get(tipo);

        for(Server c : listServ){
            if(c.getEstado().equals("livre")){
                c.setEstado("ocupado");
                flag=1;
                lockCloud.unlock();
                return c;
            }
        }

        for(Server c : this.servers.get(tipo)){
            if(c.getEstado().equals("leilao")){
                c.setEstado("ocupado");
                flag=1;
                lockCloud.unlock();
                return c;
            }
        }

        while(flag==0){
            System.out.println("Sou a thread " + Thread.currentThread().getName() + " e estou a dormir");
            this.noServers.await();
            flag=1;
        }
        System.out.println("Sou a thread " + Thread.currentThread().getName()+ " e acordei");
        //flag=0;


        for(Server c : listServ){
            if(c.getEstado().equals("livre")){
                c.setEstado("ocupado");
                lockCloud.unlock();
                return c;
            }
        }

        for(Server c : this.servers.get(tipo)){
            if(c.getEstado().equals("leilao")){
                c.setEstado("ocupado");
                lockCloud.unlock();
                return c;
            }
        }

        lockCloud.unlock();
        return null;
    }




    public void startCloud(){

        try{
            System.out.println("Cloud");

            this.socketS = new ServerSocket(this.porto);

            while(true) {
                Socket soc = socketS.accept();
                Serverworker sw = new Serverworker(this, soc);
                Thread th = new Thread(sw);
                th.start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String []args){
        Cloud c= new Cloud(2323);
        Server s = new Server(2,"id1","large",10,"livre");
        Server s3 = new Server(0,"id2","small",10,"livre");
        Server s2 = new Server(0,"id3","medium",999,"livre");

        Server s4 = new Server(10,"id4","large",10,"ocupado");
        Server s5 = new Server(0,"id5","small",10,"livre");
        Server s6 = new Server(0,"id6","medium",999,"livre");

        Server s7 = new Server(10,"id7","large",10,"ocupado");
        Server s8 = new Server(0,"id8","small",10,"livre");
        Server s9 = new Server(500,"id9","medium",999,"leilao");

        Server s10 = new Server(0,"id10","large",10,"ocupado");
        Server s11 = new Server(10,"id11","large",10,"ocupado");
        Server s12 = new Server(0,"id12","medium",999,"livre");

        List<Server> large = new ArrayList<>();
        List<Server> medium = new ArrayList<>();
        List<Server> small = new ArrayList<>();
        large.add(s);
        small.add(s3);
        medium.add(s2);
        large.add(s4);
        small.add(s5);
        medium.add(s6);
        large.add(s7);
        small.add(s8);
        medium.add(s9);
        large.add(s10);
        large.add(s11);
        medium.add(s12);
        c.servers.put(s.getTipo(),large);

        c.servers.put(s2.getTipo(),medium);
        c.servers.put(s3.getTipo(),small);

        c.startCloud();
    }
}
