import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Cloud {

    private ServerSocket socketS;
    private int porto;
    private Hashtable<String,User> users;
    private Hashtable<String, List<Server>> servers;
    private ArrayList<Bid> bids;


    public Cloud (int porto){
        this.porto=porto;
        this.users =new Hashtable<>();
        this.servers = new Hashtable<>();
        this.bids = new ArrayList<>();
    }

    public Hashtable<String, User> getUsers() {
        return users;
    }



    public synchronized boolean registerUser(String nick, String pass) {
        if(users.containsKey(nick)){
            System.out.println("Utilizador " + nick + " já existente");
            return false;
        }
        this.users.put(nick,new User(nick,pass));
        return true;
    }

    public User loginIn (String nick){
        User us=this.users.get(nick);

        synchronized (us){
                us.setAuthenticatedIn();
            }


        return us;
    }

    public int testeUser(String nick, String pass){
        if(!(this.users.containsKey(nick))){
            System.out.println("Utilizador não existe");
            return 0; //não existe
        }
        if(!this.users.get(nick).passwordCheck(pass)){
            System.out.println("Password errada");
            return 1; // password errada
        }
        User us=this.users.get(nick);
        synchronized (us) {
            if (us.isAuthenticated()) {
                System.out.println("Já autenticado");
                return 2;
            }
        }
        return 3;
    }

    public void comandoInvalido(){
        System.out.println("Comando inválido, tente de novo.");
    }

    public String minhasBids(String nick){
        StringBuilder s = new StringBuilder();
        String sAux;
        int flag=0;
        for (Bid b : this.bids){
            if (b.getNick().equals(nick)){
                sAux= "Bid no valor de " + b.getBid() + " nos servidores do tipo " + b.getTipoServer() + " | " ;
                s.append(sAux);
                flag++;
            }
        }
        if (flag==0){
            sAux = "Sem bids para apresentar";
            s.append(sAux);
        }
        return s.toString();
    }

    public synchronized double getPreco(String tipo){
        double preco = this.servers.get(tipo).get(0).getPrecoMax();
        return preco;
    }

    public synchronized void addBid(double bid, String nick, String tipo){
        Bid b = new Bid(bid,nick,tipo);
        this.bids.add(b);
    }

    public synchronized Server haDisponivel (String tipo){

        List<Server> listServ =  this.servers.get(tipo);
        for(Server c : listServ){

            if(c.getEstado().equals("livre")){
                c.setEstado("ocupado");
                return c;
            }

        }

        for(Server c : this.servers.get(tipo)){
            if(c.getEstado().equals("leilao")){
                c.setEstado("ocupado");
                return c;
            }

        }
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
            e.getMessage();
        }
    }


    public List<Server> getServers(String tipo){
        return this.servers.get(tipo);
    }

    public static void main(String []args){
        Cloud c= new Cloud(12345);
        Server s = new Server(1,"id1","large",10,"livre");
        Server s3 = new Server(1,"id2","small",10,"livre");
        Server s2 = new Server(1,"id1","medium",999,"livre");
        List<Server> novo = new ArrayList<>();
        List<Server> novo2 = new ArrayList<>();
        List<Server> novo3 = new ArrayList<>();
        novo.add(s);
        novo3.add(s3);
        novo2.add(s2);
        c.servers.put(s.getTipo(),novo);

        c.servers.put(s3.getTipo(),novo3);
        c.servers.put(s2.getTipo(),novo2);

        c.startCloud();
    }
}
