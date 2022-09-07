import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String nickname;
    private String password;
    private double debt;
    private boolean authenticated;
    private ArrayList<Server> userservers;

    // novo
    private ReentrantLock lockUser;


    public User(String nick, String pass){
        this.nickname=nick;
        this.password=pass;
        this.debt=0;
        this.authenticated=false;
        this.userservers= new ArrayList<>();
        this.lockUser = new ReentrantLock();
    }

    public String meusServidores(){
        StringBuilder s = new StringBuilder();
        String sAux;

        for (Server b : this.userservers){
                sAux= "Servidor com o id: " + b.getId() + ", comprado no valor de: " + b.getPrecoMax() + ", do tipo: " + b.getTipo() + " | " ;
                s.append(sAux);

        }
        if (userservers.size()==0){
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

        for(Server s : this.userservers){
            if(s.getId().equals(id)){
                this.userservers.remove(s);
                return;
            }
        }
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

        this.userservers.add(s);
    }

    // novo
    public void lock(){
        this.lockUser.lock();
    }
    // novo
    public void unlock(){
        this.lockUser.unlock();
    }
    
}
