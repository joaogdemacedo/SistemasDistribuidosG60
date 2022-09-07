import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class Dados implements Serializable {

    private Hashtable<String,User> users;
    private Hashtable<String, List<Server>> servers;
    private Hashtable<String,List<User>> pedidos;
    private Hashtable<String,Condition> use;
    private Hashtable<String, List<Bid>> bids;
    private Hashtable<Integer, Condition> licitacoes;
    private ReentrantLock lockCloud;


    public Dados (){
        this.users =new Hashtable<>();
        this.servers = new Hashtable<>();
        this.lockCloud = new ReentrantLock();
        this.pedidos = new Hashtable<>();
        this.use = new Hashtable<>();
        this.bids = new Hashtable<>();
        this.licitacoes = new Hashtable<>();
    }

    public synchronized Hashtable<String, List<Server>> getServers() {
        return servers;
    }

    public synchronized void adicionarServidores(String tipo, List<Server> servidores ){
        this.servers.put(tipo,servidores);
    }

    public synchronized Hashtable<String, User> getUsers() {
        return users;
    }

    public void removBid(int x, String tipo){
        lockCloud.lock();
        for(Bid c : this.bids.get(tipo)){
            if(c.getId()==x){
                this.bids.get(tipo).remove(c);

                this.licitacoes.get(x).signal();
                lockCloud.unlock();
                break;
            }
        }
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

        us.setAuthenticatedIn();

        lockCloud.unlock();
        return us;
    }

    public int testeUser(String nick, String pass){
        lockCloud.lock();
        if(!(this.users.containsKey(nick))){
            System.out.println("Utilizador não existe");
            lockCloud.unlock();
            return 0; //não existe
        }
        if(!this.users.get(nick).passwordCheck(pass)){
            System.out.println("Password errada");
            lockCloud.unlock();
            return 1; // password errada
        }

        User us=this.users.get(nick);
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

    public double getPreco(String tipo){
        lockCloud.lock();

        Server serv = this.servers.get(tipo).get(0);

        serv.lock();
        lockCloud.unlock();

        double preco = serv.getPrecoMax();

        serv.unlock();
        return preco;
    }

    public void libertarServidor(String id, String tipo){
        lockCloud.lock();

        List<Server> listServ =  this.servers.get(tipo);

        for(Server c : listServ){
            if(c.getId().equals(id)) {
                c.setUser("");
                c.setEstado("livre");
                if(this.pedidos.containsKey(tipo)) {
                    if (!this.pedidos.get(tipo).isEmpty()) {
                        String nickk = this.pedidos.get(tipo).get(0).getNickname();
                        this.use.get(nickk).signal();
                        this.pedidos.get(tipo).remove(0);
                    }
                }
                else {
                    if(this.bids.containsKey(tipo)) {
                        if (!this.bids.get(tipo).isEmpty()) {
                            int ids = idBidMaior(tipo);
                            this.licitacoes.get(ids).signal();
                            for (Bid s : this.bids.get(tipo)) {
                                if (s.getId() == ids) {
                                    this.bids.get(tipo).remove(s);
                                    break;
                                }

                            }
                        }
                    }
                }

                lockCloud.unlock();
                return;
            }
        }
        lockCloud.unlock();
        return;
    }

    public Server servidoresLivres( List<Server> listServ){
        lockCloud.lock();
        for(Server c : listServ) {
            if (c.getEstado().equals("livre")) {
                lockCloud.unlock();
                return c;
            }
        }
        lockCloud.unlock();
        return null;
    }

    public Server servidoresLeilao( List<Server> listServ){
        lockCloud.lock();
        for(Server c : listServ){
            if(c.getEstado().equals("leilao")){
                lockCloud.unlock();
                return c;
            }
        }
        lockCloud.unlock();
        return null;
    }

    public  int idBidMaior(String tipo){
        lockCloud.lock();
        double as=0;
        int id=-1;

      for(Bid a: this.bids.get(tipo)){
          if(as<a.getBidValue()) {
              as = a.getBidValue();
              id=a.getId();
          }
      }
      lockCloud.unlock();
      return id;
    }

    // reservar a leilao
    public boolean haLeilao (String tipo, String nick, double bid) {
        lockCloud.lock();
        List<Server> listServ =  this.servers.get(tipo);
        Server livres=servidoresLivres(listServ);

        if(livres!=null) {
            livres.setUser(nick);
            livres.setEstado("leilao");
            this.users.get(nick).adServer(livres);
            this.users.get(nick).setDebt(bid);
            lockCloud.unlock();
            return true;
        }

        List<Bid> currentUsers;
        if (this.bids.containsKey(tipo)) {
            currentUsers = this.bids.get(tipo);
        } else {
            currentUsers = new ArrayList<>();
        }
        Bid b= new Bid(bid, nick,tipo);

        currentUsers.add(b);
        this.bids.put(tipo, currentUsers);
        this.getUsers().get(nick).adBid(b);

        this.licitacoes.put(b.getId(), this.lockCloud.newCondition());
        Leilaoworker sw= new Leilaoworker(this, nick, tipo,b.getId(),this.licitacoes.get(b.getId()),lockCloud,bid);
        Thread as = new Thread(sw);
        as.start();

        lockCloud.unlock();
        return false;
    }

    // reservar a pedido
    public boolean haDisponivel (String tipo, String nick) {
        lockCloud.lock();

        List<Server> listServ = this.servers.get(tipo);
        Server livres = servidoresLivres(listServ);
        Server leilao = servidoresLeilao(listServ);

        if (livres != null) {
            livres.setUser(nick);
            livres.setEstado("ocupado");
            this.users.get(nick).adServer(livres);
            this.users.get(nick).setDebt(getPreco(livres.getTipo()));
            lockCloud.unlock();
            return true;
        }
        if (leilao != null) {
            String oldNick = leilao.getUser();
            this.users.get(oldNick).eliminarServidor(leilao.getId());
            this.users.get(oldNick).setRelatorio("Ficou sem o servidor " + leilao.getId());
            this.users.get(nick).adServer(leilao);
            this.users.get(nick).setDebt(getPreco(leilao.getTipo()));
            leilao.setEstado("ocupado");
            lockCloud.unlock();
            return true;
        }

        List<User> currentUsers;
        if (this.pedidos.containsKey(tipo)) {
            currentUsers = this.pedidos.get(tipo);
        } else {
            currentUsers = new ArrayList<>();
        }
        currentUsers.add(this.users.get(nick));
        this.pedidos.put(tipo, currentUsers);
        this.use.put(nick, this.lockCloud.newCondition());
        PedidoWorker pW = new PedidoWorker(this.use.get(nick),this,tipo,nick, lockCloud);
        Thread t = new Thread(pW);
        t.start();
        lockCloud.unlock();
            return false;
        }

    public synchronized void grava() throws IOException {
        FileOutputStream fileOut = new FileOutputStream("Dados.txt");
        ObjectOutputStream oos = new ObjectOutputStream(fileOut);
        oos.writeObject(this);
        oos.close();
        fileOut.close();
    }
}

