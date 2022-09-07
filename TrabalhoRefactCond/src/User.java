import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class User implements Serializable {


    private String nickname;
    private String password;
    private double debt;
    private boolean authenticated;
    private ArrayList<Server> userservers;
    private ArrayList<Bid> minhasbids;
    private String relatorio;

    private ReentrantLock lockUser;


    public User(String nick, String pass){
        this.nickname=nick;
        this.password=pass;
        this.debt=0;
        this.authenticated=false;
        this.userservers= new ArrayList<>();
        this.minhasbids= new ArrayList<>();
        this.lockUser = new ReentrantLock();
    }

    public String meusServidores(){
        lockUser.lock();
        StringBuilder s = new StringBuilder();
        String sAux;

        for (Server b : this.userservers){
                sAux= "Servidor com o id: " + b.getId() + ", do tipo: " + b.getTipo() + " | " ;
                s.append(sAux);

        }
        if (userservers.size()==0){
            sAux = "Nada a apresentar.";
            s.append(sAux);
        }
        lockUser.unlock();
        return s.toString();
    }

    public String asBids(){
        lockUser.lock();
        StringBuilder s = new StringBuilder();
        String sAux;
        int flag=0;
        for (Bid b : this.minhasbids){

            sAux= "Bid no valor de " + b.getBidValue() + " nos servidores do tipo " + b.getTipoServer() + " com id = " + b.getId()+" | " ;
            s.append(sAux);
            flag++;

        }

        lockUser.unlock();
        if (flag==0){
            sAux = "Nada a apresentar.";
            s.append(sAux);
        }

        return s.toString();
    }

    public String getNickname() {
        return this.nickname;
    }

    public void addDeb(double d){
        this.debt+=d;
    }

    public void eliminarServidor(String id){
    lockUser.lock();
        for(Server s : this.userservers){
            if(s.getId().equals(id)){
                this.userservers.remove(s);
                lockUser.unlock();
                return;
            }
        }
        lockUser.unlock();

    }

    public double getDebt() {
        return this.debt;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean passwordCheck(String pass){
        return pass.equals(this.password);
    }

    public void setAuthenticatedIn(){
        this.authenticated=true;
    }

    public void setDebt(double valor){
        this.debt-=valor;
    }

    public void setAuthenticatedOut(){
        this.authenticated=false;
    }

    public void adServer(Server s){
        lockUser.lock();
        this.userservers.add(s);
        lockUser.unlock();
    }

    public void adBid(Bid s){
        lockUser.lock();
        this.minhasbids.add(s);
        lockUser.unlock();
    }

    public void lock(){
        this.lockUser.lock();
    }
 
    public void unlock(){ this.lockUser.unlock(); }

    public String getRelatorio() {
        return relatorio;
    }

    public synchronized void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }

    public double getCBid(int x){
        double a=0;
        for(Bid c : this.minhasbids){
            if(x==c.getId()){
                a =c.getBid();

                return a;
            }
        }
        return a;
    }

    public void removeBid(int x){

        for(Bid c : this.minhasbids){
            if(x==c.getId()){
                this.minhasbids.remove(c);

                break;
            }
        }

    }
}
