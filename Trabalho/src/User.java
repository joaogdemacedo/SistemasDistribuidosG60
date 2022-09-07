import java.util.ArrayList;

public class User {
    private String nickname;
    private String password;
    private double debt;
    private boolean authenticated;
    private ArrayList<Server> userservers;




    public User(String nick, String pass){
        this.nickname=nick;
        this.password=pass;
        this.debt=0;
        this.authenticated=false;
        this.userservers= new ArrayList<>();
    }

    public String meusServidores(){
        StringBuilder s = new StringBuilder();
        String sAux;

        for (Server b : this.userservers){
                sAux= "Servidor com o id: " + b.getId() + ", comprado no valor de: " + b.getPrecoMax() + ", do tipo: " + b.getTipo() + " | " ;
                s.append(sAux);

        }
        if (userservers.size()==0){
            sAux = "Sem servidores associados";
            s.append(sAux);
        }
        return s.toString();
    }

    public String getNickname() {
        return this.nickname;
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

    public String getDadosPessoais(){
        String res=null;
        double i = getDebt();
        res=String.valueOf(i);
        res = res.concat("\n");
        for(Server c : userservers){
            res=res.concat(c.getId() +"\n");
        }
        return res;
    }

    public void setAuthenticatedOut(){
        this.authenticated=false;
    }

    public ArrayList<Server> getUserservers() {
        return userservers;
    }
    public void adServer(Server s){

        this.userservers.add(s);
    }

    
}
